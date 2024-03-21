package com.wust.ucms.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.code.kaptcha.Producer;
import com.google.zxing.WriterException;
import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.*;
import com.wust.ucms.service.impl.LogServiceImpl;
import com.wust.ucms.service.impl.LoginInfoServiceImpl;
import com.wust.ucms.service.impl.PermissionServiceImpl;
import com.wust.ucms.service.impl.UserRoleServiceImpl;
import com.wust.ucms.utils.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LoginController {

    @Autowired
    LoginInfoServiceImpl login;

    @Autowired
    UserRoleServiceImpl role;

    @Autowired
    PermissionServiceImpl permission;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    Producer captcha;

    @Autowired
    RedisCache redis;

    @Autowired
    SendEmailUtil sendEmailUtil;

    @Autowired
    RSAKeyProperties rsaKeyProperties;

    @Autowired
    LogServiceImpl log;

    @PostMapping("/register")
    public Result register(@RequestBody LoginInfo loginInfo) {
        try {
            if (!StringUtils.hasText(loginInfo.getRealName()) ||
                    !StringUtils.hasText(loginInfo.getPassword()) ||
                    !StringUtils.hasText(loginInfo.getPhone()) ||
                    !StringUtils.hasText(loginInfo.getEmail())
            ) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return new Result(-20001);
        }

        try {
            if (loginInfo.getRealName().length() > 36 ||
                    loginInfo.getPhone().length() != 11 ||
                    loginInfo.getEmail().length() > 36
            ) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return new Result(-20002);
        }

        try {
            if ((StringUtils.hasText(loginInfo.getStudentNumber()) &&
                    StringUtils.hasText(loginInfo.getTeacherNumber())) ||
                    (!StringUtils.hasText(loginInfo.getStudentNumber()) &&
                            !StringUtils.hasText(loginInfo.getTeacherNumber()))) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        JSONObject o = redis.getCacheObject(loginInfo.getConnectionId());
        VerifyInfo verifyInfo = o.toJavaObject(VerifyInfo.class);

        if (!Objects.equals(verifyInfo.getCaptcha().getCode(), loginInfo.getVerifyCode())) return new Result(-20207);
        loginInfo.setPassword(passwordEncoder.encode(loginInfo.getPassword()));
        loginInfo.setIsDelete(0);

        Integer flag = login.createUser(loginInfo);
        if (flag <= 0) return new Result(flag);

        return new Result(0);
    }

    @GetMapping("/captcha")
    public Result captchaImage(@RequestParam String connectionId, HttpServletResponse response) throws IOException {
        if (!redis.hasKey(connectionId)) throw new RuntimeException();

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        String capText = captcha.createText();
        VerifyInfo verifyInfo = new VerifyInfo();
        verifyInfo.getCaptcha().setCode(capText);
        redis.setCacheObject(connectionId, verifyInfo);

        BufferedImage bufferedImage = captcha.createImage(capText);
        ServletOutputStream out = response.getOutputStream();

        try (out) {
            ImageIO.write(bufferedImage, "jpg", out);
            out.flush();
        }

        return new Result(0);
    }

    @PostMapping("/verify")
    @Transactional
    public Result verify(@RequestBody LoginInfo loginInfo) {
        if (loginInfo == null) return new Result(-20000);

        String email = loginInfo.getEmail();
        String connectionId = loginInfo.getConnectionId();
        if (!StringUtils.hasText(email) || !StringUtils.hasText(connectionId)) return new Result(-20001);

        String code;
        JSONObject o = redis.getCacheObject(connectionId);
        VerifyInfo verifyInformation = o.toJavaObject(VerifyInfo.class);
        if (verifyInformation == null || verifyInformation.getEmail().getCode() == null) {
            VerifyInfo verifyInfo = new VerifyInfo();
            code = CodeUtil.generateVerifyCode();
            Long now = login.selectDateFromSQL();
            verifyInfo.getEmail().setCode(code);
            verifyInfo.getEmail().setSaveTime(now);
            verifyInfo.getEmail().setUpdateTime(now);
            redis.setCacheObject(connectionId, verifyInfo);
        } else {
            o = redis.getCacheObject(connectionId);
            VerifyInfo verifyInfo = o.toJavaObject(VerifyInfo.class);
            code = verifyInfo.getEmail().getCode();
            Long now = login.selectDateFromSQL();
            Long saveTime = verifyInfo.getEmail().getSaveTime();
            Long updateTime = verifyInfo.getEmail().getUpdateTime();
            if (now-saveTime <= 0 || now-updateTime <= 0) return new Result(-20004);
            if ((now-saveTime)/1000 <= 60) return new Result(-20702);
            if ((now-saveTime)/1000 <= 300) {
                if ((now-updateTime)/1000 <= 60) return new Result(-20702);
                else {
                    verifyInfo.getEmail().setUpdateTime(login.selectDateFromSQL());
                    redis.setCacheObject(connectionId, verifyInfo);
                }
            } else {
                code = CodeUtil.generateVerifyCode();
                VerifyInfo newVerifyInfo = new VerifyInfo();
                newVerifyInfo.getEmail().setCode(code);
                newVerifyInfo.getEmail().setSaveTime(now);
                newVerifyInfo.getEmail().setUpdateTime(now);
                redis.setCacheObject(connectionId, newVerifyInfo);
            }
        }

        sendEmailUtil.sendAuthCodeEmail(email, code);

        return new Result(0);
    }

    @PostMapping("/cid")
    public Result connectionId(@RequestBody LoginInfo loginInfo) {
        String connectionId = loginInfo.getConnectionId();
        Map<String, Object> data = new HashMap<>();
        if (StringUtils.hasText(connectionId)) {
            if (redis.hasKey(connectionId) && redis.getCacheObject(connectionId) == null) {
                return new Result(-20005);
            } else {
                data.put("connectionId", connectionId);
                return new Result(0, data);
            }
        } else {
            String tempId = UUID.randomUUID().toString();
            redis.setCacheObject(tempId, null);
            data.put("connectionId", tempId);
            return new Result(0, data);
        }
    }

    @PostMapping("/delete")
    public Result delete(@RequestHeader("Authorization") String token, @RequestBody LoginInfo loginInfo) throws Exception {
        String email = loginInfo.getEmail();
        if (!StringUtils.hasText(email)) return new Result(-20001);
        loginInfo = login.researchDetail(email);
        Integer code = login.logicalDeleteUser(loginInfo.getId());

        String userNumber = StringUtils.hasText(loginInfo.getStudentNumber()) ? loginInfo.getStudentNumber() : loginInfo.getTeacherNumber();

        log.createLog(userNumber, "注销", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(code);
    }

    @PostMapping("/logout")
    public Result logout(@RequestBody LoginInfo loginInfo) {
        String email = loginInfo.getEmail();
        if (!StringUtils.hasText(email)) return new Result(-20001);
        loginInfo = login.researchDetail(email);
        redis.deleteObject("login:" + loginInfo.getId().toString());

        return new Result(0);
    }

    @PostMapping("/retrieve")
    public Result retrieve(@RequestBody LoginInfo loginInfo) {
        String email = loginInfo.getEmail();
        String password = loginInfo.getPassword();
        String verifyCode = loginInfo.getVerifyCode();
        String connectionId = loginInfo.getConnectionId();
        if (!StringUtils.hasText(email) ||
                !StringUtils.hasText(password) ||
                !StringUtils.hasText(verifyCode) ||
                !StringUtils.hasText(connectionId)) return new Result(-20001);

        JSONObject o = redis.getCacheObject(connectionId);
        VerifyInfo verifyInfo = o.toJavaObject(VerifyInfo.class);
        if (!verifyCode.equals(verifyInfo.getEmail().getCode())) return new Result(-20207);
        if ((verifyInfo.getEmail().getSaveTime()-login.selectDateFromSQL())/1000 > 300) return new Result(-20700);

        loginInfo = login.researchDetail(email);
        if (loginInfo == null) return new Result(-20203);

        loginInfo.setPassword(passwordEncoder.encode(password));
        Integer code = login.updatePassword(loginInfo);
        return new Result(code);
    }

    @PostMapping("/update/password")
    public Result updatePassword(@RequestBody LoginInfo loginInfo) {
        String password = loginInfo.getPassword();
        String oldPassword = loginInfo.getOldPassword();
        String email = loginInfo.getEmail();
        if (!StringUtils.hasText(password) ||
                !StringUtils.hasText(oldPassword) ||
                !StringUtils.hasText(email)) return new Result(-20001);

        loginInfo = login.researchDetail(email);
        if (loginInfo == null) return new Result(-20203);
        if (!passwordEncoder.matches(oldPassword, loginInfo.getPassword())) return new Result(-20204);
        loginInfo.setPassword(passwordEncoder.encode(password));
        Integer code = login.updatePassword(loginInfo);
        return new Result(code);
    }

    @PostMapping("/update/phone")
    public Result updatePhone(@RequestBody LoginInfo loginInfo) {
        String password = loginInfo.getPassword();
        String phone = loginInfo.getPhone();
        String email = loginInfo.getEmail();
        if (!StringUtils.hasText(password) ||
                !StringUtils.hasText(phone) ||
                !StringUtils.hasText(email)) return new Result(-20001);

        loginInfo = login.researchDetail(email);
        if (loginInfo == null) return new Result(-20203);
        if (!passwordEncoder.matches(password, loginInfo.getPassword())) return new Result(-20204);
        loginInfo.setPhone(phone);
        Integer code = login.updatePhone(loginInfo);
        return new Result(code);
    }

    @PostMapping("/update/email")
    public Result updateEmail(@RequestBody LoginInfo loginInfo) {
        String password = loginInfo.getPassword();
        String email = loginInfo.getEmail();
        String oldEmail = loginInfo.getOldEmail();
        if (!StringUtils.hasText(password) ||
                !StringUtils.hasText(email) ||
                !StringUtils.hasText(oldEmail)) return new Result(-20001);

        loginInfo = login.researchDetail(oldEmail);
        if (loginInfo == null) return new Result(-20203);
        if (!passwordEncoder.matches(password, loginInfo.getPassword())) return new Result(-20204);
        loginInfo.setEmail(email);
        Integer code = login.updateEmail(loginInfo);
        return new Result(code);
    }

    @PostMapping("/update/role")
    public Result updateRole(@RequestHeader("Authorization") String token, @RequestBody LoginInfo loginInfo) throws Exception {
        if (loginInfo.getId() == null || loginInfo.getRoleId() == null) return new Result(-20001);
        Integer code = login.updateRoleId(loginInfo);

        loginInfo = login.researchDetailById(loginInfo.getId());
        String userNumber = StringUtils.hasText(loginInfo.getStudentNumber()) ? loginInfo.getStudentNumber() : loginInfo.getTeacherNumber();

        log.createLog(userNumber, "修改用户组", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(code);
    }

    @GetMapping("/qrcode")
    public Result qrcode(String phone, HttpServletResponse response) throws IOException, WriterException {
        if (!StringUtils.hasText(phone)) return new Result(-20001);
        LoginInfo loginInfo = login.researchDetailByPhone(phone);
        if (loginInfo == null) return new Result(-20205);

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/png");

        String secretKey;
        if (!StringUtils.hasText(loginInfo.getSecretKey())) {
            secretKey = GoogleAuthenticatorUtil.createSecretKey();
            login.setSecretKey(secretKey, loginInfo);
        }
        else secretKey = loginInfo.getSecretKey();

        ServletOutputStream out = response.getOutputStream();
        String keyUri = GoogleAuthenticatorUtil.createKeyUri(secretKey, phone, "Club_Management_System");
        QRCodeUtil.writeToStream(keyUri, out);
        try {
            out.flush();
        } finally {
            out.close();
        }

        return new Result(0);
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginInfo loginInfo) throws Exception {
        String phone = loginInfo.getPhone();
        String email = loginInfo.getEmail();
        String password = loginInfo.getPassword();
        String code = loginInfo.getVerifyCode();
        String connectionId = loginInfo.getConnectionId();

        if ((!StringUtils.hasText(phone) && !StringUtils.hasText(email)) ||
                !StringUtils.hasText(password) ||
                !StringUtils.hasText(code) ||
                !StringUtils.hasText(connectionId)) return new Result(-20001);

        if (StringUtils.hasText(phone) && StringUtils.hasText(email)) return new Result(-20006);
        if (StringUtils.hasText(phone)) {
            loginInfo = login.researchDetailByPhone(phone);
            if (loginInfo == null) return new Result(-20205);
        } else {
            loginInfo = login.researchDetail(email);
            if (loginInfo == null) return new Result(-20206);
        }
        if (!passwordEncoder.matches(password, loginInfo.getPassword())) return new Result(-20204);

        if (!GoogleAuthenticatorUtil.verification(loginInfo.getSecretKey(), code)) return new Result(-20701);

        String subject = StringUtils.hasText(loginInfo.getStudentNumber()) ? loginInfo.getStudentNumber() : loginInfo.getTeacherNumber();
        String token = JWTUtil.createJWT(subject, rsaKeyProperties.getPrivateKey());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", loginInfo.getId());
        data.put("roleId", loginInfo.getRoleId());

        redis.setCacheObject("login:"+subject, loginInfo);
        redis.del(connectionId);

        return new Result(0, data);
    }

    @PostMapping("/research/detail")
    public Result researchDetail(@RequestBody LoginInfo loginInfo) {
        String email = loginInfo.getEmail();
        if (!StringUtils.hasText(email)) return new Result(-20001);

        loginInfo = login.researchDetail(email);
        if (loginInfo == null) return new Result(-20003);
        loginInfo.setPassword("");
        loginInfo.setSecretKey("");

        Map<String, Object> data = new HashMap<>();
        data.put("loginInfo", loginInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/basic")
    public Result researchBasic(@RequestBody LoginInfo loginInfo) {
        try {
            if (loginInfo.getPageIndex() == null ||
                    loginInfo.getPageSize() == null
            ) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return new Result(-20001);
        }

        try {
            if (loginInfo.getRealName().length() > 36 ||
                    loginInfo.getPhone().length() > 11 ||
                    loginInfo.getEmail().length() > 36 ||
                    loginInfo.getPageIndex() <= 0 ||
                    loginInfo.getPageSize() <= 0
            ) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return new Result(-20002);
        }

        Map<String, Object> data = login.researchBasic(loginInfo);

        return new Result(0, data);
    }

    @PostMapping("/is-login")
    public Result isLogin(@RequestHeader("Authorization") String token) throws Exception {
        if (!StringUtils.hasText(token)) return new Result(-20001);
        String userNumber = JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject();
        LoginInfo userInfo = login.researchDetailByUserNumber(userNumber);
        if (userInfo == null) return new Result(-20203);

        UserRole userRole = role.researchUserRoleById(userInfo.getRoleId());
        List<Permission> permissionList = permission.researchPermissionOfRole(userInfo.getRoleId());
        Map<String, Object> data = new HashMap<>();
        data.put("id", userInfo.getId());
        data.put("email", userInfo.getEmail());
        data.put("userNumber", userNumber);
        data.put("realName", userInfo.getRealName());
        data.put("permissionList", permissionList);
        data.put("role", userRole);

        return new Result(0, data);
    }
}

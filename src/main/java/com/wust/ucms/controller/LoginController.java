package com.wust.ucms.controller;

import com.google.code.kaptcha.Producer;
import com.google.zxing.WriterException;
import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.LoginInfo;
import com.wust.ucms.pojo.RSAKeyProperties;
import com.wust.ucms.pojo.VerifyInfo;
import com.wust.ucms.service.impl.LoginInfoServiceImpl;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LoginController {

    @Autowired
    LoginInfoServiceImpl login;

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

        VerifyInfo verifyInfo = redis.getCacheObject(loginInfo.getConnectionId());

        if (!Objects.equals(verifyInfo.getCaptcha().getCode(), loginInfo.getVerifyCode())) return new Result(-20207);

        loginInfo.setPassword(passwordEncoder.encode(loginInfo.getPassword()));
        loginInfo.setIsDelete(0);

        Integer flag = login.createUser(loginInfo);
        if (flag <= 0) return new Result(flag);

        return new Result(0);
    }

    @GetMapping("/captcha")
    public Result captchaImage(String connectionId, HttpServletResponse response) throws IOException {
        if (!redis.hasKey(connectionId)) throw new RuntimeException();

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        String capText = captcha.createText();
        VerifyInfo verifyInfo = new VerifyInfo();
        verifyInfo.getCaptcha().setCode(capText);
        redis.set(connectionId, verifyInfo);

        BufferedImage bufferedImage = captcha.createImage(capText);
        ServletOutputStream out = response.getOutputStream();

        ImageIO.write(bufferedImage, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
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
        VerifyInfo verifyInformation = redis.getCacheObject(connectionId);
        if (verifyInformation == null || verifyInformation.getEmail().getCode() == null) {
            VerifyInfo verifyInfo = new VerifyInfo();
            code = CodeUtil.generateVerifyCode();
            Long now = login.selectDateFromSQL();
            verifyInfo.getEmail().setCode(code);
            verifyInfo.getEmail().setSaveTime(now);
            verifyInfo.getEmail().setUpdateTime(now);
            redis.setCacheObject(connectionId, verifyInfo);
        } else {
            VerifyInfo verifyInfo = redis.getCacheObject(connectionId);
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
    public Result connectionId(String connectionId) {
        Map<String, Object> data = new HashMap<>();
        if (StringUtils.hasText(connectionId)) {
            if (!redis.hasKey(connectionId)) {
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
    public Result delete(@RequestBody LoginInfo loginInfo) {
        String email = loginInfo.getEmail();
        if (!StringUtils.hasText(email)) return new Result(-20001);
        loginInfo = login.researchDetail(email);
        Integer code = login.logicalDeleteUser(loginInfo.getId());

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

        VerifyInfo verifyInfo = redis.getCacheObject(connectionId);
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
    public Result updateRole(@RequestBody LoginInfo loginInfo) {
        if (loginInfo.getId() == null || loginInfo.getRoleId() == null) return new Result(-20001);
        Integer code = login.updateRoleId(loginInfo);
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
        // TODO 1、比对用户名和密码；2、比对Google二次验证码；3、登录成功申请token和redis并存储基本信息
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

        String token = JWTUtil.createJWT(loginInfo.getId().toString(), rsaKeyProperties.getPrivateKey());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", loginInfo.getId());
        data.put("roleId", loginInfo.getRoleId());

        redis.setCacheObject("login:"+loginInfo.getId().toString(), loginInfo);
        redis.del(connectionId);

        return new Result(0, data);
    }

    @PostMapping("/research/detail")
    public Result researchDetail(@RequestBody LoginInfo loginInfo) {
        String email = loginInfo.getEmail();
        if (!StringUtils.hasText(email)) return new Result(-20001);

        loginInfo = login.researchDetail(email);
        if (loginInfo == null) return new Result(-20003);

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
}

package com.wust.ucms.controller;

import com.google.code.kaptcha.Producer;
import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.LoginInfo;
import com.wust.ucms.service.impl.LoginInfoServiceImpl;
import com.wust.ucms.utils.CodeUtil;
import com.wust.ucms.utils.RedisCache;
import com.wust.ucms.utils.SendEmailUtil;
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

        Map<String, Object> verifyInfo = redis.getCacheObject(loginInfo.getConnectionId());

        if (verifyInfo.get("code") != loginInfo.getVerifyCode()) return new Result(-20207);

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
        Map<String, Object> verifyInfo = new HashMap<>();
        verifyInfo.put("code", capText);
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
        if (redis.getCacheObject(connectionId) == null) {
            Map<String, Object> verifyInfo = new HashMap<>();
            code = CodeUtil.generateVerifyCode();
            Long now = login.selectDateFromSQL();
            verifyInfo.put("code", code);
            verifyInfo.put("saveTime", now);
            verifyInfo.put("updateTime", now);
            redis.setCacheObject(connectionId, verifyInfo);
        } else {
            Map<String, Object> verifyInfo = redis.getCacheObject(connectionId);
            code = (String) verifyInfo.get("code");
            Long now = login.selectDateFromSQL();
            Long saveTime = (Long) verifyInfo.get("saveTime");
            Long updateTime = (Long) verifyInfo.get("updateTime");
            if (now-saveTime <= 0 || now-updateTime <= 0) return new Result(-20004);
            if ((now-saveTime)/1000 <= 60) return new Result(-20702);
            if ((now-saveTime)/1000 <= 300) {
                if ((now-updateTime)/1000 <= 60) return new Result(-20702);
                else {
                    verifyInfo.put("updateTime", login.selectDateFromSQL());
                    redis.setCacheObject(connectionId, verifyInfo);
                }
            } else {
                code = CodeUtil.generateVerifyCode();
                Map<String, Object> newVerifyInfo = new HashMap<>();
                newVerifyInfo.put("code", code);
                newVerifyInfo.put("saveTime", now);
                newVerifyInfo.put("updateTime", now);
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
        return null;
    }

    @PostMapping("/update/password")
    public Result updatePassword(@RequestBody LoginInfo loginInfo) {
        return null;
    }

    @PostMapping("/update/phone")
    public Result updatePhone(@RequestBody LoginInfo loginInfo) {
        return null;
    }

    @PostMapping("/update/email")
    public Result updateEmail(@RequestBody LoginInfo loginInfo) {
        return null;
    }

    @PostMapping("/update/role")
    public Result updateRole(@RequestBody LoginInfo loginInfo) {
        return null;
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginInfo loginInfo) {
        return null;
    }

    @PostMapping("/research/detail")
    public Result researchDetail(@RequestBody LoginInfo loginInfo) {
        return null;
    }

    @PostMapping("/research/basic")
    public Result researchBasic(@RequestBody LoginInfo loginInfo) {
        return null;
    }
}

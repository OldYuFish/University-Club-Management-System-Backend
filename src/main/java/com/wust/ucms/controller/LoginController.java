package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.LoginInfo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LoginController {

    @PostMapping("/register")
    public Result register(@RequestBody LoginInfo loginInfo) {
        return null;
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody LoginInfo loginInfo) {
        return null;
    }

    @PostMapping("/logout")
    public Result logout(@RequestBody LoginInfo loginInfo) {
        return null;
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

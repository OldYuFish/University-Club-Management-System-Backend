package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.Log;
import com.wust.ucms.service.impl.LogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LogController {

    @Autowired
    LogServiceImpl logging;

    @PostMapping("/research")
    public Result researchLog(@RequestBody Log log) {
        return null;
    }
}

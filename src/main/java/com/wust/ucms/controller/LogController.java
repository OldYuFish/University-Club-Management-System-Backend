package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.Log;
import com.wust.ucms.service.impl.LogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/log")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LogController {

    @Autowired
    LogServiceImpl logging;

    @PostMapping("/research")
    public Result researchLog(@RequestBody Log log) {
        try {
            if (log.getPageIndex() == null ||
                    log.getPageSize() == null
            ) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return new Result(-20001);
        }

        try {
            if (log.getObject().length() > 12 ||
                    log.getOperate().length() > 6 ||
                    ((log.getStudentNumber() != null && !log.getStudentNumber().isEmpty()) &&
                            log.getStudentNumber().length() != 12) ||
                    log.getTeacherNumber().length() > 24
            ) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return new Result(-20002);
        }

        Map<String, Object> data = logging.researchLog(log);

        return new Result(0, data);
    }
}

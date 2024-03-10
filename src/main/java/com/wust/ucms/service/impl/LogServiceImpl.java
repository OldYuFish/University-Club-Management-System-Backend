package com.wust.ucms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wust.ucms.mapper.LogMapper;
import com.wust.ucms.mapper.LoginInfoMapper;
import com.wust.ucms.pojo.Log;
import com.wust.ucms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    LogMapper logging;

    @Autowired
    LoginInfoMapper login;

    @Override
    public Integer createLog(String object, String operate, Integer loginId) {
        Log log = new Log();
        Date dateTime = logging.selectDateFromSQL();
        log.setOperateTime(dateTime);
        log.setObject(object);
        log.setOperate(operate);
        log.setLoginId(loginId);
        int flag = logging.insert(log);
        if (flag > 0) return 0;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Map<String, Object> researchLog(Log log) {
        LambdaQueryWrapper<Log> lqw = new LambdaQueryWrapper<>();
        lqw.eq(
                log.getObject() != null && !log.getObject().isEmpty(),
                Log::getObject,
                log.getObject()
        ).eq(
                log.getOperate() != null && !log.getOperate().isEmpty(),
                Log::getOperate,
                log.getOperate()
        ).eq(
                log.getStudentNumber() != null && !log.getStudentNumber().isEmpty() && login.selectLoginIdByStudentNumber(log.getStudentNumber()) != null,
                Log::getLoginId,
                login.selectLoginIdByStudentNumber(log.getStudentNumber())
        ).eq(
                log.getTeacherNumber() != null && !log.getTeacherNumber().isEmpty() && login.selectLoginIdByTeacherNumber(log.getTeacherNumber()) != null,
                Log::getLoginId,
                login.selectLoginIdByTeacherNumber(log.getTeacherNumber())
        );

        IPage<Log> page = new Page<>(log.getPageIndex(), log.getPageSize());
        logging.selectPage(page, lqw);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", page.getTotal());
        pagination.put("pageIndex", log.getPageIndex());
        pagination.put("pageSize", log.getPageSize());

        List<Map<String, Object>> logList = new ArrayList<>();
        for (Log l : page.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("object", l.getObject());
            map.put("operate", l.getOperate());
            map.put("operateTime", l.getOperateTime());
            map.put("studentNumber", login.selectById(l.getLoginId()).getStudentNumber());
            map.put("teacherNumber", login.selectById(l.getLoginId()).getTeacherNumber());
            logList.add(map);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("logList", logList);
        data.put("pagination", pagination);

        return data;
    }
}

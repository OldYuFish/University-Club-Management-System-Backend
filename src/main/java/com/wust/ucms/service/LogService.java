package com.wust.ucms.service;

import com.wust.ucms.pojo.Log;

import java.util.Map;

public interface LogService {
    Integer createLog(String object, String operate, String userNumber);
    Map<String, Object> researchLog(Log log);
}

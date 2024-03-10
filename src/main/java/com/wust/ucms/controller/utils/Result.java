package com.wust.ucms.controller.utils;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Result {
    private Integer code;
    private String message;
    private Map<String, Object> data;

    public Result() {
        this.data = new HashMap<>();
    }

    public Result(Integer code) {
        this.code = code;
        switch (code) {
            case -10000 -> this.message = "Invalid token";
            case -10001 -> this.message = "Permission is mismatch";
            case -10002 -> this.message = "Login authentication failed";
            case -20000 -> this.message = "Missing the request parameter";
            case -20001 -> this.message = "Request field is empty";
            case -20002 -> this.message = "Incorrect field format";
            case -20003 -> this.message = "Sql operation failed";
            case -20004 -> this.message = "System clock error";
            case -20005 -> this.message = "connectionId does not exist";
            case -20006 -> this.message = "Params logic error";
            case -20100 -> this.message = "Unknown error";
            case -20101 -> this.message = "SQL Exception";
            case -20102 -> this.message = "File is too large";
            case -20103 -> this.message = "Failed to write file";
            case -20104 -> this.message = "Failed to delete file";
            case -20200 -> this.message = "User did not send verification code";
            case -20201 -> this.message = "User's phone has been registered";
            case -20202 -> this.message = "User's email has been registered";
            case -20203 -> this.message = "User does not exist";
            case -20204 -> this.message = "Password error";
            case -20205 -> this.message = "User not registered";
            case -20206 -> this.message = "The user has been locked out due to too many wrong passwords";
            case -20207 -> this.message = "Graphic verification code error";
            case -20208 -> this.message = "The club already exists";
            case -20209 -> this.message = "The user is already the president of another club";
            case -20300 -> this.message = "Users have no application form related records in the current batch";
            case -20301 -> this.message = "User has submitted two application forms in the current batch";
            case -20302 -> this.message = "The user has not saved the application form in the current batch";
            case -20303 -> this.message = "The selected specialty does not exist";
            case -20304 -> this.message = "Application form cannot be modified";
            case -20305 -> this.message = "Application form is missing attachments";
            case -20306 -> this.message = "This application form does not belong to this user";
            case -20307 -> this.message = "Application form has been deleted";
            case -20308 -> this.message = "No application form records in visible years";
            case -20309 -> this.message = "Application form does not exist";
            case -20310 -> this.message = "Error in status setting";
            case -20311 -> this.message = "Application form has been approved";
            case -20312 -> this.message = "The status of the application form is abnormal";
            case -20313 -> this.message = "Submitted Computer Science application form";
            case -20314 -> this.message = "Submitted application form for communication major";
            case -20315 -> this.message = "There is already a submitted form and a saved form";
            case -20400 -> this.message = "No valid batch exists at the current time";
            case -20401 -> this.message = "Batch with the same name exists";
            case -20402 -> this.message = "Batch time does not meet the requirements";
            case -20403 -> this.message = "Batch and other batch time conflicts";
            case -20404 -> this.message = "Batch does not exist";
            case -20500 -> this.message = "File loss";
            case -20501 -> this.message = "Original file name is empty";
            case -20502 -> this.message = "File format error";
            case -20503 -> this.message = "File attachment application form does not exist";
            case -20600 -> this.message = "The current year is not open";
            case -20700 -> this.message = "Verification code has expired";
            case -20701 -> this.message = "Verification code error";
            case -20702 -> this.message = "The time interval between two verification codes is less than 60";
            default -> this.message = "Success";
        }
        this.data = new HashMap<>();
    }

    public Result(Integer code, Map<String, Object> data) {
        this.code = code;
        switch (code) {
            case -10000 -> this.message = "Invalid token";
            case -10001 -> this.message = "Permission is mismatch";
            case -10002 -> this.message = "Login authentication failed";
            case -20000 -> this.message = "Missing the request parameter";
            case -20001 -> this.message = "Request field is empty";
            case -20002 -> this.message = "Incorrect field format";
            case -20003 -> this.message = "Sql operation failed";
            case -20004 -> this.message = "System clock error";
            case -20005 -> this.message = "connectionId does not exist";
            case -20006 -> this.message = "Params logic error";
            case -20100 -> this.message = "Unknown error";
            case -20101 -> this.message = "SQL Exception";
            case -20102 -> this.message = "File is too large";
            case -20103 -> this.message = "Failed to write file";
            case -20104 -> this.message = "Failed to delete file";
            case -20200 -> this.message = "User did not send verification code";
            case -20201 -> this.message = "User's phone has been registered";
            case -20202 -> this.message = "User's email has been registered";
            case -20203 -> this.message = "User does not exist";
            case -20204 -> this.message = "Password error";
            case -20205 -> this.message = "User not registered";
            case -20206 -> this.message = "The user has been locked out due to too many wrong passwords";
            case -20207 -> this.message = "Graphic verification code error";
            case -20208 -> this.message = "The club already exists";
            case -20209 -> this.message = "The user is already the president of another club";
            case -20300 -> this.message = "Users have no application form related records in the current batch";
            case -20301 -> this.message = "User has submitted two application forms in the current batch";
            case -20302 -> this.message = "The user has not saved the application form in the current batch";
            case -20303 -> this.message = "The selected specialty does not exist";
            case -20304 -> this.message = "Application form cannot be modified";
            case -20305 -> this.message = "Application form is missing attachments";
            case -20306 -> this.message = "This application form does not belong to this user";
            case -20307 -> this.message = "Application form has been deleted";
            case -20308 -> this.message = "No application form records in visible years";
            case -20309 -> this.message = "Application form does not exist";
            case -20310 -> this.message = "Error in status setting";
            case -20311 -> this.message = "Application form has been approved";
            case -20312 -> this.message = "The status of the application form is abnormal";
            case -20313 -> this.message = "Submitted Computer Science application form";
            case -20314 -> this.message = "Submitted application form for communication major";
            case -20315 -> this.message = "There is already a submitted form and a saved form";
            case -20400 -> this.message = "No valid batch exists at the current time";
            case -20401 -> this.message = "Batch with the same name exists";
            case -20402 -> this.message = "Batch time does not meet the requirements";
            case -20403 -> this.message = "Batch and other batch time conflicts";
            case -20404 -> this.message = "Batch does not exist";
            case -20500 -> this.message = "File loss";
            case -20501 -> this.message = "Original file name is empty";
            case -20502 -> this.message = "File format error";
            case -20503 -> this.message = "File attachment application form does not exist";
            case -20600 -> this.message = "The current year is not open";
            case -20700 -> this.message = "Verification code has expired";
            case -20701 -> this.message = "Verification code error";
            case -20702 -> this.message = "The time interval between two verification codes is less than 60";
            default -> this.message = "Success";
        }
        this.data = data;
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.data = new HashMap<>();
    }

    public Result(Integer code, String message, Map<String, Object> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}

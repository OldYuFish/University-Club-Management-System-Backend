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
            case 1 -> this.message = "Some required items are blank.";
            case 2 -> this.message = "Some items have a wrong length.";
            case 3 -> this.message = "The information has been used.";
            case 4 -> this.message = "Information mismatch.";
            case 5 -> this.message = "SQL error.";
            default -> this.message = "Success.";
        }
        this.data = new HashMap<>();
    }

    public Result(Integer code, Map<String, Object> data) {
        this.code = code;
        switch (code) {
            case 1 -> this.message = "Some required items are blank.";
            case 2 -> this.message = "Some items have a wrong length.";
            case 3 -> this.message = "The information has been used.";
            case 4 -> this.message = "Information mismatch.";
            case 5 -> this.message = "SQL error.";
            default -> this.message = "Success.";
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

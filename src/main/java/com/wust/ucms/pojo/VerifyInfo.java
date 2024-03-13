package com.wust.ucms.pojo;

import com.wust.ucms.pojo.verify.Captcha;
import com.wust.ucms.pojo.verify.Email;
import lombok.Data;

@Data
public class VerifyInfo {
    private Captcha captcha;
    private Email email;
}

package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.MemberInfo;
import com.wust.ucms.service.impl.MemberInfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MemberController {

    @Autowired
    MemberInfoServiceImpl member;

    private static Integer paramsException(MemberInfo memberInfo) {
        try {
            if (!StringUtils.hasText(memberInfo.getMemberName()) ||
                    !StringUtils.hasText(memberInfo.getDepartment()) ||
                    !StringUtils.hasText(memberInfo.getJob()) ||
                    !StringUtils.hasText(memberInfo.getStudentNumber()) ||
                    memberInfo.getClubId() == null
            ) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return -20001;
        }

        try {
            if (memberInfo.getMemberName().length() > 36 ||
                    memberInfo.getDepartment().length() > 24 ||
                    memberInfo.getJob().length() > 8 ||
                    memberInfo.getStudentNumber().length() != 12
            ) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return -20002;
        }

        return 0;
    }

    @PostMapping("/create")
    public Result createMemberInfo(@RequestBody MemberInfo memberInfo) {
        Integer code = paramsException(memberInfo);
        if (code != 0) return new Result(code);

        code = member.createMemberInfo(memberInfo);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        return new Result(0, data);
    }

    @PostMapping("/delete")
    public Result deleteMemberInfo(@RequestBody MemberInfo memberInfo) {
        Integer id = memberInfo.getId();
        try {
            if (id == null || id <= 0) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        Integer code = member.deleteMemberInfo(id);
        return new Result(code);
    }

    @PostMapping("/update")
    public Result updateMemberInfo(@RequestBody MemberInfo memberInfo) {
        Integer code = paramsException(memberInfo);
        if (code != 0) return new Result(code);

        try {
            if (memberInfo.getId() == null || memberInfo.getId() <= 0) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        code = member.updateMemberInfo(memberInfo);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        return new Result(0, data);
    }

    @PostMapping("/research")
    public Result researchMemberInfo(@RequestBody MemberInfo memberInfo) {
        Integer clubId = memberInfo.getClubId();
        if (clubId == null) return new Result(-20001);
        List<MemberInfo> memberList = member.researchMemberInfo(clubId);
        Map<String, Object> data = new HashMap<>();
        data.put("memberList", memberList);

        return new Result(0, data);
    }
}

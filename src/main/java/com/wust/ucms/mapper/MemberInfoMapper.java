package com.wust.ucms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wust.ucms.pojo.MemberInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberInfoMapper extends BaseMapper<MemberInfo> {

    @Select("select id from member_info where student_number = #{studentNumber}")
    Integer selectMemberIdByStudentNumber(String studentNumber);
}

package com.wust.ucms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wust.ucms.pojo.LoginInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface LoginInfoMapper extends BaseMapper<LoginInfo> {

    @Select("select now()")
    Date selectDateFromSQL();

    @Select("select id from login_info where email = #{email}")
    Integer selectLoginIdByEmail(String email);

    @Select("select id from login_info where student_number = #{studentNumber}")
    Integer selectLoginIdByStudentNumber(String studentNumber);

    @Select("select id from login_info where teacher_number = #{teacherNumber}")
    Integer selectLoginIdByTeacherNumber(String teacherNumber);

    @Select("select id from login_info where phone = #{phone}")
    Integer selectLoginIdByPhone(String phone);

    @Select("select * from login_info where is_delete = 1")
    List<LoginInfo> selectDeleteLoginInfo();

    @Select("select * from login_info where  role_id = #{roleId}")
    List<LoginInfo> selectLoginInfoByRoleId(Integer roleId);

    @Select("select * from login_info where student_number = #{userNumber} or teacher_number = #{userNumber}")
    LoginInfo selectLoginInfoByUserNumber(String userNumber);
}

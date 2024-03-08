package com.wust.ucms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wust.ucms.pojo.LoginInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginInfoMapper extends BaseMapper<LoginInfo> {

    @Select("select id from login_info where email = #{email}")
    Integer selectLoginIdByEmail(String email);
}

package com.wust.ucms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wust.ucms.pojo.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

@Mapper
public interface LogMapper extends BaseMapper<Log> {
    @Select("select now()")
    Date selectDateFromSQL();
}

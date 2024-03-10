package com.wust.ucms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wust.ucms.pojo.ClubInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClubInfoMapper extends BaseMapper<ClubInfo> {

    @Select("select id from club_info where club_name = #{clubName}")
    Integer selectClubIdByClubName(String clubName);

    @Select("select * from club_info where status_code = 3")
    List<ClubInfo> selectAllActiveClubInfo();

    @Select("select id from club_info where login_id = #{loginId}")
    Integer selectClubIdByLoginId(Integer loginId);
}

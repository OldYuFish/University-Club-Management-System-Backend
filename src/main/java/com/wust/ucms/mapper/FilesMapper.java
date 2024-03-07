package com.wust.ucms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wust.ucms.pojo.Files;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FilesMapper extends BaseMapper<Files> {

    @Delete("delete from files where file_name = #{fileName}")
    int deleteFileByFileName(String fileName);

    @Select("select file_name from files where login_id = #{loginId}")
    List<String> selectFileNameByLoginId(Integer loginId);

    @Select("select file_name from files where club_id = #{clubId}")
    List<String> selectFileNameByClubId(Integer clubId);

    @Select("select file_name from files where member_id = #{memberId}")
    List<String> selectFileNameByMemberId(Integer memberId);

    @Select("select file_name from files where activity_id = #{activityId}")
    List<String> selectFileNameByActivityId(Integer activityId);

    @Select("select file_name from files where fund_id = #{fundId}")
    List<String> selectFileNameByFundId(Integer fundId);

    @Select("select file_name from files where competition_id = #{competitionId}")
    List<String> selectFileNameByCompetitionId(Integer competitionId);

}

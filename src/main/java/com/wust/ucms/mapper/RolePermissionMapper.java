package com.wust.ucms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wust.ucms.pojo.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    @Delete("delete from role_permission where role_id = #{roleId}")
    Integer deleteRolePermissionByRoleId(Integer roleId);

    @Select("select permission_id from role_permission where role_id = #{roleId}")
    List<Integer> selectPermissionByRoleId(Integer roleId);
}

package com.springboot_master.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springboot_master.blog.entity.UserRole;
import org.springframework.stereotype.Repository;


/**
 * 用户角色
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Repository
public interface UserRoleDao extends BaseMapper<UserRole> {

}

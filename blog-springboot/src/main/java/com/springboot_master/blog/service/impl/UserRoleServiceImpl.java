package com.springboot_master.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot_master.blog.dao.UserInfoDao;
import com.springboot_master.blog.dao.UserRoleDao;
import com.springboot_master.blog.entity.UserRole;
import com.springboot_master.blog.enums.RoleEnum;
import com.springboot_master.blog.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 用户角色服务
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleDao, UserRole> implements UserRoleService {


}

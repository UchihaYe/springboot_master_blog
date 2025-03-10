package com.springboot_master.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springboot_master.blog.entity.ChatRecord;
import org.springframework.stereotype.Repository;

/**
 * 聊天记录
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Repository
public interface ChatRecordDao extends BaseMapper<ChatRecord> {
}

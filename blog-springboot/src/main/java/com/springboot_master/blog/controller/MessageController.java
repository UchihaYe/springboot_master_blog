package com.springboot_master.blog.controller;


import com.springboot_master.blog.annotation.AccessLimit;
import com.springboot_master.blog.annotation.OptLog;
import com.springboot_master.blog.dto.MessageBackDTO;
import com.springboot_master.blog.vo.PageResult;
import com.springboot_master.blog.vo.*;
import com.springboot_master.blog.dto.MessageDTO;
import com.springboot_master.blog.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.springboot_master.blog.constant.OptTypeConst.REMOVE;
import static com.springboot_master.blog.constant.OptTypeConst.UPDATE;

/**
 * 留言控制器
 *
 * @author xiaojie
 * @date 2021/07/29
 */
@Api(tags = "留言模块")
@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;

    /**
     * 添加留言
     *
     * @param messageVO 留言信息
     * @return {@link Result<>}
     */
    @AccessLimit(seconds = 60, maxCount = 1)
    @ApiOperation(value = "添加留言")
    @PostMapping("/messages")
    public Result<?> saveMessage(@Valid @RequestBody MessageVO messageVO) {
        messageService.saveMessage(messageVO);
        return Result.ok();
    }

    /**
     * 查看留言列表
     *
     * @return {@link Result<MessageDTO>} 留言列表
     */
    @ApiOperation(value = "查看留言列表")
    @GetMapping("/messages")
    public Result<List<MessageDTO>> listMessages() {
        return Result.ok(messageService.listMessages());
    }

    /**
     * 查看后台留言列表
     *
     * @param condition 条件
     * @return {@link Result<MessageBackDTO>} 留言列表
     */
    @ApiOperation(value = "查看后台留言列表")
    @GetMapping("/admin/messages")
    public Result<PageResult<MessageBackDTO>> listMessageBackDTO(ConditionVO condition) {
        return Result.ok(messageService.listMessageBackDTO(condition));
    }

    /**
     * 审核留言
     *
     * @param reviewVO 审核信息
     * @return {@link Result<>}
     */
    @OptLog(optType = UPDATE)
    @ApiOperation(value = "审核留言")
    @PutMapping("/admin/messages/review")
    public Result<?> updateMessagesReview(@Valid @RequestBody ReviewVO reviewVO) {
        messageService.updateMessagesReview(reviewVO);
        return Result.ok();
    }

    /**
     * 删除留言
     *
     * @param messageIdList 留言id列表
     * @return {@link Result<>}
     */
    @OptLog(optType = REMOVE)
    @ApiOperation(value = "删除留言")
    @DeleteMapping("/admin/messages")
    public Result<?> deleteMessages(@RequestBody List<Integer> messageIdList) {
        messageService.removeByIds(messageIdList);
        return Result.ok();
    }

}


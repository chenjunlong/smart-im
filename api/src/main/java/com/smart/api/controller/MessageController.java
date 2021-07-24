package com.smart.api.controller;

import javax.annotation.Resource;

import com.smart.api.controller.vo.CommentVo;
import com.smart.biz.common.model.Comment;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.smart.api.annotation.ParamDesc;
import com.smart.api.annotation.RequestLog;
import com.smart.api.annotation.ResponseLog;
import com.smart.api.exception.ApiException;
import com.smart.api.exception.ExcepFactor;
import com.smart.api.intercepter.auth.BaseInfo;
import com.smart.biz.common.model.em.CmdEnum;
import com.smart.biz.common.model.em.MsgTypeEnum;
import com.smart.biz.service.MessageService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author chenjunlong
 * @desc 消息投递
 */
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;


    /**
     * curl --request POST 'http://localhost:8000/v1/smart-im/message/send' \
     * --data-urlencode 'sender_id=1001' \
     * --data-urlencode 'receive_id=room1001' \
     * --data-urlencode 'msg_type=1' \
     * --data-urlencode 'cmd=101' \
     * --data-urlencode 'content=test message'
     */
    @RequestLog
    @ResponseLog
    @BaseInfo(desc = "发送IM消息", needAuth = true, rateLimit = 1000)
    @PostMapping(value = "/send", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean send(@RequestParam(value = "sender_id") @ParamDesc(desc = "发送者uid", range = "long:1~-1") long senderId,
            @RequestParam(value = "receive_id") @ParamDesc(desc = "接收者") String receiveId,
            @RequestParam(value = "msg_type") @ParamDesc(desc = "0p2p（receive_id传uid）, 1房间（receive_id传live_id）2（receive_id传*）", range = "int:0~2") int msgType,
            @RequestParam(value = "cmd") @ParamDesc(desc = "消息ID", range = "int:0~999") int cmd,
            @RequestParam(value = "content") @ParamDesc(desc = "消息体") String content) {
        if (!CmdEnum.isValid(cmd)) {
            throw new ApiException(ExcepFactor.E_PARAMS_ERROR, "cmd").setAttribute("rid", MDC.get("rid"));
        }
        return messageService.send(senderId, receiveId, msgType, cmd, content);
    }

    /**
     * curl --request POST 'http://localhost:8000/v1/smart-im/message/comment' \
     * --data-urlencode 'sender_id=1001' \
     * --data-urlencode 'receive_id=room1001' \
     * --data-urlencode 'content=test message'
     */
    @RequestLog
    @ResponseLog
    @BaseInfo(desc = "评论接口", needAuth = true, rateLimit = 1000)
    @PostMapping(value = "/comment", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean comment(@RequestParam(value = "sender_id") @ParamDesc(desc = "发送者uid", range = "long:1~-1") long senderId,
            @RequestParam(value = "receive_id") @ParamDesc(desc = "接收者") String receiveId,
            @RequestParam(value = "content") @ParamDesc(desc = "评论内容") String content) {
        return messageService.comment(senderId, receiveId, MsgTypeEnum.ROOM.getType(), CmdEnum.COMMENT.getCmdId(), content);
    }

    /**
     * curl --request GET \
     * 'http://localhost:8000/v1/smart-im/message/show/last_comment?room_id=room1001'
     */
    @RequestLog
    @ResponseLog
    @BaseInfo(desc = "获取最近10条评论", needAuth = true, rateLimit = 1000)
    @GetMapping(value = "/show/last_comment", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CommentVo> lastComment(@RequestParam(value = "room_id") @ParamDesc(desc = "房间ID") String roomId) {
        List<Comment> commentList = messageService.getLastCommentList(roomId);
        return CommentVo.transfer(commentList);
    }
}

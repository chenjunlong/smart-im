package com.smart.api.controller;

import com.smart.api.annotation.ParamDesc;
import com.smart.api.annotation.RequestLog;
import com.smart.api.annotation.ResponseLog;
import com.smart.api.auth.BaseInfo;
import com.smart.api.exception.ApiException;
import com.smart.api.exception.ExcepFactor;
import com.smart.service.biz.MessageService;
import com.smart.service.common.model.CmdEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author chenjunlong
 */
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;


    /**
     * curl -d 'sender_id=1001&receive_id=room1001&board_cast=1&cmd=101&content=test message' 'http://localhost:8000/v1/smart-im/message/send'
     */
    @RequestLog
    @ResponseLog
    @BaseInfo(desc = "发送IM消息", needAuth = true)
    @PostMapping(value = "/send", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean send(@RequestParam(value = "sender_id") @ParamDesc(desc = "发送者uid", range = "long:1~-1") long senderId,
                        @RequestParam(value = "receive_id") @ParamDesc(desc = "接收者uid, *表示所有房间所有人", range = "long:1~-1") String receiveId,
                        @RequestParam(value = "board_cast") @ParamDesc(desc = "0p2p, 1房间", range = "int:0~1") int boardCast,
                        @RequestParam(value = "cmd") @ParamDesc(desc = "消息类型", range = "int:0~999") int cmd,
                        @RequestParam(value = "content") @ParamDesc(desc = "消息体") String content) {
        if (!CmdEnum.isValid(cmd)) {
            throw new ApiException(ExcepFactor.E_PARAMS_ERROR, "cmd").setAttribute("rid", MDC.get("rid"));
        }
        messageService.send(senderId, receiveId, boardCast, cmd, content);
        return true;
    }
}

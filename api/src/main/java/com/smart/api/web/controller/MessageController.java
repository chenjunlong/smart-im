package com.smart.api.web.controller;


import com.smart.auth.annotation.BaseInfo;
import com.smart.auth.annotation.ParamDesc;
import com.smart.auth.annotation.RequestLog;
import com.smart.service.biz.MessageService;
import lombok.extern.slf4j.Slf4j;
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
     * curl -d 'uid=1001&room_id=room1001&content=test message' 'http://localhost:8000/v1/smart-im/message/send'
     */
    @RequestLog
    @BaseInfo(desc = "发送IM消息", needAuth = true)
    @PostMapping(value = "/send", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean send(@RequestParam("uid") @ParamDesc(desc = "发送者uid") long uid,
                        @RequestParam("room_id") @ParamDesc(desc = "房间id") String roomId,
                        @RequestParam("content") @ParamDesc(desc = "消息体") String content) {
        messageService.send(uid, roomId, content);
        return true;
    }
}

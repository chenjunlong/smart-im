package com.smart.biz.common.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户评论
 * 
 * @author chenjunlong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private long messageId;
    private long senderId;
    private String receiveId;
    private String content;
    private Timestamp createTime;
    private String ext;

}

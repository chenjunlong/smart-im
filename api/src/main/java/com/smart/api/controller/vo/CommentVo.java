package com.smart.api.controller.vo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.smart.biz.common.model.Comment;

import lombok.Data;

/**
 * @author chenjunlong
 */
@Data
public class CommentVo {

    private long messageId;
    private long senderId;
    private String receiveId;
    private String content;
    private long createTime;

    public static CommentVo transfer(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment, commentVo);
        commentVo.createTime = comment.getCreateTime().getTime();
        return commentVo;
    }

    public static List<CommentVo> transfer(List<Comment> commentList) {
        return commentList.stream().map(comment -> transfer(comment)).collect(Collectors.toList());
    }

}

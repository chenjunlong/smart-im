package com.smart.biz.dao;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.smart.biz.common.model.Comment;

/**
 * 评论消息存储
 * message_id纬度分表
 * 
 * @author chenjunlong
 */
@Repository
public class CommentDao {

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public boolean createComment(Comment comment) {
        String sql = "INSERT INTO `t_comment` (`sender_id`, `receive_id`, `content`, `create_time`, `ext`) VALUES (?, ?, ?, ?, ?)";
        int rows = jdbcTemplate.update(sql, comment.getSenderId(), comment.getReceiveId(), comment.getContent(), new Timestamp(System.currentTimeMillis()), "");
        return rows > 0;
    }

    public List<Comment> getLastCommentList(String roomId) {
        String sql = "SELECT * FROM `t_comment` WHERE `receive_id` = ? ORDER BY `create_time` ASC limit 10";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Comment.class), roomId);
    }
}

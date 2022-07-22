package com.xsh.dao;

import com.xsh.pojo.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : xsh
 * @create : 2020-02-15 - 0:26
 * @describe:
 */
public interface CommentRepository extends JpaRepository<Comment,Long> {

    /**
     * 根据评论对应的id与顶级评论找到comment列表
     * @param blogId
     * @param sort
     * @return
     */
    List<Comment> findByBlogIdAndParentCommentNull(Long blogId, Sort sort);
}

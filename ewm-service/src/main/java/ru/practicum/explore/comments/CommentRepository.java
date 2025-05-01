package ru.practicum.explore.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByCreator(Integer userId);

    @Query("SELECT c " +
            "FROM Comment AS c " +
            "WHERE c.status = 'PUBLISHED' " +
            "AND c.id IN ?1")
    List<Comment> getPublishedCommentsByIds(List<Integer> commentIds);

    @Query("SELECT c " +
            "FROM Comment AS c " +
            "WHERE c.status = 'PENDING' " +
            "AND c.id IN ?1")
    List<Comment> getPendingCommentsWithIds(List<Integer> commentIds);
}
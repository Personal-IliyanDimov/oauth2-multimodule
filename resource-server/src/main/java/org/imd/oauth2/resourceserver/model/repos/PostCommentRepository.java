package org.imd.oauth2.resourceserver.model.repos;

import org.imd.oauth2.resourceserver.model.entities.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostCommentEntity, Long> {

    List<PostCommentEntity> findAllByPostId(Long postId);
}
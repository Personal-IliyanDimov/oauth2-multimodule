package org.imd.oauth2.resourceserver.model.repos;

import org.imd.oauth2.resourceserver.model.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    public Optional<PostEntity> findByTitle(String title);
}
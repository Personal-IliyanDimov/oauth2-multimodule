package org.imd.oauth2.resourceserver.model.mapper.domain;

import org.imd.oauth2.resourceserver.model.domain.Post;
import org.imd.oauth2.resourceserver.model.entities.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostDomainMapper {
    Post toPost(PostEntity pe);
    PostEntity toPostEntity(Post post);

    void transfer(Post post, @MappingTarget PostEntity existingPostEntity);

    List<Post> toPosts(List<PostEntity> allEntities);
}

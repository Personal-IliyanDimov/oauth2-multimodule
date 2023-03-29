package org.imd.oauth2.resourceserver.model.mapper.dto;

import org.imd.oauth2.resourceserver.model.domain.Post;
import org.imd.oauth2.resourceserver.model.dto.PostDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    PostDto toPostDto(Post post);
    Post toPost(PostDto postDto);

    List<PostDto> toPostDtos(List<Post> users);

}

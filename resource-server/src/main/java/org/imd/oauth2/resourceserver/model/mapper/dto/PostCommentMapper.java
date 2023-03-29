package org.imd.oauth2.resourceserver.model.mapper.dto;

import org.imd.oauth2.resourceserver.model.domain.PostComment;
import org.imd.oauth2.resourceserver.model.dto.PostCommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCommentMapper {
    PostCommentDto toPostCommentDto(PostComment postComment);
    PostComment toPostComment(PostCommentDto postDto);

    List<PostCommentDto> toPostCommentDtos(List<PostComment> users);
}

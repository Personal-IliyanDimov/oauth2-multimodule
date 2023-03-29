package org.imd.oauth2.resourceserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.imd.oauth2.resourceserver.exception.post.PostNotFoundException;
import org.imd.oauth2.resourceserver.exception.post.PostNotUpdatedException;
import org.imd.oauth2.resourceserver.exception.postcomment.PostCommentAlreadyExistsException;
import org.imd.oauth2.resourceserver.exception.postcomment.PostCommentNotFoundException;
import org.imd.oauth2.resourceserver.exception.postcomment.PostCommentNotUpdatedException;
import org.imd.oauth2.resourceserver.model.domain.PostComment;
import org.imd.oauth2.resourceserver.model.dto.PostCommentDto;
import org.imd.oauth2.resourceserver.model.dto.group.CreateGroup;
import org.imd.oauth2.resourceserver.model.dto.group.UpdateGroup;
import org.imd.oauth2.resourceserver.model.mapper.dto.PostCommentMapper;
import org.imd.oauth2.resourceserver.services.PostCommentService;
import org.imd.oauth2.resourceserver.services.PostService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/posts/{pid}/comments")
@Validated
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentMapper pcMapper;
    private final PostService pService;
    private final PostCommentService pcService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<PostCommentDto>> getPostComments(@PathVariable(name = "pid") final Long pid) throws PostNotFoundException {
        checkPostExists(pid);

        final List<PostComment> posts = pcService.findAll(pid);
        return ResponseEntity.ok(pcMapper.toPostCommentDtos(posts));
    }

    @GetMapping(value = "/{cid}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PostCommentDto> getPostComment(@PathVariable(name = "pid") final Long pid,
                                                  @PathVariable(name = "cid") final Long cid) throws PostNotFoundException, PostCommentNotFoundException {
        checkPostExists(pid);
        checkPostCommentExists(pid, cid);

        final Optional<PostComment> pcOptional = pcService.findPostComment(pid, cid);
        if (pcOptional.isEmpty()) {
            throw new PostCommentNotFoundException(pid, cid);
        }

        return ResponseEntity.ok(pcMapper.toPostCommentDto(pcOptional.get()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PostCommentDto> createPostComment(@PathVariable(name = "pid") final Long pid,
                                                     @RequestBody @Validated(CreateGroup.class) @Valid PostCommentDto pcDto)
            throws PostNotFoundException, PostCommentAlreadyExistsException {

        checkPostExists(pid);

        final PostComment pc = pcMapper.toPostComment(pcDto);
        final PostComment createdPc = pcService.createPostComment(pid, pc);
        return ResponseEntity.ok(pcMapper.toPostCommentDto(createdPc));
    }

    @PutMapping(value = "/{cid}")
    ResponseEntity<PostCommentDto> updatePostComment(@PathVariable(name = "pid") final Long pid,
                                                     @PathVariable(name = "cid") final Long cid,
                                                     @RequestBody @Validated(UpdateGroup.class) @Valid PostCommentDto pcDto)
            throws PostNotFoundException, PostNotUpdatedException, PostCommentNotFoundException, PostCommentNotUpdatedException {

        checkPostExists(pid);
        checkPostCommentExists(pid, cid);

        final PostComment pc = pcMapper.toPostComment(pcDto);
        final PostComment createdPc = pcService.updatePostComment(pid, cid, pc);
        return ResponseEntity.ok(pcMapper.toPostCommentDto(createdPc));
    }

    @DeleteMapping(value = "/{cid}")
    ResponseEntity<?> deletePost(@PathVariable(name = "pid") final Long pid,
                                 @PathVariable(name = "cid") final Long cid) throws PostNotFoundException, PostCommentNotFoundException {
        checkPostExists(pid);
        checkPostCommentExists(pid, cid);

        pcService.deletePostById(pid, cid);
        return ResponseEntity.ok().build();
    }

    private void checkPostExists(Long pid) throws PostNotFoundException {
        if (! pService.postExists(pid)) {
            throw new PostNotFoundException(pid);
        }
    }

    private void checkPostCommentExists(Long pid, Long cid) throws PostCommentNotFoundException {
        if (! pcService.postCommentExists(pid, cid)) {
            throw new PostCommentNotFoundException(pid, cid);
        }
    }
}

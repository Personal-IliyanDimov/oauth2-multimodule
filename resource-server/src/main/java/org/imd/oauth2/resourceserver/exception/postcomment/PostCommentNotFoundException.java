package org.imd.oauth2.resourceserver.exception.postcomment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class PostCommentNotFoundException extends Exception {
    private final Long pid;
    private final Long cid;

}

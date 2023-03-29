package org.imd.oauth2.resourceserver.exception.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class PostNotFoundException extends Exception {
    private final Long id;

}

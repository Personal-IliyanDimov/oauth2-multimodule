package org.imd.oauth2.resourceserver.exception.post;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PostNotUpdatedException extends Exception {
    public PostNotUpdatedException(Exception causedBy) {
        super(causedBy);
    }
}

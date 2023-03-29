package org.imd.oauth2.resourceserver.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import org.imd.oauth2.resourceserver.model.dto.group.CreateGroup;
import org.imd.oauth2.resourceserver.model.dto.group.UpdateGroup;


@Getter
@Setter
public class PostDto {
    @Null(groups = {CreateGroup.class})
    @NotNull(groups = {UpdateGroup.class})
    private Long id;

    @NotNull
    private String title;
}

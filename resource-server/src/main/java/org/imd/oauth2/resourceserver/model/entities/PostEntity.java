package org.imd.oauth2.resourceserver.model.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "Post")
@Table (name = "post", indexes = {
        @Index(unique = true, columnList = "title")
})
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_post")
    @SequenceGenerator(name = "seq_post", initialValue = 1000, allocationSize = 1)
    private Long id;

    private String title;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PostCommentEntity> comments = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PostCommentEntity> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void addComment(PostCommentEntity comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(PostCommentEntity comment) {
        comments.remove(comment);
        comment.setPost(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostEntity)) return false;
        return id != null && id.equals(((PostEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}

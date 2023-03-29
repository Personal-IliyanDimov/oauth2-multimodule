package org.imd.oauth2.resourceserver.model.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity(name = "PostComment")
@Table(name = "post_comment")
public class PostCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_postcomment")
    @SequenceGenerator(name = "seq_postcomment", initialValue = 1000, allocationSize = 1)
    private Long id;

    private String review;

    @ManyToOne(fetch = FetchType.LAZY)
    private PostEntity post;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public PostEntity getPost() {
        return post;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostCommentEntity)) return false;
        return id != null && id.equals(((PostCommentEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}

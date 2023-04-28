# Notes 
- We should explicitly set "spring.jpa.hibernate.ddl-auto: none" \n
  when we are using flyway. Otherwise hibernate may drop and drop-and-update the schema after flyway, \n
  so we may not see any data in our tables. 
- 


# Permission model:
- Everyone who is authorized can read posts
- Everyone who is authorized can create posts
- Everyone who is authorized can ONLY update his own posts
- Everyone who is authorized can ONLY soft delete his own posts

- Everyone who is authorized can read ALL post comments
- Everyone who is authorized can create post comments
- Everyone who is authorized can ONLY update his own posts comments
- Everyone who is authorized can ONLY soft delete his own post comments
  seq_post
- If entity is soft deleted nobody can CUD it.
- Only admin can hard delete data.

# Roles model
- PostAuthor is the person who creates the post.
  -- CRUD on the post. Can not touch the comments.
  -- If post is deleted all comments are deleted (and can not be modified).
  -- Soft delete is used.

- PostCommenter is the person who leaves/adds/creates comments related with the post.
  -- CRUD on his own comments. Can not touch other comments.
  -- Soft delete is used.

- Admin is the person who checks the posts and comments to be according to the rules/practices.
  -- Can do everything with the posts and comments except (deleting them). 

# Solve ACL issues 
- https://github.com/spring-projects/spring-security/issues/5508
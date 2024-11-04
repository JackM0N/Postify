package TTSW.Postify.model;

import TTSW.Postify.interfaces.HasAuthor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "post")
public class Post implements HasAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private WebsiteUser user;

    @Column(name = "description", length = 3000)
    private String description;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Hashtag> hashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    @Size(min = 1, message = "Post must contain at least one media")
    private List<Medium> media = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private Set<Notification> notifications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<PostLike> postLikes = new LinkedHashSet<>();

    @NotNull
    @ColumnDefault("0")
    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "comment_count", nullable = false)
    private Long commentCount;
}

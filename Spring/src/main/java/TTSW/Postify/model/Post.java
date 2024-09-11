package TTSW.Postify.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private WebsiteUser user;

    @Column(name = "description", length = Integer.MAX_VALUE)
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

    @OneToMany(mappedBy = "post")
    private Set<Hashtag> hashtags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<Medium> media = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<Notification> notifications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<PostLike> postLikes = new LinkedHashSet<>();

}
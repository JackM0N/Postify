package TTSW.Postify.repository;

import TTSW.Postify.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollowedId(Long followedId);
    List<Follow> findByFollowerId(Long followerId);
}

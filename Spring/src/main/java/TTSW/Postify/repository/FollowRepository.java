package TTSW.Postify.repository;

import TTSW.Postify.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long>, JpaSpecificationExecutor<Follow> {
    Optional<Follow> findByFollowedIdAndFollowerId(Long followedId, Long followerId);
}

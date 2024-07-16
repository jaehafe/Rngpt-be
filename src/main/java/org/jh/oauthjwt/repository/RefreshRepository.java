package org.jh.oauthjwt.repository;

import jakarta.transaction.Transactional;
import java.util.Optional;
import org.jh.oauthjwt.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);

    Optional<RefreshEntity> findByRefresh(String refresh);
}

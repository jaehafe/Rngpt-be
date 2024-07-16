package org.jh.oauthjwt.refreshToken;

import org.jh.oauthjwt.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshEntity, Long> {
    Optional<RefreshEntity> findByUsername(String username);
    Optional<RefreshEntity> findByEmail(String email);
    Optional<RefreshEntity> findByRefresh(String refreshToken);
    void deleteByUsername(String username);

    void deleteByRefresh(String refreshToken);
}

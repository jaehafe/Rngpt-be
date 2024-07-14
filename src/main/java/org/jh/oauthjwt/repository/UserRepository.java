package org.jh.oauthjwt.repository;

import java.util.Optional;
import org.jh.oauthjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    UserEntity findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);
}

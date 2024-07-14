package org.jh.oauthjwt.service;

import java.util.Optional;
import org.jh.oauthjwt.dto.CustomUserDetails;
import org.jh.oauthjwt.entity.UserEntity;
import org.jh.oauthjwt.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//        Optional<UserEntity> userData = userRepository.findByEmail(username);
//
//        if (userData != null) {
//            return new CustomUserDetails(userData);
//        }
//
//        return null;
//    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + username));

        return new CustomUserDetails(userEntity);
    }
}

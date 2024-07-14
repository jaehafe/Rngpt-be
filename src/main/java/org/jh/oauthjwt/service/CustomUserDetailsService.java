package org.jh.oauthjwt.service;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userData = userRepository.findByEmail(username);

        if (userData != null) {
            return new CustomUserDetails(userData);
        }

        return null;
    }
}

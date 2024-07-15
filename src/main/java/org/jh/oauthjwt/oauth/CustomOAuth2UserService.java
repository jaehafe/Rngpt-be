package org.jh.oauthjwt.oauth;

import org.jh.oauthjwt.entity.UserEntity;
import org.jh.oauthjwt.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2Response oAuth2Response = null;
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        System.out.println("oAuth2User = " + oAuth2User);

        String email = oAuth2Response.getEmail();
        String name = oAuth2Response.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setPassword(""); // OAuth2 사용자는 비밀번호가 없음
                    newUser.setRole("ROLE_USER");
                    newUser.setVerified(true); // OAuth2 로그인은 이미 인증된 것으로 간주
                    newUser.setProvider(registrationId);
                    return userRepository.save(newUser);
                });

        // 기존 사용자의 경우에도 provider가 null이면 업데이트
        if (user.getProvider() == null) {
            user.setProvider(registrationId);
            userRepository.save(user);
        }

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
}

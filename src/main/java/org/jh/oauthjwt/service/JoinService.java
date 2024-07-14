package org.jh.oauthjwt.service;

import java.util.Random;
import org.jh.oauthjwt.dto.JoinDTO;
import org.jh.oauthjwt.dto.VerifyDTO;
import org.jh.oauthjwt.entity.UserEntity;
import org.jh.oauthjwt.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String email = joinDTO.getEmail();
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean isExist = userRepository.existsByEmail(email);

        if (isExist) {

            return;
        }

        UserEntity data = new UserEntity();
        data.setEmail(email);
        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_ADMIN");

        userRepository.save(data);
    }

    public String initiateJoinProcess(JoinDTO joinDTO) {
        String email = joinDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            return "이미 존재하는 이메일입니다.";
        }

        String verificationCode = generateVerificationCode();

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setUsername(joinDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));
        user.setRole("ROLE_USER");
        user.setVerificationCode(verificationCode);
        user.setVerified(false);

        userRepository.save(user);

        emailService.sendVerificationEmail(email, verificationCode);

        return "인증 코드가 이메일로 전송되었습니다.";
    }

    public String verifyEmail(VerifyDTO verifyDTO) {
        String email = verifyDTO.getEmail();
        String code = verifyDTO.getCode();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (user.isVerified()) {
            return "이미 인증된 사용자입니다.";
        }

        if (user.getVerificationCode().equals(code)) {
            user.setVerified(true);
            userRepository.save(user);
            return "인증이 완료되었습니다.";
        } else {
            return "잘못된 인증 코드입니다.";
        }
    }

    private String generateVerificationCode() {
        // 6자리 랜덤 코드 생성 로직
        return String.format("%06d", new Random().nextInt(999999));
    }
}

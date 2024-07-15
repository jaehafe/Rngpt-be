package org.jh.oauthjwt.service;

import org.jh.oauthjwt.dto.JoinDTO;
import org.jh.oauthjwt.dto.VerifyDTO;
import org.jh.oauthjwt.entity.UserEntity;
import org.jh.oauthjwt.global.exception.UserAlreadyVerifiedException;
import org.jh.oauthjwt.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;

    public JoinService(
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            EmailService emailService,
            RedisTemplate<String, String> redisTemplate
    ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }

    public String initiateJoinProcess(JoinDTO joinDTO) {
        String email = joinDTO.getEmail();
        Optional<UserEntity> existingUserOpt = userRepository.findByEmail(email);

        if (existingUserOpt.isPresent()) {
            UserEntity existingUser = existingUserOpt.get();
            if (existingUser.isVerified()) {
                throw new UserAlreadyVerifiedException(400, "Email already exists and is verified.");
            } else {
                // 사용자 인증이 아직 완료되지 않은 경우 새로운 인증 코드 생성 및 업데이트
                String newVerificationCode = generateVerificationCode();
                existingUser.setVerificationCode(newVerificationCode);
                userRepository.save(existingUser);

                saveVerificationCodeToRedis(email, newVerificationCode);

                emailService.sendVerificationEmail(email, newVerificationCode);
                // TODO:
                return "새로운 인증 코드가 이메일로 전송되었습니다.";
            }
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

        saveVerificationCodeToRedis(email, verificationCode);

        emailService.sendVerificationEmail(email, verificationCode);

        // TODO:
        return "인증 코드가 이메일로 전송되었습니다.";
    }

    public String verifyEmail(VerifyDTO verifyDTO) {
        String email = verifyDTO.getEmail();
        String code = verifyDTO.getCode();

        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (user.isVerified()) {
            return "이미 인증된 사용자입니다.";
        }

        String verificationCode = getVerificationCodeFromRedis(email);
        if (verificationCode == null) {
            return "인증 코드가 만료되었습니다.";
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

    private void saveVerificationCodeToRedis(String email, String code) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(email, code, 3, TimeUnit.MINUTES);
    }

    private String getVerificationCodeFromRedis(String email) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(email);
    }
}

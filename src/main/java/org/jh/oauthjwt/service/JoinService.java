package org.jh.oauthjwt.service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.jh.oauthjwt.dto.JoinDTO;
import org.jh.oauthjwt.dto.VerifyDTO;
import org.jh.oauthjwt.entity.UserEntity;
import org.jh.oauthjwt.global.exception.UserAlreadyVerifiedException;
import org.jh.oauthjwt.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final VerificationService verificationService;
    private final RedisTemplate<String, String> redisTemplate;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
            EmailService emailService, VerificationService verificationService,
            RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
        this.verificationService = verificationService;
        this.redisTemplate = redisTemplate;
    }

//    public void joinProcess(JoinDTO joinDTO) {
//
//        String email = joinDTO.getEmail();
//        String username = joinDTO.getUsername();
//        String password = joinDTO.getPassword();
//
//        Boolean isExist = userRepository.existsByEmail(email);
//        if (isExist) {
////            throw new BadRequestException(ExceptionCode.ALREADY_EXISTS_USER);
//            return;
//        }
//
//        UserEntity data = new UserEntity();
//        data.setEmail(email);
//        data.setUsername(username);
//        data.setPassword(bCryptPasswordEncoder.encode(password));
//        data.setRole("ROLE_ADMIN");
//
//        userRepository.save(data);
//    }

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

                // 인증 코드를 Redis에 3분 TTL과 함께 저장
                ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
                valueOps.set(email, newVerificationCode, 1, TimeUnit.MINUTES);

                emailService.sendVerificationEmail(email, newVerificationCode);

                return "새로운 인증 코드가 이메일로 전송되었습니다.";
            }
        }

//        Boolean isAlreadyVerified = userRepository.findByEmail(email).map(UserEntity::isVerified)
//                .orElse(false);
//        // 이메일이 이미 존재하고, 이미 인증된 사용자인 경우
//        if (userRepository.existsByEmail(email) && isAlreadyVerified) {
//            throw new UserAlreadyVerifiedException(400, "Email already exists and is verified.");
//        }

        String verificationCode = generateVerificationCode();

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setUsername(joinDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));
        user.setRole("ROLE_USER");
        user.setVerificationCode(verificationCode);
        user.setVerified(false);

        userRepository.save(user);

        // 인증 코드를 Redis에 3분 TTL과 함께 저장
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(email, verificationCode, 1, TimeUnit.MINUTES);

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

        // Redis에서 인증 코드 가져오기
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String storedCode = valueOps.get(email);

        if (storedCode == null) {
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
}

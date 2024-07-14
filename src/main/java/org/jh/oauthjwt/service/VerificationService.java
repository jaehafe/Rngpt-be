package org.jh.oauthjwt.service;

import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {
    private final RedisTemplate<String, String> redisTemplate;
    private final static long EXPIRE_MINUTES = 3;

    public VerificationService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(EXPIRE_MINUTES));
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        return code.equals(storedCode);
    }

    public void removeVerificationCode(String email) {
        redisTemplate.delete(email);
    }
}

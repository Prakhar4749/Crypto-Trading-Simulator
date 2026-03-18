package com.prakhar.auth.service.impl;

import com.prakhar.auth.entity.TwoFactorOTP;
import com.prakhar.auth.repository.TwoFactorOtpRepository;
import com.prakhar.auth.service.TwoFactorOtpService;
import com.prakhar.auth.utils.OtpUtils;
import com.prakhar.common.enums.VerificationType;
import com.prakhar.common.event.OtpNotificationEvent;
import com.prakhar.common.exception.InvalidOtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TwoFactorOtpServiceImpl implements TwoFactorOtpService {

    private static final Logger logger = LoggerFactory.getLogger(TwoFactorOtpServiceImpl.class);

    private final TwoFactorOtpRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TwoFactorOtpServiceImpl(TwoFactorOtpRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public TwoFactorOTP createOtp(Long userId, String email, String jwt) {
        repository.deleteByUserId(userId);
        String code = OtpUtils.generateOTP();
        
        TwoFactorOTP twoFactorOTP = new TwoFactorOTP();
        twoFactorOTP.setId(UUID.randomUUID().toString());
        twoFactorOTP.setUserId(userId);
        twoFactorOTP.setOtp(code);
        twoFactorOTP.setJwt(jwt);
        twoFactorOTP.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        
        TwoFactorOTP savedOtp = repository.save(twoFactorOTP);
        
        logger.info("Created 2FA OTP for userId: {}", userId);
        
        kafkaTemplate.send("otp-notification", new OtpNotificationEvent(userId, email, code, VerificationType.TWO_FACTOR));
        
        return savedOtp;
    }

    @Override
    public Optional<TwoFactorOTP> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public Optional<TwoFactorOTP> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public boolean verifyOtp(TwoFactorOTP otp, String code) {
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new InvalidOtpException("OTP has expired. Please request a new one");
        }
        if (!otp.getOtp().equals(code)) {
            throw new InvalidOtpException("Invalid OTP. Please try again");
        }
        return true;
    }

    @Override
    public void deleteOtp(String id) {
        repository.deleteById(id);
    }
}

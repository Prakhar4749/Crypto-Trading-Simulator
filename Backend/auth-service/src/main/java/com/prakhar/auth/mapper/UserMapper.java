package com.prakhar.auth.mapper;

import com.prakhar.auth.entity.User;
import com.prakhar.common.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole().name() : "ROLE_USER")
                .isVerified(user.isVerified())
                .isTwoFactorEnabled(user.isTwoFactorEnabled())
                .authProvider(user.getAuthProvider() != null ? user.getAuthProvider().name() : null)
                .signupBonusAvailed(user.isSignupBonusAvailed())
                .isActive(user.isActive())
                .profilePicture(user.getProfilePicture())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .country(user.getCountry())
                .pinCode(user.getPinCode())
                .kycStatus(user.getKycStatus() != null ? user.getKycStatus().name() : "NOT_STARTED")
                .kycVerifiedAt(user.getKycVerifiedAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        // Note: role and kycStatus are enums in User, String in DTO. 
        // Need to handle conversion if this method is used.
        // For now, let's keep it minimal or as required by context.
        return user;
    }
}

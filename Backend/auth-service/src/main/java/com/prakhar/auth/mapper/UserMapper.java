package com.prakhar.auth.mapper;

import com.prakhar.auth.entity.User;
import com.prakhar.common.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setRole(user.getRole());
        dto.setVerified(user.isVerified());
        dto.setTwoFactorEnabled(user.isTwoFactorEnabled());
        return dto;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        user.setRole(dto.getRole());
        user.setVerified(dto.isVerified());
        user.setTwoFactorEnabled(dto.isTwoFactorEnabled());
        return user;
    }
}

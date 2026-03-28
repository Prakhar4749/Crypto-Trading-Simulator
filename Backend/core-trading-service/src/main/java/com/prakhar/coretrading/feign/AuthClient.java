package com.prakhar.coretrading.feign;

import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/internal/users/{userId}")
    ApiResponse<UserDTO> getUserById(
        @PathVariable("userId") Long userId,
        @RequestHeader("X-Internal-Api-Key") String internalApiKey
    );
}

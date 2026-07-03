package io.snackdeal.backand.api.admin.main.controller;

import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.LoginRequest;
import io.snackdeal.backand.api.user.member.dto.TokenResponse;
import io.snackdeal.backand.domain.member.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public CommonResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return CommonResponse.success(authService.adminLogin(request));
    }
}

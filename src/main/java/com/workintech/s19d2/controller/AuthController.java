package com.workintech.s19d2.controller;

import com.workintech.s19d2.dto.RegisterResponse;
import com.workintech.s19d2.dto.RegistrationMember;
import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workintech/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Yeni kullanıcı kaydı.
     * Default olarak ROLE_USER atanır.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegistrationMember registration) {
        // Member nesnesi oluştur
        Member member = Member.builder()
                .email(registration.email())
                .password(registration.password())
                .build();

        // AuthenticationService ile kayıt
        Member created = authenticationService.registerUser(member);

        // Response dön
        return ResponseEntity.ok(
                new RegisterResponse(created.getId(), created.getEmail(), "User registered")
        );
    }
}


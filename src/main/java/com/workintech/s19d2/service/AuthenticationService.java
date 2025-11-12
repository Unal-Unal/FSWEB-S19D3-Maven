package com.workintech.s19d2.service;

import com.workintech.s19d2.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    /**
     * ROLE_USER ile kayıt eder.
     */
    public Member registerUser(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberService.registerWithRole(member, "ROLE_USER");
    }

    /**
     * ROLE_ADMIN ile kayıt eder.
     */
    public Member registerAdmin(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberService.registerWithRole(member, "ROLE_ADMIN");
    }

    /**
     * Basit email + password ile kayıt. Default olarak ROLE_USER atanır.
     * Testlerde kolay kullanım için eklendi.
     */
    public Member register(String email, String password) {
        Member existing = memberService.findByEmail(email);
        if (existing != null) {
            throw new RuntimeException("User with given email already exist");
        }

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        return memberService.registerWithRole(member, "ROLE_USER");
    }


    /**
     * Verilen email mevcutsa Member döner, yoksa null.
     */
    public Member findByEmail(String email) {
        return memberService.findByEmail(email);
    }

    /**
     * Basit parola doğrulama: verilen ham parola ile DB'deki hash'i karşılaştırır.
     */
    public boolean authenticate(String email, String rawPassword) {
        Member member = memberService.findByEmail(email);
        if (member == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, member.getPassword());
    }


}
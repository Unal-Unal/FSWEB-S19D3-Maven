package com.workintech.s19d2.config;

import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.entity.Role;
import com.workintech.s19d2.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final MemberRepository memberRepository;

    public SecurityConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // UserDetailsService (loads from MemberRepository)
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Member member = memberRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            var authorities = member.getRoles().stream()
                    .map(Role::getAuthority)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
            return new User(member.getEmail(), member.getPassword(), authorities);
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF disable for testing simplicity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Registration endpoint open to everyone
                        .requestMatchers("/workintech/auth/register").permitAll()
                        // OAuth2 endpoints
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        // GET /accounts accessible by USER and ADMIN
                        .requestMatchers("/workintech/accounts/**").hasAnyRole("USER", "ADMIN")
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                // Enable HTTP Basic authentication
                .httpBasic(Customizer.withDefaults())
                // Enable OAuth2 login for GitHub
                .oauth2Login(Customizer.withDefaults());

        return http.build();
    }
}

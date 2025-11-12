package com.workintech.s19d2.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workintech.s19d2.repository.MemberRepository;
import com.workintech.s19d2.repository.RoleRepository;
import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.entity.Role;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository,
                             RoleRepository roleRepository,
                             PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Member register(Member member) {
        return registerWithRole(member, "ROLE_USER");
    }

    @Override
    public Member registerWithRole(Member member, String roleAuthority) {
        Optional<Role> r = roleRepository.findByAuthority(roleAuthority);
        Role role = r.orElseGet(() -> roleRepository.save(Role.builder().authority(roleAuthority).build()));

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        member.setRoles(roles);
        return memberRepository.save(member);
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }
}
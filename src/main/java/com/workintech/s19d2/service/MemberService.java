package com.workintech.s19d2.service;

import com.workintech.s19d2.entity.Member;

public interface MemberService {
    Member register(Member member); // default role USER
    Member registerWithRole(Member member, String roleAuthority);
    Member findByEmail(String email);
}
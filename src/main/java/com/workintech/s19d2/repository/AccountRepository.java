package com.workintech.s19d2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.workintech.s19d2.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
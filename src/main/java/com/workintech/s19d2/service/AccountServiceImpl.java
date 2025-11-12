package com.workintech.s19d2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workintech.s19d2.repository.AccountRepository;
import com.workintech.s19d2.entity.Account;

import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account update(Long id, Account account) {
        return accountRepository.findById(id).map(existing -> {
            existing.setName(account.getName());
            return accountRepository.save(existing);
        }).orElse(null);
    }

    @Override
    public void delete(Long id) {
        accountRepository.deleteById(id);
    }
}

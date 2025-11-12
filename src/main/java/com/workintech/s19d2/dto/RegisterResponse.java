package com.workintech.s19d2.dto;

/**
 * Kayıt işleminin response DTO'su.
 */
public record RegisterResponse(Long id, String email, String message) {}

package com.workintech.s19d2;

import com.workintech.s19d2.dto.RegisterResponse;
import com.workintech.s19d2.dto.RegistrationMember;
import com.workintech.s19d2.entity.Account;
import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.entity.Role;
import com.workintech.s19d2.repository.AccountRepository;
import com.workintech.s19d2.repository.MemberRepository;
import com.workintech.s19d2.repository.RoleRepository;
import com.workintech.s19d2.service.AccountServiceImpl;
import com.workintech.s19d2.service.AuthenticationService;
import com.workintech.s19d2.service.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@ExtendWith(ResultAnalyzer.class)
class MainTest {

    private Member member;
    private Role roleUser;
    private Role roleAdmin;

    private Role role;

    @Mock
    private PasswordEncoder passwordEncoder;
    private AccountServiceImpl accountService;

    private Account account;
    private AuthenticationService authenticationService;

    @Mock
    private AccountRepository mockAccountRepository;

    @Mock
    private MemberRepository mockMemberRepository;

    @Mock
    private RoleRepository mockRoleRepository;


    @BeforeEach
    void setUp() {

        // Member ve Role objeleri
        member = new Member();
        roleUser = new Role();
        roleAdmin = new Role();

        roleUser.setId(1L);
        roleUser.setAuthority("USER");
        roleAdmin.setId(2L);
        roleAdmin.setAuthority("ADMIN");

        member.setEmail("test@example.com");
        member.setPassword("password");
        member.setRoles(Set.of(roleUser, roleAdmin));

        // role objesini initialize et (grantedAuthorityImplementation ve roleSettersAndGetters için)
        role = new Role();
        role.setId(1L);
        role.setAuthority("USER");

        // Account objesi
        account = new Account();
        account.setId(1L);
        account.setName("Test Account");

        // Mock repository
        mockAccountRepository = mock(AccountRepository.class);
        mockMemberRepository = mock(MemberRepository.class);
        mockRoleRepository = mock(RoleRepository.class);

        // Password encoder
        passwordEncoder = new BCryptPasswordEncoder();

        // MemberService oluştur (mock repository ile)
        MemberServiceImpl memberService = new MemberServiceImpl(mockMemberRepository, mockRoleRepository, passwordEncoder);

        // AuthenticationService oluştur (MemberService ve PasswordEncoder ile)
        authenticationService = new AuthenticationService(memberService, passwordEncoder);

        // AccountService oluştur
        accountService = new AccountServiceImpl(mockAccountRepository);
    }

    @Test
    @DisplayName("Member Setters and Getters")
    void memberSettersAndGetters() {
        assertEquals("test@example.com", member.getEmail(), "The email should match the one set.");
        assertEquals("password", member.getPassword(), "The password should match the one set.");
    }

    @Test
    @DisplayName("Member Authorities")
    void memberAuthorities() {
        Collection<? extends GrantedAuthority> authorities = member.getAuthorities();

        assertNotNull(authorities, "Authorities collection should not be null");
        assertEquals(2, authorities.size(), "There should be two roles assigned to the member");

        List<String> roleNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertTrue(roleNames.containsAll(Arrays.asList("USER", "ADMIN")),
                "The authorities should include USER and ADMIN roles");
    }


    @Test
    @DisplayName("UserDetails Implementations")
    void userDetailsImplementations() {
        assertEquals(member.getEmail(), member.getUsername(), "The username should be the same as the email.");
        assertTrue(member.isAccountNonExpired(), "The account should not be expired.");
        assertTrue(member.isAccountNonLocked(), "The account should not be locked.");
        assertTrue(member.isCredentialsNonExpired(), "The credentials should not be expired.");
        assertTrue(member.isEnabled(), "The account should be enabled.");
    }

    @Test
    void testAccountSettersAndGetters() {

        Long expectedId = 1L;
        String expectedName = "Test Account";


        Account account = new Account();
        account.setId(expectedId);
        account.setName(expectedName);


        assertEquals(expectedId, account.getId(), "The ID returned was not the same value set.");
        assertEquals(expectedName, account.getName(), "The name returned was not the same value set.");
    }

    @Test
    @DisplayName("Role Setters and Getters")
    void roleSettersAndGetters() {
        assertEquals(1L, role.getId(), "The id should match the one set.");
        assertEquals("USER", role.getAuthority(), "The authority should match the one set.");
    }

    @Test
    @DisplayName("GrantedAuthority Implementation")
    void grantedAuthorityImplementation() {

        String authority = role.getAuthority();

        assertNotNull(authority, "Authority should not be null.");
        assertEquals("USER", authority, "The authority should return 'USER'.");
    }


    @Test
    @DisplayName("AccountRepository should be  instance of JpaRepository")
    void accountRepositoryInstanceCheck() {

        assertTrue(mockAccountRepository instanceof JpaRepository, "MovieRepository should be an instance of JpaRepository");
    }

    @Test
    @DisplayName("MemberRepository should be  instance of JpaRepository")
    void memberRepositoryInstanceCheck() {

        assertTrue(mockMemberRepository instanceof JpaRepository, "MemberRepository should be an instance of JpaRepository");
    }

    @Test
    @DisplayName("RoleRepository should be  instance of JpaRepository")
    void roleRepositoryInstanceCheck() {

        assertTrue(mockRoleRepository instanceof JpaRepository, "RoleRepository should be an instance of JpaRepository");
    }

    @Test
    @DisplayName("RegisterResponse Data Storage")
    void registerResponseDataStorage() {
        Long id = 1L; // Örnek ID
        String email = "test@example.com";
        String message = "Registration successful";

        // Artık "created" yok, direkt değerleri veriyoruz
        RegisterResponse response = new RegisterResponse(id, email, message);

        assertEquals(email, response.email(), "Email should match the one provided");
        assertEquals(message, response.message(), "Message should match the one provided");
        assertEquals(id, response.id(), "ID should match the one provided");
    }



    @Test
    @DisplayName("RegistrationMember Data Storage")
    void registrationMemberDataStorage() {
        String email = "user@example.com";
        String password = "securePassword123";

        RegistrationMember member = new RegistrationMember(email, password);

        assertEquals(email, member.email(), "Email should match the one provided");
        assertEquals(password, member.password(), "Password should match the one provided");
    }

    @Test
    @DisplayName("Find All Accounts")
    void findAll() {
        given(mockAccountRepository.findAll()).willReturn(Arrays.asList(account));

        List<Account> accounts = accountService.findAll();

        assertThat(accounts).isNotNull();
        assertThat(accounts.size()).isEqualTo(1);
        assertThat(accounts.get(0)).isEqualTo(account);
    }

    @Test
    @DisplayName("Save Account")
    void save() {
        when(mockAccountRepository.save(account)).thenReturn(account);

        Account savedAccount = accountService.save(account);

        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getId()).isEqualTo(account.getId());
        verify(mockAccountRepository).save(account);
    }

    @Test
    @DisplayName("Register New Member Successfully")
    void registerNewMemberSuccessfully() {
        // role nesnesi null olmamalı
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setAuthority("ADMIN");

        // Doğru stubbing
        when(mockMemberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(mockRoleRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(adminRole));
        when(mockMemberRepository.save(any(Member.class))).thenReturn(member);

        // register çağrısı
        Member registeredMember = authenticationService.register("test@example.com", "password");

        // assert
        assertEquals("test@example.com", registeredMember.getEmail());
        assertEquals("encodedPassword", registeredMember.getPassword());

        // verify
        verify(mockMemberRepository).save(any(Member.class));
    }





    @Test
    @DisplayName("Register Member Throws Exception When Email Exists")
    void registerMemberThrowsExceptionWhenEmailExists() {
        given(mockMemberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        assertThatThrownBy(() -> authenticationService.register("test@example.com", "password"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User with given email already exist");
    }
}

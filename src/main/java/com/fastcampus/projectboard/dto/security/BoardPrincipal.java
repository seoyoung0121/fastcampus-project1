package com.fastcampus.projectboard.dto.security;

import com.fastcampus.projectboard.dto.UserAccountDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.stream.Collectors;

import java.util.Collection;
import java.util.Set;

public record BoardPrincipal (String username,
                              String password,
                              Collection<? extends GrantedAuthority> authorities,
                              String email,
                              String nickname,
                              String memo) implements UserDetails {

    public static BoardPrincipal of(String username,String password, String email, String nickname, String memo){
        Set<RoleType> roleTypes= Set.of(RoleType.USER);
        return new BoardPrincipal(username, password,
                roleTypes.stream().map(RoleType::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet()), //권한 여기서 넣어줌
                email, nickname, memo);
    }

    public static BoardPrincipal from(UserAccountDto dto){
        return BoardPrincipal.of(
                dto.userId(),
                dto.userPassword(),
                dto.email(),
                dto.nickname(),
                dto.memo()
        );
    }

    public UserAccountDto toDto(){
        return UserAccountDto.of(
                username,
                password,
                email,
                nickname,
                memo);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum RoleType{
        USER("ROLE_USER"); //권한은 이거 하나
        @Getter private final String name;
        RoleType(String name){
            this.name=name;
        }

    }
}

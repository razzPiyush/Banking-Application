package com.common.BankData.entity.security;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Authority implements GrantedAuthority {

    private final String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
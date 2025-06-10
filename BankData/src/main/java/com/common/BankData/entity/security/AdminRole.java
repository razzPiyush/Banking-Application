package com.common.BankData.entity.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

import com.common.BankData.entity.Admin;

@Entity
@Table(name = "admin_roles")
@Getter
@Setter
@NoArgsConstructor
public class AdminRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminRoleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public AdminRole(Admin admin, Role role) {
        this.admin = admin;
        this.role = role;
    }
}
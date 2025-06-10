package com.common.BankData.entity;

import com.common.BankData.entity.security.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long adminId;

    @Column(name = "admin_name")
    private String adminName;

    @Column(name = "username", unique = true)
    private String userName;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "bank_ifsc")
    private String bankIFSC;

    private String token;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "admin_roles",
        joinColumns = @JoinColumn(name = "admin_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> adminRoles = new ArrayList<>();

    public Admin(Admin other) {
        this.adminId = other.adminId;
        this.adminName = other.adminName;
        this.adminRoles = other.adminRoles;
        this.userName = other.userName;
        this.bankIFSC = other.bankIFSC;
        this.passwordHash = other.passwordHash;
        this.token = other.token;
    }
}
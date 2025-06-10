package com.common.BankData.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "username", unique = true)
    private String userName;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "user_id")
    @ColumnDefault("0")
    private long userId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Set<Account> accounts = new HashSet<>();

    private String token;

    // Copy constructor
    public Customer(final Customer other) {
        this.customerId = other.customerId;
        this.customerName = other.customerName;
        this.userName = other.userName;
        this.password = other.password;
        this.token = other.token;
        this.accounts = other.accounts;
        this.userId = other.userId;
    }

    // Constructor for new customer creation
    public Customer(String customerName, String userName, String password, Set<Account> accounts, String token, long userId) {
        this.customerName = customerName;
        this.userName = userName;
        this.password = password;
        this.accounts = accounts;
        this.token = token;
        this.userId = userId;
    }
}
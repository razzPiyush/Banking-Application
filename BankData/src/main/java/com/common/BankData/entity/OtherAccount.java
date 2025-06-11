package com.common.BankData.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OtherAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "account_name")
    private String accountName;

    private String bankIfsc;
    private String firstName;
    private String lastName;

    @Column(name = "balance")
    @ColumnDefault("0.0")
    private double balance = 0.0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    @Column(columnDefinition = "integer default 0")
    private int accountStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @Column(nullable = true)
    private long accountId;
}
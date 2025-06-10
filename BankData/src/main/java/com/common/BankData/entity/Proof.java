package com.common.BankData.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Proof {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int proofId;

    @Column(nullable = false, unique = true)
    private long uuid;

    @Column(unique = true)
    private String passportNumber;

    @Column(nullable = false)
    private String emailId;

    private Date dob;
    private int age;

    @OneToOne(mappedBy = "proof")
    @JsonBackReference
    private Account acc;
}
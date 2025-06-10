package com.common.BankData.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String bankIfsc;
    private String firstName;
    private String lastName;

    @ColumnDefault("0")
    private long phoneNo;

    @ColumnDefault("0.0")
    @Column(name = "balance")
    private double balance = 0.0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    @Column(columnDefinition = "integer default 0")
    private int accountStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "proofid")
    @JsonManagedReference
    private Proof proof;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = true)
    private long accountId;

    private String remarks;
}
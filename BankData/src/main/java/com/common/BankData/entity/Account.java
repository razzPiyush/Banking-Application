package com.common.BankData.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.annotations.ColumnDefault;
import javax.persistence.*;
import java.sql.Date;

@Entity
public class Account {
    private static final Logger logger = LoggerFactory.getLogger(Account.class);

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

    // Standard getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getBankIfsc() { return bankIfsc; }
    public void setBankIfsc(String bankIfsc) { this.bankIfsc = bankIfsc; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public long getPhoneNo() { return phoneNo; }
    public void setPhoneNo(long phoneNo) { this.phoneNo = phoneNo; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) {
        logger.info("Updating balance for account {} from {} to {}", this.accountId, this.balance, balance);
        this.balance = balance;
    }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getAccountStatus() { return accountStatus; }
    public void setAccountStatus(int accountStatus) { this.accountStatus = accountStatus; }

    public Proof getProof() { return proof; }
    public void setProof(Proof proof) { this.proof = proof; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    @PrePersist
    protected void onCreate() {
        logger.info("Creating new account with ID: {}", this.accountId);
    }

    @PreUpdate
    protected void onUpdate() {
        logger.info("Updating account: {}", this.accountId);
    }
}
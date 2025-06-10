package com.common.BankData.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PrimaryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Date date;

    @Column(name = "local_date_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime localDateTime;

    private String description;
    private String type;
    private String status;
    private double amount;

    @Transient
    private BigDecimal availableBalance;

    private String recipientName;
    private long recipientAccountNo;

    @Transient
    private long phoneNo;

    private long accountId;

    public PrimaryTransaction(Date date, String description, String status, 
                            double amount, String recipientName, 
                            long recipientAccountNo, long accountId, 
                            LocalDateTime localDateTime, String type) {
        this.date = date;
        this.description = description;
        this.status = status;
        this.amount = amount;
        this.recipientName = recipientName;
        this.recipientAccountNo = recipientAccountNo;
        this.accountId = accountId;
        this.localDateTime = localDateTime;
        this.type = type;
    }
}
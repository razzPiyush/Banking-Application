package com.common.BankData.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Entity(name = "schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scheduleid;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dates;

    private long recipientAccountNo;
    private String status;
    private String recipientName;
    private double amount;
    private String type;

    @Column(name = "schedule_type")
    private String scheduleType;

    @Column(nullable = true)
    private long accountId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastProcessedDate;

    // Constructor for scheduled transactions
    public Schedule(int scheduleid, Date dates, long recipientAccountNo, 
                   String status, String recipientName, double amount, 
                   String type, long accountId) {
        this.scheduleid = scheduleid;
        this.dates = dates;
        this.recipientAccountNo = recipientAccountNo;
        this.status = status;
        this.recipientName = recipientName;
        this.amount = amount;
        this.type = type;
        this.accountId = accountId;
    }
}
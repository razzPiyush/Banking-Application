package com.common.BankData.dao;

import com.common.BankData.entity.PrimaryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface TransferDao extends JpaRepository<PrimaryTransaction, Long> {
    
    Set<PrimaryTransaction> findByAccountId(long accountId);
    
    Set<PrimaryTransaction> findByRecipientAccountNo(long accountId);
}
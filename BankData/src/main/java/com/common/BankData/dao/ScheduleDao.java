package com.common.BankData.dao;

import com.common.BankData.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface ScheduleDao extends JpaRepository<Schedule, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Schedule s WHERE s.scheduleid = :scheduleId")
    int removeByScheduleid(@Param("scheduleId") int scheduleId);
}
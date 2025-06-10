package com.banking.batch.TransactionScheduling.config;

import com.common.BankData.entity.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration extends DefaultBatchConfigurer {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final JpaTransactionManager transactionManager;
    private final DBWriter dbWriter;

    private static final int CHUNK_SIZE = 10;
    private static final String SCHEDULE_QUERY =
            "SELECT scheduleid, accountid, amount, dates, " +
            "recipientaccountno, recipientName, status, type " +
            "FROM schedule";

    @Override
    protected JobRepository createJobRepository() throws Exception {
        MapJobRepositoryFactoryBean factoryBean = new MapJobRepositoryFactoryBean();
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    @Bean
    public JdbcCursorItemReader<Schedule> reader() {
        JdbcCursorItemReader<Schedule> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(SCHEDULE_QUERY);
        reader.setRowMapper(new ScheduleRowMapper());
        return reader;
    }

    @Bean
    public UserItemProcessor processor() {
        return new UserItemProcessor();
    }

    @Bean
    public Step processSchedulesStep() {
        return stepBuilderFactory.get("processSchedules")
                .transactionManager(transactionManager)
                .<Schedule, Schedule>chunk(CHUNK_SIZE)
                .reader(reader())
                .processor(processor())
                .writer(dbWriter)
                .build();
    }

    @Bean
    public Job scheduleProcessingJob() {
        return jobBuilderFactory.get("scheduleProcessingJob")
                .incrementer(new RunIdIncrementer())
                .flow(processSchedulesStep())
                .end()
                .build();
    }

    private static class ScheduleRowMapper implements RowMapper<Schedule> {
        @Override
        public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
            Schedule schedule = new Schedule();
            schedule.setScheduleid(rs.getInt("scheduleid"));
            schedule.setAccountId(rs.getLong("accountid"));
            schedule.setAmount(rs.getFloat("amount"));
            schedule.setDates(rs.getDate("dates"));
            schedule.setRecipientAccountNo(rs.getLong("recipientaccountno"));
            schedule.setRecipientName(rs.getString("recipientName"));
            schedule.setStatus(rs.getString("status"));
            schedule.setType(rs.getString("type"));
            return schedule;
        }
    }
}
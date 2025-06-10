package com.common.BankData.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleList {
    
    private int id;
    private List<Schedule> schedule = new ArrayList<>();
}
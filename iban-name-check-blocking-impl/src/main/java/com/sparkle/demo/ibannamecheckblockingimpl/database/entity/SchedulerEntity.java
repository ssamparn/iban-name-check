package com.sparkle.demo.ibannamecheckblockingimpl.database.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "shedlock")
@Table(name = "scheduler_entity")
public class SchedulerEntity {

    @Id
    private String name;

    private Timestamp lock_until;

    private Timestamp locked_at;

    private String locked_by;

}

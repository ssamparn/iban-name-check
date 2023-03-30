package com.sparkle.demo.ibannamecheckblockingimpl;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT10S")
@SpringBootApplication
public class IbanNameCheckBlockingImplApplication {

	public static void main(String[] args) {
		SpringApplication.run(IbanNameCheckBlockingImplApplication.class, args);
	}

}

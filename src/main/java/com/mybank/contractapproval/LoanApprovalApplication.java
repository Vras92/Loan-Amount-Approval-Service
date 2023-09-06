package com.mybank.contractapproval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LoanApprovalApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoanApprovalApplication.class, args);
	}
}

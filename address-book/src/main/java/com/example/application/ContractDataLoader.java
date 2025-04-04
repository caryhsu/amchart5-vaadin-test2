package com.example.application;

import com.example.application.repo.ContractRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class ContractDataLoader implements CommandLineRunner {

    private final ContractRepository contractRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (contractRepository.count() == 0) { // 避免重複插入
            contractRepository.save(new Contract("John", "Doe", "john.doe@example.com", "123-456-7890"));
            contractRepository.save(new Contract("Jane", "Smith", "jane.smith@example.com", "098-765-4321"));
            contractRepository.save(new Contract("Alice", "Johnson", "alice.johnson@example.com", "456-789-0123"));
            System.out.println("Test contracts saved.");
        }
    }
}

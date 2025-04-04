package com.example.application.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.application.Contract;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {
}

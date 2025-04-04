package com.example.application.services;

import com.example.application.Contract;
import com.example.application.ContractDTO;
import com.example.application.repo.ContractRepository;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.crud.ListRepositoryService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@BrowserCallable
@AnonymousAllowed
public class ContractService {

    private final ContractRepository contractRepository;

    public List<ContractDTO> findAll() {
        return contractRepository.findAll().stream()
                .map(contract -> {
                    ContractDTO dto = new ContractDTO();
                    dto.setId(contract.getId());
                    dto.setFirstName(contract.getFirstName());
                    dto.setLastName(contract.getLastName());
                    dto.setEmail(contract.getEmail());
                    dto.setPhone(contract.getPhone());
                    return dto;
                })
                .collect(Collectors.toList());
    }

}

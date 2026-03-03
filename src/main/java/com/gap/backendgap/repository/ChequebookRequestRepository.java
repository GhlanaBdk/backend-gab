package com.gap.backendgap.repository;

import com.gap.backendgap.entity.ChequebookRequest;
import com.gap.backendgap.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChequebookRequestRepository
        extends JpaRepository<ChequebookRequest, Long> {

    List<ChequebookRequest> findByAccount(Account account);

    List<ChequebookRequest> findAllByOrderByRequestedAtDesc();

}

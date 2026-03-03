package com.gap.backendgap.repository;

import com.gap.backendgap.entity.Account;
import com.gap.backendgap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByRib(String rib);

    List<Account> findByUser(User user);

}

package com.bank.service;

import com.bank.dto.TransferMessage;
import com.bank.entity.Account;
import com.bank.repository.AccountDao;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {
    private final AccountDao accountDao;

    private final Map<Long, Account> accountMap = new HashMap<>();
    private final List<Long> accountIdList = new ArrayList<>();

    @Value("${app.kafka.topic}")
    private String topicName;
    private final KafkaTemplate<String, TransferMessage> kafkaTemplate;

    @PostConstruct
    @Transactional
    public void init() {
        List<Account> accounts = accountDao.findAll();

        if (accounts.isEmpty()) {
            for (int i = 0; i < 1000; i++) {
                accounts.add(Account.builder().balance(BigDecimal.valueOf(10000)).build());
            }
            accountDao.save(accounts);
        }

        for (Account account : accounts) {
            accountMap.put(account.getId(), account);
            accountIdList.add(account.getId());
        }
    }


}

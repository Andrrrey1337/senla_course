package com.bank.service;

import com.bank.dto.TransferMessage;
import com.bank.entity.Account;
import com.bank.repository.AccountDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {
    private final AccountDao accountDao;

    private final Map<Long, Account> accountMap = new HashMap<>();
    private final List<Long> accountIdList = new ArrayList<>();

    @Value("${app.kafka.topic}")
    private String topicName;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional("transactionManager")
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

    // запускаем метод каждые 200 мс (но отсчет начинается только после завершения предыдущего вызова)
    @Scheduled(fixedDelay = 200)
    @Transactional("kafkaTransactionManager")
    public void SendTransfer() {
        int totalAcc = accountIdList.size();

        if (totalAcc < 2) {
            log.info("Ожидание генерации счетов...");
            return;
        }

        int fromIndex = ThreadLocalRandom.current().nextInt(totalAcc);
        int toIndex;
        do {
            toIndex = ThreadLocalRandom.current().nextInt(totalAcc);
        } while (fromIndex == toIndex);

        Long fromAccountId = accountIdList.get(fromIndex);
        Long toAccountId = accountIdList.get(toIndex);

        double randomAmount = 990 * ThreadLocalRandom.current().nextDouble() + 100;
        BigDecimal amount = BigDecimal.valueOf(randomAmount).setScale(4, RoundingMode.HALF_UP);

        TransferMessage message = TransferMessage.builder()
                .id(UUID.randomUUID())
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(amount)
                .build();

        log.info("Отправка сообщения о переводе: ID={}, Сумма={}, Отправитель={}, Получатель={}",
                message.getId(), message.getAmount(), message.getFromAccountId(), message.getToAccountId());

        // Отправляем DTO в топик Kafka
        kafkaTemplate.send(topicName, message.getId().toString(), message);
    }
}

package com.bank.service;


import com.bank.dto.TransferMessage;
import com.bank.entity.Account;
import com.bank.entity.Transfer;
import com.bank.repository.AccountDao;
import com.bank.repository.TransferDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("consumer")
public class ConsumerService {
    private final AccountDao accountDao;
    private final TransferDao transferDao;

    String status = "готово";

    Account accountFrom = null;
    Account accountTo = null;

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "bank-consumer-group")
    @Transactional("transactionManager")
    public void consumeTransfer(List<TransferMessage> messages) {
        for (TransferMessage message : messages) {
            log.info("Получено сообщение на перевод: ID={}, Сумма={}", message.getId(), message.getAmount());
            try {
                accountFrom = accountDao.findById(message.getFromAccountId());
                accountTo = accountDao.findById(message.getToAccountId());

                // проверка существования аккаунтов
                if (accountFrom == null || accountTo == null) {
                    throw new IllegalArgumentException("Один или оба счета не найдены в БД");
                }

                if (accountFrom.getBalance().compareTo(message.getAmount()) < 0) {
                    throw new IllegalArgumentException("Недостаточно средств на счете");
                }

                accountFrom.setBalance(accountFrom.getBalance().subtract(message.getAmount()));
                accountTo.setBalance(accountTo.getBalance().add(message.getAmount()));

                log.info("Перевод {} успешно выполнен!", message.getId());


            } catch (Exception e) {
                log.warn("Ошибка при выполнении перевода {}: {}", message.getId(), e.getMessage());
                status = "завершилось с ошибкой";
            }

            // создаем пустышку с ID, чтобы Hibernate мог заполнить колонку from_account_id
            if (accountFrom == null) {
                accountFrom = Account.builder().id(message.getFromAccountId()).build();
            }
            if (accountTo == null) {
                accountTo = Account.builder().id(message.getToAccountId()).build();
            }

            Transfer transfer = Transfer.builder()
                    .id(message.getId())
                    .fromAccount(accountFrom)
                    .toAccount(accountTo)
                    .amount(message.getAmount())
                    .status(status)
                    .build();

            // Сохраняем запись о переводе
            transferDao.save(transfer);
        }
    }
}

package com.bank.repository;

import com.bank.entity.Transfer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class TransferDao {
    @PersistenceContext
    private EntityManager em;

    public void save(Transfer transfer) {
        em.persist(transfer);
    }
}

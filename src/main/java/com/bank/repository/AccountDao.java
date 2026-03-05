package com.bank.repository;

import com.bank.entity.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountDao {
    @PersistenceContext
    private EntityManager em;

    public List<Account> findAll() {
        return em.createQuery("select a from Account a", Account.class).getResultList();
    }

    public Account findById(Long id) {
        return em.find(Account.class, id);
    }

    public void save(List<Account> accounts) {
        for (Account account : accounts) {
            em.persist(account);
        }
    }
}

package com.example.springtx.propagation;


import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManger;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }


    @Test
    void commit() {
        log.info("트랜잭션 시작");
        TransactionStatus status = txManger.getTransaction(new DefaultTransactionAttribute());


        log.info("트랜잭션 커밋 시작");
        txManger.commit(status);
        log.info("트랜잭션 커밋 완료");
    }

    @Test
    void rollback() {
        log.info("트랜잭션 시작");
        TransactionStatus status = txManger.getTransaction(new DefaultTransactionAttribute());


        log.info("트랜잭션 롤백 시작");
        txManger.rollback(status);
        log.info("트랜잭션 롤백 완료");
    }

    @Test
    void double_commit() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋 시작");
        txManger.commit(tx1);

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 커밋 시작");
        txManger.commit(tx2);

    }

    @Test
    void double_commit_rollback() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋 시작");
        txManger.commit(tx1);

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 롤백");
        txManger.rollback(tx2);

    }

    @Test
    void inner_commit() {

        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction={}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction={}", inner.isNewTransaction());
        log.info("내부 트랜잭션 커밋");
        txManger.commit(inner);


        log.info("외부 트랜잭션 커밋");
        txManger.commit(outer);


    }

    @Test
    void outer_rollback() { //외부트랜잭션 롤백

        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManger.getTransaction(new DefaultTransactionAttribute());
        //DB INSERT A

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("내부 트랜잭션 커밋");

        //DB INSERT B
        txManger.commit(inner);

        //문제 발생 !!!!!
        log.info("외부 트랜잭션 롤백");
        txManger.rollback(outer);

        //외부 트랜잭션이 롤백되면 내부 트랜잭션도 롤백

    }


    @Test
    void inner_rollback() { //내부 트랜잭션 롤백

        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManger.getTransaction(new DefaultTransactionAttribute());
        //DB INSERT A

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManger.getTransaction(new DefaultTransactionAttribute());
        log.info("내부 트랜잭션 롤백");

        //문제 발생
        txManger.rollback(inner); //rollback-only 표시

        log.info("외부 트랜잭션 커밋");

        Assertions.assertThatThrownBy(()->txManger.commit(outer))
            .isInstanceOf(UnexpectedRollbackException.class);// UnexpectedRollbackException 발생 시스템 입장에서 커밋을 시도했으나 롤백되었음을 알려야함

        //외부 트랜잭션이 롤백되면 내부 트랜잭션도 롤백

    }






}

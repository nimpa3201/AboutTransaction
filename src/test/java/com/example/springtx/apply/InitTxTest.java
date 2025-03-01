package com.example.springtx.apply;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.awt.desktop.AppForegroundListener;

@Slf4j
@SpringBootTest
public class InitTxTest {


    @Autowired Hello hello;

    @Test
    void go(){
        //초기화 코드는 스프링이 초기화 시점에 호출한다.
    }




    @TestConfiguration
    static class InitTxTestConfig{
        @Bean
        Hello hello(){
            return new Hello();
        }

    }




    static class Hello {
        @PostConstruct
        @Transactional
        public void initV1() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active={}", isActive);

            // 초기화 코드 PostConstruct가 먼저 호출되고, 그 다음에 트랙잭션 AOP가 적용되기 때문
            //따라서 초기화 시점에는 해당 메서드에서 트랜잭션을 획들할 수 없다.
        }

        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void initV2(){
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init ApplicationReadyEvent tx active={}", isActive);

        }

    }

}

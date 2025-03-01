package com.example.springtx.apply;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired
    CallService callService;


    @Test
    void printProxy(){
        log.info("callService class={}",callService.getClass());
    }

    @Test
    void internalCall(){
        callService.internal();
    }

    @Test
    void externalCall(){
        callService.external();
    }


    @TestConfiguration
    static class InternalCallV1TestConfig{

        @Bean
        CallService callService(){
            return new CallService();
        }
    }

    static class CallService{
        public void external(){
            log.info("call external");
            this.printInfo();
            this.internal(); // this(생략) 나 자신의 인스턴스 호출 --> 내부 호출은 프록시를 무시함.
        }

        @Transactional
        public void internal(){
            log.info("call internal");
            printInfo();
        }

        private void printInfo(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}",txActive);
        }

    }
}


// 프록시 방식의 AOP 한계
//@Transactional을 사용하는 트랙잭션 AOP는 프록시를 사용한다. 프록시를 사용하면 메서드 내부 호출에 프록시를 적용할 수 없다.
//해결 - > 내부 호출을 피하기 위해 internall() 메서드를 별도의 클래스로 분리

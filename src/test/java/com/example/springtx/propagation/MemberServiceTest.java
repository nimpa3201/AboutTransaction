package com.example.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;


    /**
     * memberService    @Transactionl : OFF
     * memberRepository @Transactionl : ON
     * logRepository   @Transactionl : ON
     */
    @Test
    void outerTxOff_success() {

        //given
        String username = " outerTxOff_success";


        //when
        memberService.joinV1(username);

        //then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }


    /**
     * memberService    @Transactionl : OFF
     * memberRepository @Transactionl : ON
     * logRepository   @Transactionl : ON Excption
     */
    @Test
    void outerTxOff_fail() {

        //given
        String username = "로그예외_outerTxOff_fail";


        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> memberService.joinV1(username))
            .isInstanceOf(RuntimeException.class);

        //then
        assertTrue(memberRepository.find(username).isPresent()); // memger 저장( 커밋 성공)
        assertTrue(logRepository.find(username).isEmpty()); // 롤백

    }


    /**
     * memberService    @Transactionl : ON
     * memberRepository @Transactionl : OFF
     * logRepository   @Transactionl : OFF
     */
    @Test
    void sigle_Tx() { // 같은 트랜잭션 사용하여 같은 커넥션 1개 사용

        //given
        String username = " outerTxOff_success";


        //when
        memberService.joinV1(username);

        //then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }


    /**
     * memberService    @Transactionl : ON
     * memberRepository @Transactionl : ON
     * logRepository   @Transactionl : ON
     */
    @Test
    void outerTxON_success() { //논리 트랜잭션 생김

        //given
        String username = " outerTxOn_success";


        //when
        memberService.joinV1(username);

        //then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }

    /**
     * memberService    @Transactionl : ON
     * memberRepository @Transactionl : ON
     * logRepository   @Transactionl : ON Excption
     */
    @Test
    void outerTxOn_fail() {

        //given
        String username = "로그예외_outerTxOn_fail";


        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> memberService.joinV1(username))
            .isInstanceOf(RuntimeException.class);

        //then
        assertTrue(memberRepository.find(username).isEmpty()); // memger 저장( 커밋 성공)
        assertTrue(logRepository.find(username).isEmpty()); // 롤백

    }





}
package com.example.springtx.order;

public class NotEnoughMoneyException extends Exception{ //결제 잔고가 부족하면 생기는 비지니스 예외(체크드 예외)
    public NotEnoughMoneyException(String message) {
        super(message);
    }
}

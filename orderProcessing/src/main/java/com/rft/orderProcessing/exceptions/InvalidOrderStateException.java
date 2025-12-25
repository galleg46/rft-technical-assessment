package com.rft.orderProcessing.exceptions;

public class InvalidOrderStateException extends IllegalArgumentException {

    public InvalidOrderStateException(String status) {
        super(status.toUpperCase() +" is not a proper order state");
    }
}

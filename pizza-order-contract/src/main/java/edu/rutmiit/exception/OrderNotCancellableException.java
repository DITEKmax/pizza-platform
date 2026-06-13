package edu.rutmiit.exception;

public class OrderNotCancellableException extends RuntimeException {
    public OrderNotCancellableException(Long orderId, String currentStatus) {
        super(String.format("Order id=%d cannot be cancelled in status %s", orderId, currentStatus));
    }
}

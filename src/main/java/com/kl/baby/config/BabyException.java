package com.kl.baby.config;

/**
 * @author 59780
 */
public class BabyException extends RuntimeException {


    private int status = -1;

    private BabyException(String message) {
        super(message);
    }

    private BabyException(int status, String message) {
        super(message);
        this.status = status;
    }

    public static BabyException create(String message) {
        return new BabyException(message);
    }

    public static BabyException create(int status, String message) {
        return new BabyException(status, message);
    }

    public int getStatus() {
        return status;
    }
}

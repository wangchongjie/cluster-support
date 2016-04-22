package com.baidu.unbiz.multiengine.cluster.zookeeper.client;

public class ZKClientException extends RuntimeException {

    public ZKClientException() {
    }

    public ZKClientException(String message) {
        super(message);
    }

    public ZKClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZKClientException(Throwable cause) {
        super(cause);
    }

//    public ZKClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
//        super(message, cause, enableSuppression, writableStackTrace);
//    }
}

package com.sample.utils;

public interface Constant {

    String apiKey = "apiKey=sample";

    interface TOPIC {
        String WELCOME_TOPIC = "WELCOME-TOPIC";
    }

    interface INDEX {
        String FIRST_NAME = "firstName-index";
        String PHONE = "phone-index";
        String EMAIL = "email-index";
    }

    interface REGEX {
        String DD_M_YYYY_HH_MM_SS = "dd-M-yyyy hh:mm:ss";
        String YYYY_MM_DD_HH_MM_SS = "yyyyMMddHHmmssSSS";
        String PHONE = "^(\\+\\d{1,2}\\s?)?1?\\-?\\.?\\s?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";
    }
}

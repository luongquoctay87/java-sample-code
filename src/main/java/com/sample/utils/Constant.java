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

        // Ten-Digit Number: 2055550125
        String TEN_DIGIT_NUMBER = "^\\d{10}$";

        // To allow optional whitespace, dots, or hyphens (-) between the numbers:
        // This pattern will allow numbers like 2055550125, 202 555 0125, 202.555.0125, and 202-555-0125.
        String NUMBER_WITH_WHITESPACES = "^(\\d{3}[- .]?){2}\\d{4}$";

        // Number With Parentheses
        // To allow the optional parenthesis in the number, This expression will allow numbers like (202)5550125, (202) 555-0125 or (202)-555-0125.
        // Additionally, this expression will also allow the phone numbers covered in the above example.
        String NUMBER_WITH_PARENTHESES = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$";

        // To permit the prefix in our number, we have added to the beginning of our pattern the characters: (\\+\\d{1,3}( )?)?
        // This expression will enable phone numbers to include international prefixes, taking into account that international prefixes are normally numbers with a maximum of three digits.
        String NUMBER_WITH_INTERNATIONAL_PREFIX = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$";

        // The expression used in the last section: ^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$
        // Regular expression to allow numbers like +111 123 456 789: ^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$
        // Pattern to allow numbers like +111 123 45 67 89: ^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$
        // For example: 2055550125, 202 555 0125, (202) 555-0125, 111 (202) 555-0125, 636 856 789, +111 636 856 789, 636 85 67 89, +111 636 85 67 89
        String NUMBER_MULTIPLE_REGEX = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
    }
}

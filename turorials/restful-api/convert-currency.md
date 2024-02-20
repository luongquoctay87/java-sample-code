NumberFormat usFormat = NumberFormat.getCurrencyInstance(Locale.US);
NumberFormat inFormat = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
NumberFormat cnFormat = NumberFormat.getCurrencyInstance(Locale.CHINA);
NumberFormat frFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);

String us = usFormat.format(payment);
String india = inFormat.format(payment);
String china = cnFormat.format(payment);
String france = frFormat.format(payment);
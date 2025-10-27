package com.aapeli.colorgui;

import java.util.Comparator;

public enum SortOrder {
    ORDER_ABC(String::compareTo),
    ORDER_CBA(SortOrder.ORDER_ABC.comparator.reversed()),
    ORDER_123_FIRST(Comparator.comparingDouble(SortOrder::parseFirstNumber)),
    ORDER_321_FIRST(SortOrder.ORDER_123_FIRST.comparator.reversed()),
    ORDER_123_ALL(Comparator.comparingDouble(SortOrder::parseAllNumbers)),
    ORDER_321_ALL(SortOrder.ORDER_123_ALL.comparator.reversed());

    private final Comparator<String> comparator;

    SortOrder(Comparator<String> comparator) {
        this.comparator = comparator;
    }

    public Comparator<String> getComparator() {
        return comparator;
    }

    private static double parseFirstNumber(String text) {
        int textLength = text.length();
        if (textLength == 0) {
            return Double.MAX_VALUE;
        } else {
            StringBuilder sb = new StringBuilder(textLength);
            boolean foundNumbersBeforeDecimalPart = false;

            for (int i = 0; i < textLength; ++i) {
                char c = text.charAt(i);
                if (c == '-' && sb.isEmpty()) {
                    sb.append(c);
                } else if (c == '.' && foundNumbersBeforeDecimalPart) {
                    sb.append('.');
                } else if (c >= 48 && c <= 57) { // ASCII [0-9]
                    sb.append(c);
                    if (!foundNumbersBeforeDecimalPart) {
                        foundNumbersBeforeDecimalPart = true;
                    }
                } else {
                    break;
                }
            }

            text = sb.toString();
            if (text.isEmpty()) {
                return -1.8D;
            } else if (text.equals("-")) {
                return -1.8D;
            } else {
                return Double.parseDouble(text);
            }
        }
    }

    private static double parseAllNumbers(String text) {
        int textLength = text.length();
        if (textLength == 0) {
            return Double.MAX_VALUE;
        } else {
            StringBuilder sb = new StringBuilder(textLength);
            boolean foundNumbersBeforeDecimalPart = false;

            for (int i = 0; i < textLength; ++i) {
                char c = text.charAt(i);
                if (c == '-' && sb.isEmpty()) {
                    sb.append(c);
                } else if (c == '.' && foundNumbersBeforeDecimalPart) {
                    sb.append('.');
                } else if (c >= 48 && c <= 57) { // ASCII [0-9]
                    sb.append(c);
                    if (!foundNumbersBeforeDecimalPart) {
                        foundNumbersBeforeDecimalPart = true;
                    }
                }
            }

            text = sb.toString();
            if (text.isEmpty()) {
                return -1.8D;
            } else if (text.equals("-")) {
                return -1.7976931348623157E308D;
            } else {
                return Double.parseDouble(text);
            }
        }
    }
}

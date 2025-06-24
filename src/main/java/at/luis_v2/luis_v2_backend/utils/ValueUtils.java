package at.luis_v2.luis_v2_backend.utils;

public class ValueUtils {
    public static double parseWithMaxDecimals(String input, int maxDecimals) {
        int dotIndex = input.indexOf(".");
        if (dotIndex >= 0 && input.length() > dotIndex + maxDecimals + 1) {
            input = input.substring(0, dotIndex + maxDecimals + 1);
        }
        return Double.parseDouble(input);
    }
}

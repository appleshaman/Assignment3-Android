package com.example.assignment3.Utils;

public class GetHexUtils {


    // a function used to make hex decimal
    private final static char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder stringBuffer = new StringBuilder(bytes.length * 2);

        for (byte aByte : bytes) {
            int high = (aByte & 0xf0) >> 4;
            int low = aByte & 0x0f;
            stringBuffer.append(HEX_CHAR[high]).append(HEX_CHAR[low]);
        }
        return stringBuffer.toString();
    }

    public static byte[] decode(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        String highString = null;
        String lowString = null;
        int high = 0;
        int low = 0;
        for (int i = 0; i < len; i++) {
            highString = hex.substring(i * 2, i * 2 + 1);
            high = Integer.parseInt(highString, 16);
            lowString = hex.substring(i * 2 + 1, i * 2 + 2);
            low = Integer.parseInt(lowString, 16);
            result[i] = (byte) ((high << 4) + low);
        }
        return result;

    }

}

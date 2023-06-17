package com.jantarox.woodanalyzer.stream;

import java.util.Scanner;

public class CoderRLE {
    public byte[] decodeByteArray(String encoded, int width, int height) {
        byte[] array = new byte[width * height];
        Scanner scanner = new Scanner(encoded).useDelimiter("[^\\d]+");
        int i = 0;
        while (scanner.hasNextInt()) {
            int count = Integer.parseInt(scanner.next());
            byte label = Byte.parseByte(scanner.next());
            if (label != 0) {
                for (int j = 0; j < count; j++) {
                    array[i] = label;
                    i++;
                }
            } else {
                i += count;
            }
        }
        return array;
    }

    public String encodeByteArray(byte[] array, int length) {
        byte lastLabel = 0;
        int count = 0;
        String encoded = "";
        for (int i = 0; i < length; i++) {
            byte label = array[i];
            if (label == lastLabel) {
                count++;
            } else {
//                encoded = encoded.concat(String.valueOf(count) + this.labelToChar(lastLabel));
                encoded = encoded.concat(String.format("%d,%d|", count, lastLabel));
                lastLabel = label;
                count = 1;
            }
        }
        encoded = encoded.concat(String.format("%d,%d|", count, lastLabel));
        if (encoded.startsWith("0,0|")) {
            encoded = encoded.substring(4);
        }
        return encoded;
    }

    public short[] decodeShortArray(String encoded, int width, int height) {
        short[] array = new short[width * height];
        Scanner scanner = new Scanner(encoded).useDelimiter("[^\\d]+");
        int i = 0;
        while (scanner.hasNextInt()) {
            int count = Integer.parseInt(scanner.next());
            short label = Short.parseShort(scanner.next());
            if (label != 0) {
                for (int j = 0; j < count; j++) {
                    array[i] = label;
                    i++;
                }
            } else {
                i += count;
            }
        }
        return array;
    }

    public String encodeShortArray(short[] array, int length) {
        short lastLabel = 0;
        int count = 0;
        String encoded = "";
        for (int i = 0; i < length; i++) {
            short label = array[i];
            if (label == lastLabel) {
                count++;
            } else {
//                encoded = encoded.concat(String.valueOf(count) + this.labelToChar(lastLabel));
                encoded = encoded.concat(String.format("%d,%d|", count, lastLabel));
                lastLabel = label;
                count = 1;
            }
        }
        encoded = encoded.concat(String.format("%d,%d|", count, lastLabel));
        if (encoded.startsWith("0,0|")) {
            encoded = encoded.substring(4);
        }
        return encoded;
    }
}

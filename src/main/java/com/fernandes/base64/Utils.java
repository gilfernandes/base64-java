package com.fernandes.base64;

import java.util.Arrays;

public class Utils {

  public static void printBytes(int[] quartett) {
    System.out.println();
    for(int b : quartett) {
      System.out.printf("%s ", b);
      showBits(b);
    }
    System.out.println(new String(asCharArray(quartett)));
  }

  public static void showBits(int i) {
    System.out.println(String.format("%8s", Integer.toBinaryString(i))
        .replaceAll(" ", "0"));
  }

  public static int indexOf(int[] bytes, int value) {
    for (int i = 0; i < bytes.length; i++) {
      if (bytes[i] == value) {
        return i;
      }
    }
    return bytes.length;
  }

  public static int[] convertSignedToUnsigned(byte[] bytes) {
    int[] res = new int[bytes.length];
    Arrays.setAll(res, i -> Byte.toUnsignedInt(bytes[i]));
    return res;
  }

  public static byte[] convertUnsignedToSigned(int[] ints) {
    byte[] res = new byte[ints.length];
    for(int i = 0; i < ints.length; i++) {
      res[i] = (byte) (ints[i] & 0xFF);
    }
    return res;
  }

  public static int[] asIntArray(char[] charArray) {
    int[] result = new int[charArray.length];
    Arrays.setAll(result, i -> (int) charArray[i]);
    return result;
  }

  public static char[] asCharArray(int[] intArray) {
    char[] result = new char[intArray.length];
    for(int i = 0; i < intArray.length; i++) {
      result[i] = (char) intArray[i];
    }
    return result;
  }

}

package com.fernandes.base64;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Sketch {

  public static void main(String[] args) {
    String test = "Isto é bom!";
    byte[] bytes = test.getBytes(StandardCharsets.UTF_8);
    int[] ints = new int[bytes.length];
    for(int i = 0; i < bytes.length; i++) {
      ints[i] = bytes[i] & 0xff;
    }
    System.out.println(Arrays.toString(bytes));
    System.out.println(Arrays.toString(ints));
  }
}

package com.fernandes.base64;

import static com.fernandes.base64.Utils.convertSignedToUnsigned;
import static com.fernandes.base64.Utils.convertUnsignedToSigned;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Contains byte encoding and decoding methods, but also methods for decoding both strings and files.
 */
public class Base64 {

  public static byte[] base64Encode(byte[] bytes) {
    int[] raw = convertSignedToUnsigned(bytes);
    int[] encoded = base64Encode(raw);
    return convertUnsignedToSigned(encoded);
  }

  public static int[] base64Encode(int[] bytes) {
    int[] res = new int[encodeCalcByteSize(bytes)];
    int length = bytes.length;
    for (int i = 1, position = 0; i < length; i++) {
      if (i % 3 == 2) {
        int[] trio = Arrays.copyOfRange(bytes, i - 2, i + 1);
        int[] quartett = encodeTrio(trio);
        System.arraycopy(quartett, 0, res, position, 4);
        position += 4;
      }
    }
    int remaining = length % 3;
    if (remaining > 0) {
      int[] remainingBytes = Arrays.copyOfRange(bytes, length - remaining, length);
      int[] quartet = remaining == 2 ? encodeDuo(remainingBytes) : encodeUno(remainingBytes);
      System.arraycopy(quartet, 0, res, res.length - quartet.length, quartet.length);
    }
    return res;
  }

  public static String base64Encode(String str) {
    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
    int[] ints = convertSignedToUnsigned(bytes);
    int[] encoded = base64Encode(ints);
    return new String(convertUnsignedToSigned(encoded), StandardCharsets.UTF_8);
  }

  public static byte[] base64Decode(byte[] bytes) {
    int[] raw = convertSignedToUnsigned(bytes);
    int[] decoded = base64Decode(raw);
    return convertUnsignedToSigned(decoded);
  }

  public static int[] base64Decode(int[] bytes) {
    int[] res = new int[decodeCalcByteSize(bytes)];
    int length = bytes.length;
    int chunk = 4;
    int moduloMax = chunk - 1;
    for (int i = 1, position = 0; i < length - chunk; i++) {
      if (i % chunk == moduloMax) {
        int[] quartett = Arrays.copyOfRange(bytes, i - moduloMax, i + 1);
        int[] converted = convertEncodedBytes(quartett);
        int[] decoded = bytesDecodeQuartett(converted);
        System.arraycopy(decoded, 0, res, position, 3);
        position += 3;
      }
    }
    int[] remainingBytes = Arrays.copyOfRange(bytes, length - chunk, length);
    int[] converted = convertEncodedBytes(remainingBytes);
    int[] decoded = decodeIncomplete(converted);
    System.arraycopy(decoded, 0, res, res.length - decoded.length, decoded.length);
    return res;
  }

  public static String base64Decode(String str) {
    byte[] resBytes = decodeToBytes(str);
    return new String(resBytes, StandardCharsets.UTF_8);
  }

  public static String encodeFile(Path path, Charset charset) throws IOException {
    byte[] allBytes = Files.readAllBytes(path);
    int[] ints = convertSignedToUnsigned(allBytes);
    int[] res = base64Encode(ints);
    return new String(convertUnsignedToSigned(res), charset == null ? StandardCharsets.UTF_8 : charset);
  }

  public static void decodeToFile(String content, Path path) throws IOException {
    byte[] bytes = decodeToBytes(content);
    Files.write(path, bytes);
  }

  public static void encodeFileToFile(Path source, Path target) throws IOException {
    byte[] allBytes = Files.readAllBytes(source);
    byte[] encoded = base64Encode(allBytes);
    Files.write(target, encoded);
  }

  public static void decodeFileToFile(Path source, Path target) throws IOException {
    byte[] allBytes = Files.readAllBytes(source);
    byte[] encoded = base64Decode(allBytes);
    Files.write(target, encoded);
  }

  private static byte[] decodeToBytes(String str) {
    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
    int[] ints = convertSignedToUnsigned(bytes);
    int[] res = base64Decode(ints);
    return convertUnsignedToSigned(res);
  }

  static int encodeCalcByteSize(int[] bytes) {
    return (int) Math.ceil((bytes.length * 4. / 3) / 4) * 4;
  }

  static int[] encodeUno(int[] uno) {
    assert uno.length == 1;
    int[] trio = {uno[0], 15, 255};
    int[] quartett = bytesEncodeTrio(trio);
    return new int[]{
        Constants.BASE_64_ENCODiNG_CHARS[quartett[0]],
        Constants.BASE_64_ENCODiNG_CHARS[quartett[1]],
        Constants.PADDING_CHAR,
        Constants.PADDING_CHAR
    };
  }

  static int[] encodeDuo(int[] duo) {
    assert duo.length == 2;
    int[] trio = {duo[0], duo[1], 63};
    int[] quartett = bytesEncodeTrio(trio);
    return new int[]{
        Constants.BASE_64_ENCODiNG_CHARS[quartett[0]],
        Constants.BASE_64_ENCODiNG_CHARS[quartett[1]],
        Constants.BASE_64_ENCODiNG_CHARS[quartett[2]],
        Constants.PADDING_CHAR
    };
  }

  static int[] encodeTrio(int[] bytes) {
    assert bytes.length == 3;
    int[] quartett = bytesEncodeTrio(bytes);
    return new int[]{
        Constants.BASE_64_ENCODiNG_CHARS[quartett[0]],
        Constants.BASE_64_ENCODiNG_CHARS[quartett[1]],
        Constants.BASE_64_ENCODiNG_CHARS[quartett[2]],
        Constants.BASE_64_ENCODiNG_CHARS[quartett[3]],
    };
  }

  static int[] bytesEncodeTrio(int[] bytes) {
    int i = bytes[0];
    int first = i >> 2;
    int temp = (i & 3) << 4;
    int j = bytes[1];
    int second = (j >> 4) | temp;
    temp = (j & 15) << 2;
    int k = bytes[2];
    int third = k >> 6 | temp;
    int fourth = k & 63;
    return new int[]{first, second, third, fourth};
  }

  static int decodeCalcByteSize(int[] bytes) {
    int realLength = Utils.indexOf(bytes, '=');
    return (int) Math.floor(realLength * 3. / 4);
  }

  static int[] convertEncodedBytes(int[] bytes) {
    int[] res = new int[bytes.length];
    for (int i = 0; i < bytes.length; i++) {
      res[i] = Constants.CHARS_BASE_64_ENCODING[bytes[i]];
    }
    return res;
  }

  static int[] decodeIncomplete(int[] bytes) {
    int[] quartet = new int[4];
    int padPos = Utils.indexOf(bytes, Constants.CHARS_BASE_64_ENCODING['=']);
    System.arraycopy(bytes, 0, quartet, 0, padPos);
    int[] temp = bytesDecodeQuartett(quartet);
    switch (padPos) {
      case 4:
        return temp;
      case 3:
        return new int[]{temp[0], temp[1]};
      case 2:
        return new int[]{temp[0]};
      default:
        return new int[0];
    }
  }

  static int[] bytesDecodeQuartett(int[] bytes) {
    int i = bytes[0];
    int j = bytes[1];
    int k = bytes[2];
    int l = bytes[3];
    int first = (i << 2) | (j >> 4);
    int second = ((j << 4) | (k >> 2)) & 0xFF;
    int third = ((k << 6) | (l)) & 0xFF;
    return new int[]{first, second, third};
  }

}

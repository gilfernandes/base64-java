package com.fernandes.base64;

/**
 * Constants used in the encoder and decoders
 */
public class Constants {
  static final String BASE_64_ENCODiNG = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
  static final char[] BASE_64_ENCODiNG_CHARS = BASE_64_ENCODiNG.toCharArray();
  static final char[] CHARS_BASE_64_ENCODING = new char[128];
  static final char PADDING_CHAR = '=';

  static {
    for (int i = 0; i < Constants.BASE_64_ENCODiNG_CHARS.length; i++) {
      char b = Constants.BASE_64_ENCODiNG_CHARS[i];
      Constants.CHARS_BASE_64_ENCODING[b] = (char) i;
    }
    Constants.CHARS_BASE_64_ENCODING['='] = (char) (Constants.BASE_64_ENCODiNG.length() + 1);
  }
}

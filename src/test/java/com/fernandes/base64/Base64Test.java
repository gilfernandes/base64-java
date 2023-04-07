package com.fernandes.base64;

import static com.fernandes.base64.Base64.base64Decode;
import static com.fernandes.base64.Base64.base64Encode;
import static com.fernandes.base64.Base64.bytesDecodeQuartett;
import static com.fernandes.base64.Base64.convertEncodedBytes;
import static com.fernandes.base64.Base64.decodeCalcByteSize;
import static com.fernandes.base64.Base64.decodeFileToFile;
import static com.fernandes.base64.Base64.decodeIncomplete;
import static com.fernandes.base64.Base64.decodeToFile;
import static com.fernandes.base64.Base64.encodeDuo;
import static com.fernandes.base64.Base64.encodeFile;
import static com.fernandes.base64.Base64.encodeFileToFile;
import static com.fernandes.base64.Base64.encodeTrio;
import static com.fernandes.base64.Base64.encodeUno;
import static com.fernandes.base64.Utils.asIntArray;
import static com.fernandes.base64.Utils.convertUnsignedToSigned;
import static com.fernandes.base64.Utils.printBytes;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class Base64Test {


  @Test
  public void whenEncode_ShouldProduceRightResults() {
    int[] ints = asIntArray("Man".toCharArray());
    int[] quartett = encodeTrio(ints);
    printBytes(quartett);
    assertEquals(new String(convertUnsignedToSigned(quartett), StandardCharsets.UTF_8), "TWFu");
    int[] duo = {77,97};
    int[] quartett2 = encodeDuo(duo);
    printBytes(quartett2);
    assertEquals(new String(convertUnsignedToSigned(quartett2), StandardCharsets.UTF_8), "TWE=");
    int[] uno = {77};
    int[] quartett3 = encodeUno(uno);
    printBytes(quartett3);
    assertEquals(new String(convertUnsignedToSigned(quartett3), StandardCharsets.UTF_8), "TQ==");
  }

  @Test
  public void whenBase64_ShouldProduceRightOutput() {
    String encoded = base64Encode("Man");
    assertEquals(encoded.length(), 4);
    assertEquals(encoded, "TWFu");
  }

  @Test
  public void whenBase64_Woman_ShouldProduceRightOutput() {
    String encoded = base64Encode("Woman");
    assertEquals(encoded, "V29tYW4=");
  }

  @Test
  public void whenBase64_King_ShouldProduceRightOutput() {
    String encoded = base64Encode("King");
    assertEquals(encoded, "S2luZw==");
  }

  @Test
  public void whenBase64_Base64_ShouldProduceRightOutput() {
    String encoded = base64Encode("Base64");
    assertEquals(encoded, "QmFzZTY0");
  }

  @Test
  public void whenBase64_long_sentence_ShouldProduceRightOutput() {
    String encoded = base64Encode("Base64 can be used to transmit and store text that might otherwise cause delimiter collision");
    assertEquals(encoded, "QmFzZTY0IGNhbiBiZSB1c2VkIHRvIHRyYW5zbWl0IGFuZCBzdG9yZSB0ZXh0IHRoYXQgbWlnaHQgb3RoZXJ3aXNlIGNhdXNlIGRlbGltaXRlciBjb2xsaXNpb24=");
  }

  @Test
  public void whenDecode_ShouldProduceRightResults() {
    int[] input = {19, 22, 5, 46};
    int[] output = bytesDecodeQuartett(input);
    assertEquals(output.length, 3);
    assertEquals(output[0], 77);
    assertEquals(output[1], 97);
    assertEquals(output[2], 110);
  }

  @Test
  public void whenDecodeBytes_ShouldProduceRightResults() {
    int[] input = {84, 87, 70, 117}; // TWFu
    int[] output = convertEncodedBytes(input);
    assertEquals(output.length, input.length);
    int[] expected = {19, 22, 5, 46};
    assertArrayEquals(expected, output);
  }

  @Test
  public void whenDecodeBytesWithOnePadding_ShouldProduceRightResults() {
    int[] input = {84, 87, 70, '='}; // TWF=
    int[] output = convertEncodedBytes(input);
    assertEquals(output.length, input.length);
    int[] expected = {19, 22, 5, 65};
    assertArrayEquals(expected, output);
  }

  @Test
  public void whenDecodeTrio_ShouldDecodeTrio() {
    int[] rawInput = {'T', 'W', 'E', '='}; // TWE=
    int[] input = convertEncodedBytes(rawInput);
    int[] output = decodeIncomplete(input);
    assertEquals(output.length, 2);
    int[] expected = {77, 97};
    assertArrayEquals(expected, output);
  }

  @Test
  public void whenDecodeDuo_ShouldDecodeTrio() {
    int[] rawInput = {'T', 'Q', '=', '='}; // TWE=
    int[] input = convertEncodedBytes(rawInput);
    int[] output = decodeIncomplete(input);
    assertEquals(output.length, 1);
    int[] expected = {77};
    assertArrayEquals(expected, output);
  }

  @Test
  public void whenDecodeCalcByteSize_ShouldGiveRightResults() {
    assertEquals(1, decodeCalcByteSize(new int[]{'T', 'Q', '=', '='}));
    assertEquals(2, decodeCalcByteSize(new int[]{'T', 'W', 'E', '='}));
    assertEquals(3, decodeCalcByteSize(new int[]{'T', 'W', 'F', 'u'}));
    assertEquals(4, decodeCalcByteSize(new int[]{'Z', 'm', '9', '1', 'c', 'g', '=', '='})); // four
    assertEquals(5, decodeCalcByteSize(new int[]{'d', 'G', 'h', 'y', 'Z', 'W', 'U', '='})); // three
    assertEquals(6, decodeCalcByteSize(new int[]{'d', 'G', 'h', 'y', 'Z', 'W', 'V', 'z'})); // threes
  }

  @Test
  public void whenBase64Decode_ShouldGiveRightResults() {
    int[] input = asIntArray("TWFu".toCharArray());
    int[] results = base64Decode(input);
    assertArrayEquals(new int[]{77, 97, 110}, results);
  }

  @Test
  public void whenBase64DecodeIncomplete_ShouldGiveRightResults() {
    int[] input = asIntArray("TWE=".toCharArray());
    int[] results = base64Decode(input);
    assertArrayEquals(new int[]{77, 97}, results);
  }

  @Test
  public void whenBase64DecodeString_ShouldGiveRightResults() {
    assertEquals("Man", base64Decode("TWFu"));
    assertEquals("Ma", base64Decode("TWE="));
    assertEquals("M", base64Decode("TQ=="));
    assertEquals("four", base64Decode("Zm91cg=="));
    assertEquals("three", base64Decode("dGhyZWU="));
    assertEquals("threes", base64Decode("dGhyZWVz"));
  }

  @Test
  public void whenEncodeDecode_ShouldRetrieveSameResult() {
    String[] tests = {
        "4 × 6",
        "The first known standardized use of the encoding now called MIME Base64 was in the Privacy-enhanced Electronic Mail",
        "Because Base64 is a six-bit encoding, and because the decoded values are divided into 8-bit octets, ",
        "Olá, isto é um teste",
        "Heute ist ein sehr schöner Tag",
        "Ma nagyon szép nap van",
        "In the above quote, the encoded value of Man is TWFu. Encoded in ASCII, the characters M, a, and n are stored as the byte values 77, 97, and 110, which are the 8-bit binary values 01001101, 01100001, and 01101110. These three values are joined together into a 24-bit string, producing 010011010110000101101110. Groups of 6 bits (6 bits have a maximum of 26 = 64 different binary values) are converted into individual numbers from start to end (in this case, there are four numbers in a 24-bit string), which are then converted into their corresponding Base64 character values.\n" +
            "\n" +
            "As this example illustrates, Base64 encoding converts three octets into four encoded characters."
    };
    for(String orig : tests) {
      String str = base64Encode(orig);
      String decoded = base64Decode(str);
      assertEquals(orig, decoded);
      System.out.printf("%s %s%n", str, decoded);
    }
  }

  @Test
  public void whenEncodeFile_ShouldDecodeToSame() throws URISyntaxException, IOException {
    Path source = FileProvider.provideTextFile();
    String content = encodeFile(source, StandardCharsets.UTF_8);
    assertNotNull(content);
    System.out.println(content);
    Path target = Paths.get("temp_hindi.txt");
    decodeToFile(content, target);
    compareFiles(source, target);
  }

  @Test
  public void whenEncodeDecodeFileText_ShouldGetSameFile() throws URISyntaxException, IOException {
    Path source = FileProvider.provideTextFile();
    Path target = Paths.get("encoded_hindi.txt");
    encodeFileToFile(source, target);
    Path decodedTarget = Paths.get("decoded_temp_hindi.txt");
    decodeFileToFile(target, decodedTarget);
    compareFiles(source, decodedTarget);
  }

  @Test
  public void whenEncodeDecodeFileImage_ShouldGetSameFile() throws URISyntaxException, IOException {
    Path source = FileProvider.provideImageFile();
    Path target = Paths.get("encoded_image.txt");
    encodeFileToFile(source, target);
    Path decodedTarget = Paths.get("random_image.png");
    decodeFileToFile(target, decodedTarget);
    compareFiles(source, decodedTarget);
  }

  private static void compareFiles(Path source, Path target) throws IOException {
    byte[] sourceBytes = Files.readAllBytes(source);
    byte[] targetBytes = Files.readAllBytes(target);
    assertArrayEquals(sourceBytes, targetBytes);
  }
}
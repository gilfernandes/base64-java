package com.fernandes.base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides sample files for testing.
 */
public class FileProvider {

  public static Path provideTextFile() throws URISyntaxException {
    return getPath("hindi_text.txt");
  }

  public static Path provideImageFile() throws URISyntaxException {
    return getPath("random_image.png");
  }

  private static Path getPath(String name) throws URISyntaxException {
    URL resource = Thread.currentThread().getContextClassLoader().getResource(name);
    assertNotNull(resource);
    Path source = Paths.get(resource.toURI());
    assertTrue(Files.exists(source));
    return source;
  }

}

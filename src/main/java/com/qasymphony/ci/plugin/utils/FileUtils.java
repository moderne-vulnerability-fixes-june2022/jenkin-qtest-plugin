package com.qasymphony.ci.plugin.utils;

import java.io.*;

/***
 *
 *  Created by: phuta
 *  Created date: 11/10/21 - 11:54 AM
 *
 */

public class FileUtils {
  public static void saveConfig(String config, File configFile) throws IOException {
    FileWriter fileWriter = new FileWriter(configFile);
    fileWriter.write(config);
    fileWriter.close();
  }

  public static String loadConfig(File configFile) throws IOException {
    StringBuilder content = new StringBuilder();
    String line;
    try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
      while ((line = reader.readLine()) != null) {
        content.append(line);
        content.append(System.lineSeparator());
      }
      return content.toString();
    }
  }
}

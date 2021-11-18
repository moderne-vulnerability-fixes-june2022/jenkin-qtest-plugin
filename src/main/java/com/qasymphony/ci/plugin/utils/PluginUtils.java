package com.qasymphony.ci.plugin.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.*;

/***
 *
 *  Created by: phuta
 *  Created date: 11/10/21 - 11:54 AM
 *
 */

public class PluginUtils {
  public static String encode(String data) {
    Base64 base64 = new Base64();
    return base64.encodeToString(data.getBytes());
  }

  public static String decode(String data) {
    Base64 base64 = new Base64();
    return new String(base64.decode(data.getBytes()));
  }
}

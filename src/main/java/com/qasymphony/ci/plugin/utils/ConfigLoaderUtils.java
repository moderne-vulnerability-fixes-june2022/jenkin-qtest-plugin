package com.qasymphony.ci.plugin.utils;

import com.qasymphony.ci.plugin.OauthProvider;
import com.qasymphony.ci.plugin.exception.StoreResultException;
import com.qasymphony.ci.plugin.model.qtest.TokenExpiration;
import com.qasymphony.ci.plugin.store.StoreResultServiceImpl;
import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import com.qasymphony.ci.plugin.utils.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.remoting.RoleChecker;

import java.io.*;

/***
 *
 *  Created by: phuta
 *  Created date: 11/11/21 - 9:15 AM
 *
 */

public class ConfigLoaderUtils {

  private static final String TOKEN_CONFIG_FILE = ".config";

  public static boolean saveConfig(FilePath filePath, String content) {
    FilePath configFilePath = new FilePath(filePath, TOKEN_CONFIG_FILE);
    String encodedContent = PluginUtils.encode(content);
    try {
      configFilePath.act(new FilePath.FileCallable<String>() {
        @Override public String invoke(File file, VirtualChannel virtualChannel)
            throws IOException, InterruptedException {
          BufferedWriter writer = null;
          try {
            writer = new BufferedWriter(new FileWriter(file.getPath(), false));
            writer.write(encodedContent);
            writer.newLine();
            return null;
          } finally {
            if (null != writer)
              writer.close();
          }
        }

        @Override public void checkRoles(RoleChecker roleChecker) throws SecurityException {
        }
      });
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static boolean updateAccessTokenExpirationConfig(String qTestUrl, String apiKey, FilePath filePath) {
    FilePath configFilePath = new FilePath(filePath, TOKEN_CONFIG_FILE);
    try {
      String jsonString = configFilePath.act(new FilePath.FileCallable<String>() {
        @Override
        public String invoke(File file, VirtualChannel virtualChannel) throws IOException, InterruptedException {
          BufferedReader reader = null;
          StringBuilder content = new StringBuilder();
          try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
              content.append(line);
              content.append(System.lineSeparator());
            }
          } finally {
            if (null != reader)
              reader.close();
          }
          String encoded = content.toString();
          return StringUtils.isEmpty(encoded) ? null : PluginUtils.decode(encoded);
        }

        @Override
        public void checkRoles(RoleChecker roleChecker) throws SecurityException {
        }
      });
      TokenExpiration tokenExpiration = StringUtils.isEmpty(jsonString) ? null : JsonUtils.fromJson(jsonString, TokenExpiration.class);
      if (null != tokenExpiration) {
        OauthProvider.putOrUpdateTokenExpiration(qTestUrl, apiKey, tokenExpiration.getToken(), tokenExpiration.getValidUntilUTC());
        return true;
      }
    } catch (Exception ex) {
      return false;
    }
    return false;
  }
}

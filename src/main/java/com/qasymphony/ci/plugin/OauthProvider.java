package com.qasymphony.ci.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.qasymphony.ci.plugin.exception.OAuthException;
import com.qasymphony.ci.plugin.model.qtest.TokenExpiration;
import com.qasymphony.ci.plugin.model.qtest.TokenStatus;
import com.qasymphony.ci.plugin.utils.HttpClientUtils;
import com.qasymphony.ci.plugin.utils.JsonUtils;
import com.qasymphony.ci.plugin.utils.ResponseEntity;
import org.apache.commons.httpclient.HttpStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author trongle
 * @version 10/23/2015 5:22 PM trongle $
 * @since 1.0
 */
public class OauthProvider {
  private static final Logger LOG = Logger.getLogger(OauthProvider.class.getName());
  private static final String HEADER_KEY = "Basic amVua2luczpkZEtzVjA4NmNRbW8wWjZNUzBCaU4wekpidVdLbk5oNA==";

  private static final Map<String, Map<String, TokenExpiration>> QTEST_TOKEN_EXPIRATION_HOLDER = new HashMap<>();
  private OauthProvider() {

  }

  public static void putOrUpdateTokenExpiration(String qTestUrl, String apiKey, String accessToken, long accessTokenExpiration) {
    TokenExpiration tokenExpiration = getTokenExpiration(qTestUrl, apiKey);
    if (null == tokenExpiration) {
      tokenExpiration = new TokenExpiration();
      Map<String, TokenExpiration> token = new HashMap<>();
      token.put(apiKey, tokenExpiration);
      QTEST_TOKEN_EXPIRATION_HOLDER.put(qTestUrl, token);
    }
    tokenExpiration.setToken(accessToken);
    tokenExpiration.setValidUntilUTC(accessTokenExpiration);

  }

  public static TokenExpiration getTokenExpiration(String qTestUrl, String apiKey) {
    Map<String, TokenExpiration> insts = QTEST_TOKEN_EXPIRATION_HOLDER.get(qTestUrl);
    if (null != insts) {
      return insts.get(apiKey);
    }
    return null;
  }
  /**
   * Get access token from apiKey
   *
   * @param url    url
   * @param apiKey apiKey
   * @return access token
   * @throws OAuthException OAuthException
   */
  public static String getAccessToken(String url, String apiKey) throws OAuthException {
    TokenExpiration tokenExpiration = getTokenExpiration(url, apiKey);
    if (null != tokenExpiration) {
      Instant now = Instant.now();
      long nowInMillisecond = now.toEpochMilli();
      if (nowInMillisecond < tokenExpiration.getValidUntilUTC()) {
        return tokenExpiration.getToken();
      }
    }
    String accessToken = getAccessToken(url, apiKey, HEADER_KEY);
    TokenStatus accessTokenStatus = getAccessTokenStatus(url, accessToken);
    putOrUpdateTokenExpiration(url, apiKey, accessToken, accessTokenStatus.getExpiration());
    return accessToken;
  }

  private static String getAccessToken(String url, String apiKey, String secretKey) throws OAuthException {
    StringBuilder sb = new StringBuilder()
      .append(url)
      .append("/oauth/token?grant_type=refresh_token")
      .append("&refresh_token=").append(HttpClientUtils.encode(apiKey));
    Map<String, String> headers = new HashMap<>();
    headers.put(Constants.HEADER_AUTH, secretKey);
    try {
      ResponseEntity entity = HttpClientUtils.post(sb.toString(), headers, null);
      if (HttpStatus.SC_OK != entity.getStatusCode()) {
        throw new OAuthException(entity.getBody(), entity.getStatusCode());
      }
      JsonNode node = JsonUtils.readTree(entity.getBody());
      if (null == node) {
        throw new OAuthException("Cannot get access token from: " + entity.getBody(), entity.getStatusCode());
      }
      return JsonUtils.getText(node, "access_token");
    } catch (Exception e) {
      throw new OAuthException(e.getMessage(), e);
    }
  }

  private static TokenStatus getAccessTokenStatus(String url, String accessToken) throws OAuthException {
    StringBuilder sb = new StringBuilder()
        .append(url)
        .append("/oauth/status");
    Map<String, String> headers = buildHeaders(accessToken, null);
    try {
      ResponseEntity entity = HttpClientUtils.get(sb.toString(), headers);
      if (HttpStatus.SC_OK != entity.getStatusCode()) {
        throw new OAuthException(entity.getBody(), entity.getStatusCode());
      }
      return JsonUtils.fromJson(entity.getBody(), TokenStatus.class);
    } catch (Exception e) {
      throw new OAuthException(e.getMessage(), e);
    }
  }
  /**
   * Build header with get access token from refresh token
   *
   * @param url     url
   * @param apiKey  apiKey
   * @param headers headers
   * @return headers
   */
  public static Map<String, String> buildHeaders(String url, String apiKey, Map<String, String> headers) {
    String accessToken = null;
    try {
      accessToken = getAccessToken(url, apiKey);
    } catch (Exception e) {
      LOG.log(Level.WARNING, "Error while build header:" + e.getMessage());
    }
    return buildHeaders(accessToken, headers);
  }

  /**
   * Build headers with access token
   *
   * @param accessToken accessToken
   * @param headers     headers
   * @return headers
   */
  public static Map<String, String> buildHeaders(String accessToken, Map<String, String> headers) {
    Map<String, String> map = new HashMap<>();
    //appSecretKey is refresh token, so we packed with Bearer in header when build headers to qTest
    map.put(Constants.HEADER_AUTH, "Bearer " + accessToken);
    map.put(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
    if (null != headers && headers.size() > 0) {
      map.putAll(headers);
    }
    return map;
  }
}

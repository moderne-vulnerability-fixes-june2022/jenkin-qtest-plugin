package com.qasymphony.ci.plugin.model.qtest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/***
 *
 *  Created by: phuta
 *  Created date: 11/9/21 - 11:25 AM
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenExpiration {
  private String token;
  private long validUntilUTC;

  public TokenExpiration(String token, long validUntilUTC) {
    this.token = token;
    this.validUntilUTC = validUntilUTC;
  }

  public TokenExpiration() {
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public long getValidUntilUTC() {
    return validUntilUTC;
  }

  public void setValidUntilUTC(long validUntilUTC) {
    this.validUntilUTC = validUntilUTC;
  }
}

package com.qasymphony.ci.plugin.model.qtest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/***
 *
 *  Created by: phuta
 *  Created date: 11/9/21 - 9:14 AM
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenStatus {
  Long expiration;
  Long validityInMilliseconds;

  public TokenStatus() {
  }

  public Long getExpiration() {
    return expiration;
  }

  public void setExpiration(Long expiration) {
    this.expiration = expiration;
  }

  public Long getValidityInMilliseconds() {
    return validityInMilliseconds;
  }

  public void setValidityInMilliseconds(Long validityInMilliseconds) {
    this.validityInMilliseconds = validityInMilliseconds;
  }
}

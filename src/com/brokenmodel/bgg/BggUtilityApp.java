/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

public class BggUtilityApp {
  public static void main(String[] args) {
    
    try {
      MysqlBggRepository rep = new MysqlBggRepository();
      for (BggUser user : rep.getCorrelatedUsers()) {
        rep.setUserAsCorrelated(user, true);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }         
  }
}

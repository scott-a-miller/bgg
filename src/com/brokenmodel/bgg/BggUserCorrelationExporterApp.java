/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.io.*;

public class BggUserCorrelationExporterApp {
  public static void main(String[] args) {
    
    try {
      MysqlBggRepository rep = new MysqlBggRepository();
      try {      
        BufferedWriter bw = new BufferedWriter(new FileWriter("correlated_users.txt"));
        try {
          BggUserCorrelationMap map = rep.getAllUserCorrelations(true);
          for (BggUser user : map.getUsers()) {
            for (BggCorrelatedUser correlatedUser : map.getCorrelatedUsers(user)) {
              bw.write(user.getName() + "\t" + correlatedUser.getUser().getName() + "\t" + correlatedUser.getCorrelation() + "\t" + correlatedUser.getCommonRatings());
              bw.newLine();
            }
          }
        }
        finally {
          if (bw != null ){
            try { bw.close(); } catch (Exception e) { }
          }
        }
      }
      finally {
        try {rep.close();} catch (Exception e) { }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }         
  }
}

/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.io.*;

public class BggGameCorrelationExporterApp {
  public static void main(String[] args) {
    
    try {
      MysqlBggRepository rep = new MysqlBggRepository();
      try {      
        BufferedWriter bw = new BufferedWriter(new FileWriter("correlated_games.txt"));
        try {
          BggGameCorrelationMap map = rep.getAllCorrelations();
          for (BggGame game : map.getGames()) {
            for (BggCorrelatedGame correlatedGame : map.getCorrelatedGames(game)) {
              bw.write(game.getName() + "\t" + correlatedGame.getGame().getName() + "\t" + correlatedGame.getCorrelation() + "\t" + correlatedGame.getCommonRatings());
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

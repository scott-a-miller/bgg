/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.io.*;
import java.util.List;

public class BggGameRecommendationExporter {
  public static void main(String[] args) {
    
    try {
      MysqlBggRepository rep = new MysqlBggRepository();
      try {      
        BufferedWriter bw = new BufferedWriter(new FileWriter("recommendations.txt"));
        try {
          BggUser user = rep.getUser("CitizenBob");
          List<BggGameRecommendation> recommendations = rep.getGameRecommendations(user);
          List<BggAvgRecommendation> avgRecs = BggAvgRecommendation.createAvgRecommendations(user, recommendations, rep, true);
          BggAvgRecommendation.sortByWeight(avgRecs);
          for (BggAvgRecommendation avgRec : avgRecs) {
            bw.write(avgRec.getGame().getName());
            bw.write("\t");
            bw.write(String.valueOf(avgRec.getAvgRating()));
            bw.write("\t");
            bw.write(String.valueOf(avgRec.getWeightedRating()));
            bw.write("\t");
            bw.write(String.valueOf(avgRec.getNumRatings()));
            bw.write("\t");
            for (BggGameRecommendation rec : avgRec.getRecommendations()) {
              bw.write(rec.getRecommendingUser().getUser().getName());
              bw.write(" : ");
              bw.write(String.valueOf(rec.getRating()));
              bw.write(" : ");
              bw.write(String.valueOf(rec.getRecommendingUser().getCorrelation()));
              bw.write(" : ");
              bw.write(String.valueOf(rec.getRecommendingUser().getCommonRatings()));
              bw.write("\t");
            }
            bw.newLine();
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

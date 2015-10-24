/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.util.*;

public class BggGameCorrelator {
  public static void main(String[] args) {
    
    try {
      MysqlBggRepository rep = new MysqlBggRepository();      
      try {      
        List<BggGame> games = rep.getMostRatedGames(1000);
        List<BggGameRatingList> lists = new ArrayList<BggGameRatingList>(games.size());
        int gameCount = 1;
        for (BggGame game : games) {
          System.out.println("Getting list for (" + gameCount + " of " + games.size() + "): " + game.getName());
          lists.add(rep.getGameRatingList(game));
          gameCount++;
        }
        for (int i = 0; i < games.size(); i++) {
          for (int j = 0; j < i; j++) {
            BggGame gameB = games.get(i);
            BggGame gameA = games.get(j);
            BggGameRatingList listB = lists.get(i);
            BggGameRatingList listA = lists.get(j);

            BggGameCorrelation newCorrelation = listA.calculateCorrelation(listB);
            System.out.println(i + " " + j + " " + gameB.getName() + " : " + gameA.getName() + " -> " + newCorrelation.getCorrelation() + " (" + newCorrelation.getCommonRatings() + ")"); 
            rep.addCorrelation(newCorrelation);
          }
        }
        for (BggGame game : games) {
          rep.setGameAsCorrelated(game, true);
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

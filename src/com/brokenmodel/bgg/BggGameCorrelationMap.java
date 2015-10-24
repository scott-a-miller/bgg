/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.util.*;

public class BggGameCorrelationMap {
  private Map<Integer, List<BggCorrelatedGame>> map = new HashMap<Integer, List<BggCorrelatedGame>>();
  private List<BggGame> games;
  
  public BggGameCorrelationMap(List<BggGameCorrelation> correlations) {
    Map<Integer, BggGame> gameMap = new HashMap<Integer, BggGame>();
    for (BggGameCorrelation correlation : correlations) {
      addToMap(correlation.getGameA(), correlation.getGameB(), correlation.getCorrelation(), correlation.getCommonRatings());
      addToMap(correlation.getGameB(), correlation.getGameA(), correlation.getCorrelation(), correlation.getCommonRatings());
      gameMap.put(correlation.getGameA().getId(), correlation.getGameA());
      gameMap.put(correlation.getGameB().getId(), correlation.getGameB());
    }
    games = new ArrayList<BggGame>(gameMap.values());
    Collections.sort(games, new Comparator<BggGame>() {
      public int compare(BggGame o1, BggGame o2) {
        return o1.getName().compareTo(o2.getName());
      }      
    });
    for (BggGame game : games) {
      Collections.sort(map.get(game.getId()), new Comparator<BggCorrelatedGame>() {
        public int compare(BggCorrelatedGame o1, BggCorrelatedGame o2) {
          return -1 * Float.compare(o1.getCorrelation(), o2.getCorrelation());
        }        
      });
    }
  }
  
  public List<BggCorrelatedGame> getCorrelatedGames(BggGame game) {
    return map.get(game.getId());
  }
  
  public List<BggGame> getGames() {
    return games;
  }
  
  private void addToMap(BggGame firstGame, BggGame secondGame, float correlation, int commonRatings) {
    List<BggCorrelatedGame> games = map.get(firstGame.getId());
    if (games == null) {
      games = new ArrayList<BggCorrelatedGame>();
      map.put(firstGame.getId(), games);
    }
    games.add(new BggCorrelatedGame(secondGame, correlation, commonRatings));    
  }
}

/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.text.DecimalFormat;
import java.util.*;

public class BggAvgRecommendation {
  private DecimalFormat avgFormat = new DecimalFormat("0.00");
  
  private BggUser user;
  private BggGame game;
  private float avgRating = 0f;
  private int numRatings;
  private float weightedRating = 0f;
  private List<BggGameRecommendation> recommendations;
  
  public BggAvgRecommendation(BggUser user, BggGame game, List<BggGameRecommendation> recommendations, final boolean orderByTanimoto) {
    this.user = user;
    this.game = game;
    this.recommendations = new ArrayList<BggGameRecommendation>(recommendations);
    Collections.sort(this.recommendations, new Comparator<BggGameRecommendation>() {
      public int compare(BggGameRecommendation o1, BggGameRecommendation o2) {
        if (orderByTanimoto) {
          return -1 * Float.compare(o1.getRecommendingUser().getTanimoto(), o2.getRecommendingUser().getTanimoto());
        }
        else {
          return -1 * Float.compare(o1.getRecommendingUser().getCorrelation(), o2.getRecommendingUser().getCorrelation());
        }
      }      
    });
    for (BggGameRecommendation rec : this.recommendations) {
      numRatings++;
      avgRating = (avgRating * (numRatings - 1) + rec.getRating()) / numRatings;
      float similarityScore = orderByTanimoto ? rec.getRecommendingUser().getTanimoto() : rec.getRecommendingUser().getCorrelation();
      weightedRating = (weightedRating * (numRatings - 1) + rec.getRating() * similarityScore) / numRatings;
    }
  }
  
  public BggUser getUser() {
    return user;
  }
  
  public BggGame getGame() {
    return game;
  }

  public float getAvgRating() {
    return avgRating;
  }

  public int getNumRatings() {
    return numRatings;
  }

  public float getWeightedRating() {
    return weightedRating;
  }

  public List<BggGameRecommendation> getRecommendations() {
    return recommendations;
  }

  public int getRecommendationsPlusTwo() {
    return recommendations.size() + 2;
  }
  
  public static void sortByAvg(List<BggAvgRecommendation> avgRecs) {
    Collections.sort(avgRecs, new Comparator<BggAvgRecommendation>() {
      public int compare(BggAvgRecommendation o1, BggAvgRecommendation o2) {
        return -1 * Float.compare(o1.avgRating, o2.avgRating);
      } 
    });
  }

  public static void sortByWeight(List<BggAvgRecommendation> avgRecs) {
    Collections.sort(avgRecs, new Comparator<BggAvgRecommendation>() {
      public int compare(BggAvgRecommendation o1, BggAvgRecommendation o2) {
        if (o1.numRatings < 3) {
          if (o2.numRatings < 3) {
            return -1 * Float.compare(o1.weightedRating, o2.weightedRating);
          }
          else {
            return 1;
          }
        }
        else if (o2.numRatings < 3) {
          return -1;
        }
        else {
          return -1 * Float.compare(o1.weightedRating, o2.weightedRating);
        }        
      } 
    });
  }

  public static List<BggAvgRecommendation> createAvgRecommendations(BggUser user, List<BggGameRecommendation> allRecommendations, MysqlBggRepository rep, boolean orderByTanimoto) throws Exception {
    Map<Integer,List<BggGameRecommendation>> map = new HashMap<Integer,List<BggGameRecommendation>>();
    for (BggGameRecommendation recommendation : allRecommendations) {
      List<BggGameRecommendation> recommendations = map.get(recommendation.getGameId());
      if (recommendations == null) {
        recommendations = new ArrayList<BggGameRecommendation>();
        map.put(recommendation.getGameId(), recommendations);
      }
      recommendations.add(recommendation);
    }
    
    List<BggAvgRecommendation> avgRecommendations = new ArrayList<BggAvgRecommendation>();
    for (Integer gameId : map.keySet()) {
      BggGame game = rep.getGameById(gameId);
      if (game == null) {
        throw new RuntimeException("No game for id: " + gameId);
      }
      avgRecommendations.add(new BggAvgRecommendation(user, game, map.get(gameId), orderByTanimoto));
    }
    return avgRecommendations;
  }
  
  public String getFormattedAvgRating() {
    if (Float.isNaN(avgRating)) {
      return null;
    }
    else {
      return avgFormat.format(avgRating);
    }
  }
  
  public String getFormattedWeightedRating() {
    if (Float.isNaN(weightedRating)) {
      return null;
    }
    else {
      return avgFormat.format(weightedRating);
    }
  }
}

/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg.server;

import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.*;

import com.brokenmodel.bgg.*;
import com.brokenmodel.swats.ControllerRequest;

public class BggRecommendedGamesController extends BggController {
  private static final long serialVersionUID = 1L;
  private static final String SORT_BY_NUM_RATINGS = "numRatings";
  private static final String SORT_BY_AVG_RATING = "avgRating";

  private NumRatingsComparator numRatingsComparator = new NumRatingsComparator();
  private AvgRatingComparator avgRatingComparator = new AvgRatingComparator(); 
  
  private Map<Integer, SoftReference<List<BggAvgRecommendation>>> tanimotoCache = 
    Collections.synchronizedMap(new HashMap<Integer, SoftReference<List<BggAvgRecommendation>>>());
  private Map<Integer, SoftReference<List<BggAvgRecommendation>>> correlationCache = 
    Collections.synchronizedMap(new HashMap<Integer, SoftReference<List<BggAvgRecommendation>>>());
    
  public void doRequest(ControllerRequest request, MysqlBggRepository rep) throws Exception { 
    String userName = URLEncoder.encode(request.getParameter("username"), "UTF-8");
    userName = userName.replace("+", "%20");
    BggUser user = rep.getUser(userName);
    boolean orderByTanimoto = false;
    String similarityString = request.getParameter("similarity");
    if ("tanimoto".equals(similarityString)) {
      orderByTanimoto = true;
    }
    
    HashMap<String, Object> params = new HashMap<String, Object>();
    if (user == null) {
      params.put("username", userName);
      renderView(request, "no_such_user", params);
    }
    else {
      List<BggAvgRecommendation> recommendations = null;
      SoftReference<List<BggAvgRecommendation>> cachedRef;
      if (orderByTanimoto) {
        cachedRef = tanimotoCache.get(user.getId());
      }
      else {
        cachedRef = correlationCache.get(user.getId());
      }
      if (cachedRef != null) {
        recommendations = cachedRef.get();        
      }
      if (recommendations == null) {
        BggGameRecommender recommender = new BggGameRecommender(rep);
        recommendations = recommender.getAvgRecommendations(user, orderByTanimoto);
        if (orderByTanimoto) {
          tanimotoCache.put(user.getId(), new SoftReference<List<BggAvgRecommendation>>(recommendations));
        }
        else {
          correlationCache.put(user.getId(), new SoftReference<List<BggAvgRecommendation>>(recommendations));
        }
      }
      String sortByString = request.getParameter("sort_by");
      if (SORT_BY_NUM_RATINGS.equals(sortByString)) {
        recommendations = new ArrayList<BggAvgRecommendation>(recommendations);
        Collections.sort(recommendations, numRatingsComparator);
        params.put("sorted_by_num_ratings", true);
      }
      else if (SORT_BY_AVG_RATING.equals(sortByString)) {
        recommendations = new ArrayList<BggAvgRecommendation>(recommendations);
        Collections.sort(recommendations, avgRatingComparator);
        params.put("sorted_by_avg_rating", true);
      }
      else {
        params.put("sorted_by_weighted_rating", true);
      }

      params.put("orderByTanimoto", orderByTanimoto);
      params.put("similarity", orderByTanimoto ? "tanimoto" : "pearson");
      params.put("user", user);
      params.put("recommendations", recommendations);
      renderView(request, "recommended_games", params);
    }    
  }
  
  private class NumRatingsComparator implements Comparator<BggAvgRecommendation> {
    public int compare(BggAvgRecommendation o1, BggAvgRecommendation o2) {
      if (o1.getNumRatings() > o2.getNumRatings()) {
        return -1;
      }
      else if (o1.getNumRatings() < o2.getNumRatings()) {
        return 1;
      }
      else {
        return 0;
      }
    }    
  }
  
  private class AvgRatingComparator implements Comparator<BggAvgRecommendation> {
    public int compare(BggAvgRecommendation o1, BggAvgRecommendation o2) {
      if (o1.getAvgRating() > o2.getAvgRating()) {
        return -1;
      }
      else if (o1.getAvgRating() < o2.getAvgRating()) {
        return 1;
      }
      else {
        return 0;
      }
    }    
  }
}

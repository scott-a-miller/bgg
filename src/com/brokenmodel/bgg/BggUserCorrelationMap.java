/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.util.*;

public class BggUserCorrelationMap {
  private Map<Integer, List<BggCorrelatedUser>> map = new HashMap<Integer, List<BggCorrelatedUser>>();
  private List<BggUser> users;
  
  public BggUserCorrelationMap(List<BggUserCorrelation> correlations, final boolean orderByTanimoto) {
    Map<Integer, BggUser> userMap = new HashMap<Integer, BggUser>();
    for (BggUserCorrelation correlation : correlations) {
      addToMap(correlation.getUserA(), correlation.getUserB(), correlation.getCorrelation(), correlation.getCommonRatings(), correlation.getTanimoto());
      addToMap(correlation.getUserB(), correlation.getUserA(), correlation.getCorrelation(), correlation.getCommonRatings(), correlation.getTanimoto());
      userMap.put(correlation.getUserA().getId(), correlation.getUserA());
      userMap.put(correlation.getUserB().getId(), correlation.getUserB());
    }
    users = new ArrayList<BggUser>(userMap.values());
    Collections.sort(users, new Comparator<BggUser>() {
      public int compare(BggUser o1, BggUser o2) {
        return o1.getName().compareTo(o2.getName());
      }      
    });
    for (BggUser user : users) {
      Collections.sort(map.get(user.getId()), new Comparator<BggCorrelatedUser>() {
        public int compare(BggCorrelatedUser o1, BggCorrelatedUser o2) {
          if (orderByTanimoto) {
            return -1 * Float.compare(o1.getTanimoto(), o2.getTanimoto());
          }
          else {
            return -1 * Float.compare(o1.getCorrelation(), o2.getCorrelation());
          }
        }        
      });
    }
  }
  
  public List<BggCorrelatedUser> getCorrelatedUsers(BggUser user) {
    return map.get(user.getId());
  }
  
  public List<BggUser> getUsers() {
    return users;
  }
  
  private void addToMap(BggUser firstUser, BggUser secondUser, float correlation, int commonRatings, float tanimoto) {
    List<BggCorrelatedUser> users = map.get(firstUser.getId());
    if (users == null) {
      users = new ArrayList<BggCorrelatedUser>();
      map.put(firstUser.getId(), users);
    }
    users.add(new BggCorrelatedUser(secondUser, correlation, commonRatings, tanimoto));    
  }
}

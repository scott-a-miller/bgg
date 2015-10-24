/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg.server;

import java.net.URLEncoder;
import java.util.*;

import com.brokenmodel.bgg.*;
import com.brokenmodel.swats.ControllerRequest;

public class BggUserCorrelationsController extends BggController {
  private static final long serialVersionUID = 1L;
    
  protected void doRequest(ControllerRequest request, MysqlBggRepository rep) throws Exception {
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
      List<BggUserCorrelation> correlations = rep.getUserCorrelations(user, orderByTanimoto);
      BggUserCorrelationMap map = new BggUserCorrelationMap(correlations, orderByTanimoto);
      params.put("user", user);
      List<BggCorrelatedUser> correlatedUsers = map.getCorrelatedUsers(user);
      if (correlatedUsers.size() > 40) {
        correlatedUsers = correlatedUsers.subList(0, 40);
      }
      params.put("orderByTanimoto", orderByTanimoto);
      params.put("similarity", orderByTanimoto ? "tanimoto" : "pearson");
      params.put("users", correlatedUsers);
      renderView(request, "user_correlations", params);
    }    
  }  
}

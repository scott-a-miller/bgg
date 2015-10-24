/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg.server;

import java.net.URLEncoder;
import java.util.*;

import com.brokenmodel.bgg.*;
import com.brokenmodel.swats.ControllerRequest;

public class BggUserCorrelationsListController extends BggController {
  private static final long serialVersionUID = 1L;

  protected void doRequest(ControllerRequest request, MysqlBggRepository rep) throws Exception {
    String queryString = request.getParameter("query");
    String query = null;
    if (queryString != null) {
      queryString = queryString.trim();
      query = queryString.toLowerCase();
    }
    
    Map<String, Object> params = new HashMap<String, Object>();
    if (request.getParameter("all") != null) {
      params.put("users", rep.getCorrelatedUsersList());
      params.put("has_query", false);
    }
    else if (query == null || query.length() == 0) {
      params.put("users", new ArrayList<BggUser>(0));
      params.put("has_query", false);
    }
    else {
      query = URLEncoder.encode(query, "UTF-8");
      query = query.replace("+", "%20");      
      params.put("query", HTMLEntities.htmlentities(queryString));
      params.put("has_query", true);
      params.put("users", rep.getCorrelatedUsersList(query));
    }
    
    renderView(request, "user_correlations_list", params);
  }
}

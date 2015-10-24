/**
 * $Id: ControllerRoutes.java,v 1.2 2010/11/23 15:04:10 smiller Exp $
 * Copyright 2010 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.brokenmodel.swats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container of controllers with their respective routes
 */
public class ControllerRoutes {
  private List<Entry> entries = new ArrayList<Entry>();
  private AbstractController defaultController;
  private AbstractController unknownResourceController;
  
  public ControllerRoutes(AbstractController defaultController) {
    this.defaultController = defaultController;
    this.unknownResourceController = new UnknownResourceController();
  }
  
  public ControllerRoutes(AbstractController defaultController, AbstractController unknownResourceController) {
    this.defaultController = defaultController;
    this.unknownResourceController = unknownResourceController;
  }
  
  public ControllerRoutes add(List<Route> routes, AbstractController controller) {
    entries.add(new Entry(routes, controller));
    return this;
  }
  
  public ControllerRoutes add(Route route, AbstractController controller) {
    entries.add(new Entry(Arrays.asList(route), controller));
    return this;
  }
  
  public AbstractController getDefaultController() {
    return defaultController;
  }
  
  public MatchedRoute matchRoute(String path) throws Exception {
    if (path == null || path.length() == 0 || path.length() == 1) {
      return new MatchedRoute(defaultController, new HashMap<String,String>(0)); 
    }
    else {
      if (path.endsWith("/")) {
        path = path.substring(0, path.length()-1);
      }
      for (Entry entry : entries) {
        for (Route route : entry.getRoutes()) {
          Map<String, String> urlParams = route.matches(path);
          if (urlParams != null) {
            return new MatchedRoute(entry.getController(), urlParams);
          }
        }
      }
    }
    return new MatchedRoute(unknownResourceController, new HashMap<String,String>(0)); 
  }
  
  private class Entry {
    private List<Route> routes;
    private AbstractController controller;
    public Entry(List<Route> routes, AbstractController controller) {
      this.routes = routes;
      this.controller = controller;
    }
    public List<Route> getRoutes() {
      return routes;
    }
    public AbstractController getController() {
      return controller;
    }
  }
}

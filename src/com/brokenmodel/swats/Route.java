/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.swats;

import java.util.*;

/**
 * Defines a route for a controller. The string should follow standard
 * url conventions. Additionally, a path component can contain a colon
 * to indicate a parameter. e.g.
 * 
 * "/user_profile/:id"
 * 
 * will match
 * 
 * - /user profile/32  - and keep 32 as the "id" parameter
 * - /user_profile/77  - and keep 77 as the "id" parameter
 * 
 * The route can have any number of parameters.
 */
public class Route {
  private String route;
  private List<PathComponent> components = new ArrayList<PathComponent>();
  
  public Route(String route) {
    this.route = route;
    for (String componentString : route.split("\\/")) {
      if (componentString.startsWith(":")) {
        components.add(new PathComponent(componentString.substring(1), true));
      }
      else {
        components.add(new PathComponent(componentString, false));
      }
    }    
  }
  
  public String getRoute() {
    return route;
  }
  
  public Map<String, String> matches(String urnPath) {
    Map<String, String> map = new HashMap<String, String>();
    int pathIdx = 0;
    String[] pathElements = urnPath.split("\\/");
    if (pathElements == null) {
      return null;
    }
    for (String componentString : pathElements) {
      componentString = componentString.replace("`", "/");
      if (pathIdx >= components.size()) {
        return null;
      }
      PathComponent component = components.get(pathIdx++);
      if (component.param) {
        map.put(component.name, componentString);
      }
      else if (!component.name.equals(componentString)) {
        return null;
      }
    }
    if (pathIdx < components.size()){ 
      return null;
    }
    else {
      return map;
    }
  }
  
  private class PathComponent {
    public String name;
    public boolean param;
    public PathComponent(String name, boolean param) {
      this.name = name; this.param = param;
    }
  }  
}

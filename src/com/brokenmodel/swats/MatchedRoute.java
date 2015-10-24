/**
 * $Id: MatchedRoute.java,v 1.1 2010/11/23 14:57:51 smiller Exp $
 * Copyright 2010 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.brokenmodel.swats;

import java.util.Map;

/**
 * A matched route with controller and resulting url parameters
 */
public class MatchedRoute {
  private AbstractController controller;
  private Map<String, String> urlParams;
  
  public MatchedRoute(AbstractController controller, Map<String, String> urlParams) {
    this.controller = controller;
    this.urlParams = urlParams;
  }
  
  public AbstractController getController() {
    return controller;
  }
  
  public Map<String, String> getUrlParams() {
    return urlParams;
  }
}
/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.swats;


public class StaticViewController extends AbstractController {
  private static final long serialVersionUID = 1L;
  private String viewTemplate;
  
  public StaticViewController(String viewTemplate) {
    this.viewTemplate = viewTemplate;
  }
  
  public void doRequest(ControllerRequest request) throws Exception {
    renderView(request, viewTemplate);
  }
}

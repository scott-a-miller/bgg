/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.swats;

import org.antlr.stringtemplate.StringTemplate;

public class UnknownResourceController extends AbstractController implements ViewRenderer {
  public void doRequest(ControllerRequest request) throws Exception {
    renderView(request, "unknown_resource", this);
  }
  public void renderView(
      ControllerRequest request,
      StringTemplate view) throws Exception {
    log_info("Unknown resource request: " + request.getRequest().getPathInfo());
    view.setAttribute("path", request.getRequest().getPathInfo());
  }
}

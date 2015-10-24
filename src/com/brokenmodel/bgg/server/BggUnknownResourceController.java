/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg.server;

import java.util.HashMap;

import com.brokenmodel.swats.*;

public class BggUnknownResourceController extends AbstractController {
  private static final long serialVersionUID = 1L;

  public void doRequest(ControllerRequest request) throws Exception {
    log.info("Unknown resource request: " + request.getRequest().getPathInfo());
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("path", request.getRequest().getPathInfo());
    renderView(request, "unknown_resource", params);
  }
}

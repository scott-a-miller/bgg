/**
 * $Id: ViewRenderer.java,v 1.2 2010/09/29 16:12:27 smiller Exp $
 * Copyright 2010 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.brokenmodel.swats;

import org.antlr.stringtemplate.StringTemplate;

public interface ViewRenderer {
  public void renderView(
      ControllerRequest request,
      StringTemplate view) throws Exception;
}
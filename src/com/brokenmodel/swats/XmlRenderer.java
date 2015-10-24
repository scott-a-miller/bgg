/**
 * $Id: XmlRenderer.java,v 1.3 2010/10/05 14:54:42 smiller Exp $
 * Copyright 2010 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.brokenmodel.swats;

import javax.xml.stream.XMLStreamWriter;

public interface XmlRenderer {
  public void renderXml(
      ControllerRequest request,
      XMLStreamWriter xml) throws Exception;
}
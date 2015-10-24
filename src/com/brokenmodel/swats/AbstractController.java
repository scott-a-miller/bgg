/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.swats;

import java.io.*;
import java.net.URL;
import java.util.Map;
import java.util.logging.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.*;

import org.antlr.stringtemplate.*;

/**
 * The abstract class for all controllers. This abstraction handles setting
 * up the data source, logging, and preparing the template engine. Subclasses need 
 * only define their routes and implement renderView.
 */
public abstract class AbstractController {
  private static final String DEFAULT_ENCODING = "UTF-8";
  
  private static final long serialVersionUID = 1L;  
    
  private String controllerName;
  protected Logger log;
  private Level severe = Level.SEVERE;
      
  public AbstractController() {
    log = Logger.getLogger("com.leadscope.swats");
    String className = getClass().getName();
    controllerName = className.substring(className.lastIndexOf('.')+1, className.length());
  }
    
  public abstract void doRequest(ControllerRequest request) throws Exception;
  
  public String getControllerName() {
    return controllerName;
  }
  
  protected void log_info(String msg) {
    log.info(controllerName + ": " + msg);
  }

  protected void log_warn(String msg) {
    log.warning(controllerName + ": " + msg);
  }

  protected void log_severe(String msg) {
    log.severe(controllerName + ": " + msg);
  }

  protected void log_severe(String msg, Throwable t) {
    log.log(severe, controllerName + ": " + msg, t);
  }

  protected void log(Throwable t) {
    log.log(severe, "Error in " + controllerName, t);
  }
    
  private StringTemplate getView(ControllerRequest request, String name) {
    StringTemplateGroup group = new StringTemplateGroup("views");
    StringTemplate view = group.getInstanceOf("views/" + name);
    view.setAttribute("app_root", request.getAppRoot());
    view.setAttribute("html_root", request.getHtmlRoot());
    view.setAttribute("session_id", request.getSession().getId());
    return view;
  }
  
  protected boolean isCachingEnabled() {
    if (this instanceof XmlRenderer) {
      return false;
    }
    return true;
  }
  
  protected void disableCaching(ControllerRequest request) throws Exception {
    request.getResponse().setHeader("Cache-Control", "no-cache");
    request.getResponse().setHeader("Pragma", "no-cache");
    request.getResponse().setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
  }
  
  protected void renderBytes(
      ControllerRequest request,
      byte[] bytes,
      String mimeType,
      String fileName) throws Exception {
    ServletOutputStream os = request.getResponse().getOutputStream();
    if (!isCachingEnabled()) {
      disableCaching(request);
    }    
    request.getResponse().setContentType(mimeType);
    
    if (fileName != null) {
      request.getResponse().setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
    }
    
    os.write(bytes, 0, bytes.length);
    os.flush();
  }
  
  protected void renderInputStream(
      ControllerRequest request,
      InputStream is,
      String mimeType,
      String fileName) throws Exception {
    ServletOutputStream os = request.getResponse().getOutputStream();
    if (!isCachingEnabled()) {
      disableCaching(request);
    }    
    request.getResponse().setContentType(mimeType);
    
    if (fileName != null) {
      request.getResponse().setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
    }
    
    byte[] buffer = new byte[1024];
    for (int bytesRead = is.read(buffer); bytesRead >= 0; bytesRead = is.read(buffer)) {
      os.write(buffer, 0, bytesRead);
    }
    os.flush();
  }
  
  /**
   * @param request the incoming request
   * @param viewTemplate the name of the template to renderer
   */
  protected void renderView(
      ControllerRequest request,
      String viewTemplate) throws Exception {
    ServletOutputStream os = request.getResponse().getOutputStream();
    if (!isCachingEnabled()) {
      disableCaching(request);
    }    
    request.getResponse().setContentType("text/html");
    
    StringTemplate view = getView(request, viewTemplate);
    os.print(view.toString());
    os.flush();
  }
  
  /**
   * @param request the incoming request
   * @param viewTemplate the name of the template to renderer
   * @param renderer a renderer that can provide values to the view; null is ok
   */
  protected void renderView(
      ControllerRequest request,
      String viewTemplate,
      ViewRenderer renderer) throws Exception {
    ServletOutputStream os = request.getResponse().getOutputStream();
    if (!isCachingEnabled()) {
      disableCaching(request);
    }    
    request.getResponse().setContentType("text/html");
    
    StringTemplate view = getView(request, viewTemplate);
    if (renderer != null) {
      renderer.renderView(request, view);
    }
    os.print(view.toString());
    os.flush();
  }

  /**
   * @param request the incoming request
   * @param viewTemplate the name of the template to renderer
   * @param params the parameters that should be passed to the view
   */
  protected void renderView(
      ControllerRequest request,
      String viewTemplate,
      Map<String, Object> params) throws Exception {
    ServletOutputStream os = request.getResponse().getOutputStream();
    if (!isCachingEnabled()) {
      disableCaching(request);
    }    
    request.getResponse().setContentType("text/html");
    
    StringTemplate view = getView(request, viewTemplate);
    if (params != null) {
      for (String key : params.keySet()) {
        view.setAttribute(key, params.get(key));
      }
    }
    os.print(view.toString());
    os.flush();
  }

  protected void renderPlainText(
      ControllerRequest request,
      String value) throws Exception {
    OutputStream os = request.getResponse().getOutputStream();
    if (!isCachingEnabled()) {
      disableCaching(request);
    }
    request.getResponse().setContentType("text/plain");
      
    OutputStreamWriter writer = new OutputStreamWriter(os, DEFAULT_ENCODING);
    writer.write(value);
    writer.flush();
  }
  
  protected void renderXml(
      ControllerRequest request,
      XmlRenderer renderer) throws Exception {
    OutputStream os = request.getResponse().getOutputStream();
    if (!isCachingEnabled()) {
      disableCaching(request);
    }    
    request.getResponse().setContentType("text/xml");
        
    XMLOutputFactory xof = XMLOutputFactory.newInstance();
    XMLStreamWriter serializer = xof.createXMLStreamWriter(os, "UTF-8");
    serializer.writeStartDocument();
    try {
      renderer.renderXml(request, serializer);
    }
    catch (Throwable t) {
      log(t);
      try {
	serializer.writeStartElement("Exception");
	StringWriter sw = new StringWriter();
	t.printStackTrace(new PrintWriter(sw));
	serializer.writeCData(sw.toString());
	serializer.writeEndElement();
      }
      catch (Exception e) { }
    }
    serializer.writeEndDocument();
  }
  
  protected void redirect( 
      ControllerRequest request,
      String relativeUrl) throws Exception {
    request.getResponse().setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    request.getResponse().setHeader("Location", request.getAppRoot() + "/" + relativeUrl);
  }
  
  protected void redirect( 
      ControllerRequest request,
      URL url) throws Exception {
    request.getResponse().setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    request.getResponse().setHeader("Location", url.toString());
  }
  
  protected void toXml(String tag, String value, XMLStreamWriter xml) throws Exception {
    if (value != null) {
      xml.writeStartElement(tag);
      xml.writeCharacters(value);
      xml.writeEndElement();
    }
  }

  protected void toXmlCdata(String tag, String value, XMLStreamWriter xml) throws Exception {
    if (value != null) {
      xml.writeStartElement(tag);
      xml.writeCData(value);
      xml.writeEndElement();
    }
  }
  
  protected void toXmlNull(String tag, String value, XMLStreamWriter xml) throws Exception {
    xml.writeStartElement(tag);
    if (value != null) {
      xml.writeCharacters(value);
    }
    xml.writeEndElement();
  }

  protected void toXmlCdataNull(String tag, String value, XMLStreamWriter xml) throws Exception {
    xml.writeStartElement(tag);
    if (value != null) {
      xml.writeCData(value);
    }
    xml.writeEndElement();
  }
}

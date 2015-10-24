/**
 * $Id: ControllerRequest.java,v 1.6 2010/11/22 22:06:35 smiller Exp $
 * Copyright 2010 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.brokenmodel.swats;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.fileupload.FileItem;

/**
 * Collection of objects as part of a request for a controller
 */
public class ControllerRequest {
  private HttpServletRequest request; 
  private HttpServletResponse response;
  private String appRoot;
  private String htmlRoot;
  private Map<String, String> urlParams;
  private Type type;
  private List<FileItem> fileItems;
  private DataSource dataSource;
  
  public static enum Type {
    GET, POST, PUT, DELETE
  }

  public ControllerRequest(HttpServletRequest request,
      HttpServletResponse response, String appRoot,
      String htmlRoot,
      Map<String, String> urlParams, Type type,
      List<FileItem> fileItems,
      DataSource dataSource) {    
    this.request = request;
    this.response = response;
    this.appRoot = appRoot;
    this.htmlRoot = htmlRoot;
    this.urlParams = urlParams;
    this.type = type;
    this.fileItems = fileItems;
    this.dataSource = dataSource;
  }

  public String getParameter(String param) {
    String urlParam = urlParams.get(param);
    if (urlParam != null) {
      return urlParam;
    }
    else {
      return request.getParameter(param);
    }
  }
  
  public DataSource getDataSource() {
    return dataSource;
  }
  
  public List<FileItem> getFileItems() {
    return fileItems;
  }
  
  public HttpSession getSession() {
    return request.getSession();
  }
  
  public HttpServletRequest getRequest() {
    return request;
  }
  
  public HttpServletResponse getResponse() {
    return response;
  }

  public String getAppRoot() {
    return appRoot;
  }
  
  public String getHtmlRoot() {
    return htmlRoot;
  }

  public Map<String, String> getUrlParams() {
    return urlParams;
  }

  public Type getType() {
    return type;
  }  
}

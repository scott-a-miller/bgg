/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.swats;

import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public abstract class RouterServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;  
  private ControllerRoutes controllers;
  private ServletFileUpload fileUpload;
  
  protected Logger log;
  private Level severe = Level.SEVERE;
    
  public RouterServlet(ControllerRoutes controllers) {
    this.controllers = controllers;
    log = Logger.getLogger("com.leadscope.swats");
    fileUpload = new ServletFileUpload(new DiskFileItemFactory());
  }
  
  protected void log(Throwable t) {
    log.log(severe, "Error in controller", t);
  }
      
  public abstract DataSource getDataSource();
  
  private void handleRequest(HttpServletRequest request, HttpServletResponse response, ControllerRequest.Type type) {    
    try {
      URL rootURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), 
	  request.getContextPath() + request.getServletPath());

      String appRoot = rootURL.getFile();
      String htmlRoot = request.getContextPath();

      MatchedRoute matchedRoute = controllers.matchRoute(request.getPathInfo());      
      ControllerRequest controllerRequest = new ControllerRequest(
          request, response, appRoot, htmlRoot, matchedRoute.getUrlParams(), type,
          handleMultipart(request),
          getDataSource());
      AbstractController controller = matchedRoute.getController();
      controller.doRequest(controllerRequest);
    }
    catch (Throwable t) {
      log(t);
      try {
	// only will work if output stream has not been opened
	PrintWriter pw = new PrintWriter(response.getWriter()); 
	pw.append("<pre>");
	pw.append("We're sorry - an error has occurred:\n\n");
	t.printStackTrace(pw);
	pw.append("</pre>");
      }
      catch (Throwable t2) { }
    }
  }
  
  private List<FileItem> handleMultipart(HttpServletRequest request) throws Exception {
    List<FileItem> fileItems = new ArrayList<FileItem>();
    if (ServletFileUpload.isMultipartContent(request)) {
      for (Object itemObj : fileUpload.parseRequest(request)) {
	fileItems.add((FileItem)itemObj);
      }
    }
    return fileItems;
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    handleRequest(request, response, ControllerRequest.Type.GET);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    handleRequest(request, response, ControllerRequest.Type.POST);
  }
  
  public void doPut(HttpServletRequest request, HttpServletResponse response) {
    handleRequest(request, response, ControllerRequest.Type.PUT);
  }
  
  public void doDelete(HttpServletRequest request, HttpServletResponse response) {
    handleRequest(request, response, ControllerRequest.Type.DELETE);
  }
}

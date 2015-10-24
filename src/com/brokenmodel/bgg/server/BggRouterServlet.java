/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg.server;

import java.util.Arrays;

import javax.naming.*;
import javax.sql.DataSource;

import com.brokenmodel.swats.*;

public class BggRouterServlet extends RouterServlet {
  private static final long serialVersionUID = 1L;  
  
  public BggRouterServlet() {
    super(new ControllerRoutes(new StaticViewController("index"), new BggUnknownResourceController()).
        add(new Route("/index"), new StaticViewController("index")).
        add(new Route("/about"), new StaticViewController("about")).
        add(new Route("/game_correlations/:game_id"), new BggGameCorrelationsController()).
        add(Arrays.asList(
            new Route("/recommended_games/:username/:similarity/:sort_by"), 
            new Route("/recommended_games/:username/:similarity"), 
            new Route("/recommended_games/:username"), 
            new Route("/recommended_games")), new BggRecommendedGamesController()).
        add(Arrays.asList(
            new Route("/user_correlations/:username"),
            new Route("/user_correlations/:username/:similarity")), new BggUserCorrelationsController()).
        add(Arrays.asList(
            new Route("/user_correlations_list/:all"),
            new Route("/user_correlations_list")), new BggUserCorrelationsListController()).
        add(new Route("/game_correlations"), new BggGameCorrelationsListController())); 
  }

  private static DataSource dataSource;
  static {
    try {
      Context ctx = new InitialContext();
      Context env = (Context) ctx.lookup("java:/comp/env/");
      dataSource = (DataSource) env.lookup("jdbc/bgg");
    }
    catch (NoInitialContextException e) {
      System.out.println("Running outside of jndi container");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public DataSource getDataSource() {
    return dataSource;
  }
}

/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg.server;

import java.util.List;

import org.antlr.stringtemplate.StringTemplate;

import com.brokenmodel.bgg.*;
import com.brokenmodel.swats.*;

public class BggGameCorrelationsController extends BggController {
  private static final long serialVersionUID = 1L;
    
  public void doRequest(ControllerRequest request, final MysqlBggRepository rep) throws Exception {
    final String idString = request.getParameter("game_id");
    try {
      int id = Integer.parseInt(idString);
      final BggGame game = rep.getGameById(id);
      if (game == null) {
        throw new RuntimeException("Unknown game id: " + id);
      }
      else {
        renderView(request, "game_correlations", new ViewRenderer() {
          public void renderView(ControllerRequest request, StringTemplate view) throws Exception {
            List<BggGameCorrelation> correlations = rep.getGameCorrelations(game);
            BggGameCorrelationMap map = new BggGameCorrelationMap(correlations);
            view.setAttribute("game", game);
            view.setAttribute("games", map.getCorrelatedGames(game));
          }          
        });
      }
    }
    catch (Exception e) {
      log(e);
      renderView(request, "invalid_game_id", new ViewRenderer() {
        public void renderView(ControllerRequest request, StringTemplate view) throws Exception {
          view.setAttribute("game_id", idString);
        }        
      });
    }
  }
}

/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg.server;

import org.antlr.stringtemplate.StringTemplate;

import com.brokenmodel.bgg.MysqlBggRepository;
import com.brokenmodel.swats.*;

public class BggGameCorrelationsListController extends BggController {
  private static final long serialVersionUID = 1L;

  public void doRequest(ControllerRequest request, final MysqlBggRepository rep) throws Exception {
    renderView(request, "game_correlations_list", new ViewRenderer() {
      public void renderView(ControllerRequest request, StringTemplate view) throws Exception {
        view.setAttribute("games", rep.getCorrelatedGamesList());
      }           
    });
  }
}

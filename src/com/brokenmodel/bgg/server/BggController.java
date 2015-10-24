/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg.server;

import com.brokenmodel.bgg.MysqlBggRepository;
import com.brokenmodel.swats.*;

public abstract class BggController extends AbstractController {
  @Override
  public void doRequest(ControllerRequest request) throws Exception {
    MysqlBggRepository rep = new MysqlBggRepository(request.getDataSource().getConnection());
    try {
      doRequest(request, rep);
    }
    finally {
      rep.close();
    }
  }

  protected abstract void doRequest(ControllerRequest request, MysqlBggRepository rep) throws Exception;
}

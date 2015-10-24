/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.util.concurrent.*;

public class RepositorySource {
  private BlockingQueue<MysqlBggRepository> reps = new LinkedBlockingQueue<MysqlBggRepository>();
  private int repCount;
  private boolean closing;
  
  public RepositorySource(int repCount) throws Exception {
    this.repCount = repCount;    
    for (int i = 0; i < repCount; i++) {
      reps.add(new MysqlBggRepository());
    }
  }
  
  public MysqlBggRepository getRepository() throws InterruptedException {
    if (!closing) {
      return reps.poll(15, TimeUnit.SECONDS);
    }
    else {
      return null;
    }
  }
  
  public void returnRepository(MysqlBggRepository rep) throws InterruptedException {
    reps.put(rep);
  }
  
  public void close() {
    int closedRepCount = 0;
    closing = true;
    try {
      while (closedRepCount < repCount) {
        reps.poll(1, TimeUnit.MINUTES).close();
        closedRepCount++;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

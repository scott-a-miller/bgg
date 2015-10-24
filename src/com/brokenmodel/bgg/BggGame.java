/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.io.Serializable;
import java.text.DecimalFormat;

public class BggGame implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private String name;
  private int id = -1;
  private int bggId;
  private Integer numRatings;
  private String thumbUrl;
  private float avgBggRating = Float.NaN;
    
  private static DecimalFormat ratingFormat = new DecimalFormat("#0.000"); 
  
  public BggGame() { }
    
  public BggGame(int id, String name, int bggId, String thumbUrl, float avgBggRating) {
    this.id = id;
    this.name = name;
    this.bggId = bggId;
    this.thumbUrl = thumbUrl;
    this.avgBggRating = avgBggRating;
  }

  public String getName() {
    return name;
  }

  public String getHtmlName() {
    return HTMLEntities.htmlentities(name);
  }
  
  public int getBggId() {
    return bggId;
  }

  public void setBggId(int bggId) {
    this.bggId = bggId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  public String getBggUrl() {
    return "http://boardgamegeek.com/boardgame/" + bggId;
  }
  
  public void setNumRatings(int numRatings) {
    this.numRatings = numRatings;
  }
  
  public Integer getNumRatings() {
    return numRatings;
  }

  public void setThumbUrl(String thumbUrl) {
    this.thumbUrl = thumbUrl;
  }
  
  public String getThumbUrl() {
    return thumbUrl;
  }
  
  public void setAvgBggRating(float r) {
    this.avgBggRating = r;
  }
  
  public float getAvgBggRating() {
    return avgBggRating;
  }
  
  public String getFormattedAvg() {
    if (Float.isNaN(avgBggRating)) {
      return "No BGG Rating";
    }
    return ratingFormat.format(avgBggRating);
  }
  
  @Override
  public String toString() {
    return "BggGame [bggId=" + bggId + ", id=" + id + ", name=" + name
        + ", numRatings=" + numRatings + "]";
  }  
}

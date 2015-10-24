package com.brokenmodel.bgg;

import java.io.Serializable;
import java.net.URLDecoder;

public class BggUser implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private String name;
  private int id = -1;
  private Integer numRatings;
  private float superlativeThreshold = 8f;
  
  public BggUser(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getDisplayName() {
    try {
      return URLDecoder.decode(name, "UTF-8");
    }
    catch (Exception e) {
      return name;
    }
  }
  
  public String getHtmlName() {
    return HTMLEntities.htmlentities(getDisplayName());
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNumRatings(int numRatings) {
    this.numRatings = new Integer(numRatings);
  }
  
  public Integer getNumRatings() {
    return numRatings;
  }
  
  public String getBggUrl() {
    return "http://boardgamegeek.com/user/" + name;
  }
  
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public float getSuperlativeThreshold() {
    return superlativeThreshold;
  }

  public void setSuperlativeThreshold(float superlativeThreshold) {
    this.superlativeThreshold = superlativeThreshold;
  }  
}

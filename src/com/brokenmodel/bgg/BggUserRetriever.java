/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.xmlpull.v1.*;

public class BggUserRetriever {
  public static void retrieveRatings(String username, MysqlBggRepository rep) throws Exception {
    BggUser user = new BggUser(-1, username);

    user = rep.addUser(user);
    
    rep.startTransaction();
    
    try {      
      URL url = new URL("http://www.boardgamegeek.com/xmlapi/collection/" + user.getName() + "?rated=1");
      InputStream is = url.openStream();
      
      XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
      parser.setInput(is, "UTF8");
      
      BggGame game = null;
      BggRating rating = null;
      
      int result = parser.getEventType();    
      while (result != XmlPullParser.END_DOCUMENT) {
        if (result == XmlPullParser.START_TAG) {
          if ("item".equals(parser.getName())) {
            int gameId = Integer.parseInt(parser.getAttributeValue(null, "objectid"));
            game = new BggGame();
            game.setBggId(gameId);
            rating = new BggRating(user, game);
          }
          else if ("name".equals(parser.getName())) {          
            game.setName(parser.nextText());
          }
          else if ("thumbnail".equals(parser.getName())) {
            game.setThumbUrl(parser.nextText());
          }
          else if ("rating".equals(parser.getName())) {
            String ratingString = parser.getAttributeValue(null, "value");
            if (ratingString != null) {
              rating.setRating(Float.parseFloat(ratingString));
            }
          }
          else if ("rank".equals(parser.getName()) && "boardgame".equals(parser.getAttributeValue(null, "name"))) {
            String bggRatingString = parser.getAttributeValue(null, "bayesaverage");
            if (bggRatingString != null && !"Not Ranked".equals(bggRatingString)) {
              rating.setBggRating(Float.parseFloat(bggRatingString));
            }
          }
        }
        else if (result == XmlPullParser.END_TAG) { 
          if ("item".equals(parser.getName())) {
            if (!Float.isNaN(rating.getRating())) {
              game = rep.addGame(game);
              rating.setGame(game);
              rating = rep.addRating(rating);
            }
            game = null;
            rating = null;
          }
        }
        result = parser.next();
      }
      
      rep.commit();
    }
    catch (Exception e) {
      try {rep.rollback();} catch (Exception e2) { }
      throw e;
    }
    catch (Throwable t) {
      try {rep.rollback();} catch (Exception e2) { }
      throw new RuntimeException(t);
    }
  }  
  
  private static class UserFileIterator implements Iterator<String> {
    private BufferedReader reader;
    private String nextLine;
    public UserFileIterator(String filename) throws Exception {
      reader = new BufferedReader(new FileReader(filename));
      nextLine = reader.readLine();
    }
    public boolean hasNext() {
      return nextLine != null;
    }
    public String next() {
      return nextLine; 
    }
    public void remove() { }
    public void close() {
      try {
        reader.close();
      }
      catch (Exception e) { }
    }
  }
  
  public static void main(String[] args) {
    Iterator<String> iter = null; 
    try {
      iter = new UserFileIterator("scrape_users/all_usernames.txt");
      // iter = Arrays.asList("mpevans").iterator();
      MysqlBggRepository rep = new MysqlBggRepository();
      int count = 0;
      while (iter.hasNext()) {
        String line = iter.next();
        count++;
        System.out.println("Checking ratings for user (" + count + "): " + line);
        try {          
          String name = line.trim(); 
          BggUser user = rep.getUser(name);
          if (user == null || rep.countUserRatings(user) == 0) {
            System.out.println("\tRetrieving ratings");
            BggUserRetriever.retrieveRatings(name, rep);
          }
          else {
            System.out.println("\tAlready have ratings");
          }
        }
        catch (Exception e) {
          System.out.println("\tFailed retrieving ratings for: " + e.getMessage());
          e.printStackTrace();
        }
      }  
      try { 
        rep.close(); 
        if (iter instanceof UserFileIterator) { ((UserFileIterator)iter).close(); }
      } catch (Exception e) { }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.io.InputStream;
import java.net.URL;

import org.xmlpull.v1.*;

public class BggGameRatingFetcher {

  public static void main(String[] args) {
    try {
      MysqlBggRepository rep = new MysqlBggRepository();      
      try {
        for (BggGame game : rep.getCorrelatedGamesList()) {
          URL url = new URL("http://www.boardgamegeek.com/xmlapi/boardgame/" + game.getBggId() + "?stats=1");
          InputStream is = url.openStream();
          
          XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
          parser.setInput(is, "UTF8");

          int result = parser.getEventType();
          
          boolean ratingFound = false;
          while (result != XmlPullParser.END_DOCUMENT) {
            if (result == XmlPullParser.START_TAG) {
              if ("average".equals(parser.getName())) {
                Float rating = Float.parseFloat(parser.nextText());
                game.setAvgBggRating(rating);
                rep.setAvgBggRating(game, rating);
                if (ratingFound) {
                  throw new RuntimeException("Already found one rating");
                }
                else{
                  System.out.println("Set (" + game.getBggId() + ") " + game.getName() + " to: " + rating);
                  ratingFound = true;
                }
              }
            }
            result = parser.next();
          }
        }
      }
      finally {
        try {rep.close();} catch (Exception e) { }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }         
  }

}

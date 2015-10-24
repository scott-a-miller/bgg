/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.sql.*;
import java.util.*;

public class MysqlBggRepository {
  private Connection conn;
  private boolean closed;
  
  public MysqlBggRepository() throws Exception {
    Class.forName("com.mysql.jdbc.Driver");
    String url = "jdbc:mysql://localhost:3306/bgg_2011";
    conn = DriverManager.getConnection(url,"root", "");
  }
  
  public MysqlBggRepository(Connection connection) throws Exception {
    conn = connection;
  }
  
  public void startTransaction() throws Exception {
    conn.setAutoCommit(false);
  }
  
  public void commit() throws Exception {
    try {
      conn.commit();
    }
    finally {
      conn.setAutoCommit(true);
    }
  }
  
  public void rollback() throws Exception {
    try {
      conn.rollback();
    }
    finally {
      conn.setAutoCommit(true);
    }
  }
  
  public BggUser getUser(String name) throws Exception {
    String sql = "select id, name from users where name = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setString(1, name);
      ResultSet result = stmt.executeQuery();
      if (result.next()) {
        return new BggUser(result.getInt(1),result.getString(2));
      }
      else {
        return null;
      }
    }
    finally {
      try { stmt.close();} catch (Exception e) {}
    }
  }
  
  public int countUserRatings(BggUser user) throws Exception {
    String sql = "select count(user_id) from ratings where user_id = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setInt(1, user.getId());
      ResultSet result = stmt.executeQuery();
      if (result.next()) {
        return result.getInt(1);
      }
      else {
        return 0;
      }
    }
    finally {
      try { stmt.close();} catch (Exception e) {}
    }    
  }
  
  public BggUser addUser(BggUser newUser) throws Exception {
    BggUser oldUser = getUser(newUser.getName());
    if (oldUser != null) {
      return oldUser;
    }    
    String sql = "insert into users(name) values(?)";
    PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    try {
      stmt.setString(1, newUser.getName());
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        return new BggUser(rs.getInt(1), newUser.getName());
      }
      else {
        throw new RuntimeException("No auto generated key");
      }
    }
    finally {
      try { stmt.close();} catch (Exception e) { }
    }
  }

  public void removeRatings(BggUser user) throws Exception {
    String sql = "delete from ratings where user_id = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setInt(1, user.getId());
      stmt.executeUpdate();
    }
    finally {
      try {stmt.close();} catch (Exception e) { }
    }
  }
  
  public BggGame getGame(String name) throws Exception {
    return getGameByNameOrBggId(name, -1);
  }
  
  public BggGame getGameById(int id) throws Exception {
    String sql = "select id, name, bgg_id, thumb_url, avg_bgg_rating from games where id = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setInt(1, id); 
      ResultSet result = stmt.executeQuery();
      if (result.next()) {
        return new BggGame(
            result.getInt(1), 
            result.getString(2), 
            result.getInt(3), 
            result.getString(4), 
            result.getFloat(5));
      }
      else {
        return null;
      }
    }
    finally {
      try { stmt.close();} catch (Exception e) {}
    }    
  }
  
  public BggGame getGameByBggId(int bggId) throws Exception {
    return getGameByNameOrBggId(null, bggId);
  }
  
  public BggGame getGameByNameOrBggId(String name, int bggId) throws Exception {
    String sql = "select id, name, bgg_id, thumb_url, avg_bgg_rating from games where name = ? or bgg_id = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setString(1, name);
      stmt.setInt(2, bggId); 
      ResultSet result = stmt.executeQuery();
      if (result.next()) {
        return new BggGame(
            result.getInt(1), 
            result.getString(2), 
            result.getInt(3), 
            result.getString(4), 
            result.getFloat(5));
      }
      else {
        return null;
      }
    }
    finally {
      try { stmt.close();} catch (Exception e) {}
    }
  }

  public BggGame addGame(BggGame newGame) throws Exception {
    BggGame oldGame = getGameByNameOrBggId(newGame.getName(), newGame.getBggId());
    if (oldGame != null) {
      return oldGame;
    }    
    String sql = "insert into games(name, bgg_id, thumb_url, avg_bgg_rating) values(?, ?, ?, ?)";
    PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    try {
      stmt.setString(1, newGame.getName());
      stmt.setInt(2, newGame.getBggId());
      stmt.setString(3, newGame.getThumbUrl());
      stmt.setFloat(4, newGame.getAvgBggRating());
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        return new BggGame(rs.getInt(1), newGame.getName(), newGame.getBggId(), newGame.getThumbUrl(), newGame.getAvgBggRating());
      }
      else {
        throw new RuntimeException("No auto generated key");
      }
    }
    finally {
      try { stmt.close();} catch (Exception e) { }
    }
  }

  public BggRating getRating(BggUser user, BggGame game) throws Exception {
    String sql = "select rating, bgg_rating from ratings where user_id = ? and game_id = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setInt(1, user.getId());
      stmt.setInt(2, game.getId());
      ResultSet result = stmt.executeQuery();
      if (result.next()) { 
        return new BggRating(user, game, result.getFloat(1), result.getFloat(2));
      }
      else {
        return null;
      }
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }
  }
  
  public List<BggGame> getMostRatedGames(int minRatings) throws Exception {
    String sql = "select id, name, bgg_id, thumb_url, avg_bgg_rating, num_ratings from (select game_id, count(game_id) num_ratings from ratings group by game_id order by num_ratings desc) as ratings_counts, games where num_ratings >= ? and id = game_id;";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setInt(1, minRatings);
      ResultSet result = stmt.executeQuery();
      List<BggGame> games = new ArrayList<BggGame>();
      while (result.next()) { 
        BggGame game = new BggGame(
            result.getInt(1), 
            result.getString(2), 
            result.getInt(3), 
            result.getString(4), 
            result.getFloat(5));
        game.setNumRatings(result.getInt(6));
        games.add(game);
      }
      return games;
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }    
  }

  public void setGameAsCorrelated(BggGame game, boolean correlated) throws Exception {
    String sql = "update games set correlated = ? where id = ?";
    PreparedStatement stmt = null;    
    try {
      stmt = conn.prepareStatement(sql);
      stmt.setBoolean(1, correlated);
      stmt.setInt(2, game.getId());
      stmt.executeUpdate();
    }
    finally {
      if (stmt != null) {
        try {stmt.close(); } catch (Exception e) { }
      }
    }
  }
  
  public List<BggGame> getCorrelatedGamesList() throws Exception {
    String sql = "select id, name, bgg_id, thumb_url, avg_bgg_rating from games where correlated = 1 order by name";
    PreparedStatement stmt =  conn.prepareStatement(sql);
    try {
      ResultSet result = stmt.executeQuery();
      List<BggGame> games = new ArrayList<BggGame>();
      while (result.next()) {
        BggGame game = new BggGame(
            result.getInt(1), 
            result.getString(2), 
            result.getInt(3), 
            result.getString(4), 
            result.getFloat(5));
        games.add(game);
      }
      return games;
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }    
  }

  public void setUserAsCorrelated(BggUser user, boolean correlated) throws Exception {
    String sql = "update users set correlated = ? where id = ?";
    PreparedStatement stmt = null;    
    try {
      stmt = conn.prepareStatement(sql);
      stmt.setBoolean(1, correlated);
      stmt.setInt(2, user.getId());
      stmt.executeUpdate();
    }
    finally {
      if (stmt != null) {
        try {stmt.close(); } catch (Exception e) { }
      }
    }
  }
  
  public List<BggUser> getCorrelatedUsersList() throws Exception {
    String sql = "select id, name from users where correlated = 1 order by name";
    PreparedStatement stmt =  conn.prepareStatement(sql);
    try {
      ResultSet result = stmt.executeQuery();
      List<BggUser> users = new ArrayList<BggUser>();
      while (result.next()) {
        users.add(new BggUser(result.getInt(1), result.getString(2)));
      }
      return users;
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }    
  }

  public List<BggUser> getCorrelatedUsersList(String queryString) throws Exception {
    String sql = "select id, name from users where name = ? and correlated = 1 order by name";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setString(1, queryString);
      ResultSet result = stmt.executeQuery();
      List<BggUser> users = new ArrayList<BggUser>();
      while (result.next()) {
        users.add(new BggUser(result.getInt(1), result.getString(2)));
      }
      return users;
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }    
  }
  
  public BggRating addRating(BggRating rating) throws Exception {
    BggRating oldRating = getRating(rating.getUser(), rating.getGame());
    PreparedStatement stmt = null;
    try {
      if (oldRating == null) {
        String sql = "insert into ratings(user_id, game_id, rating, bgg_rating) values(?, ?, ?, ?)";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, rating.getUser().getId());
        stmt.setInt(2, rating.getGame().getId());
        if (!Float.isNaN(rating.getRating())) {
          stmt.setFloat(3, rating.getRating());
        }
        else {
          stmt.setNull(3, Types.FLOAT);
        }
        if (!Float.isNaN(rating.getBggRating())) {
          stmt.setFloat(4, rating.getBggRating());
        }
        else {
          stmt.setNull(4, Types.FLOAT);
        }
        stmt.executeUpdate();
        return new BggRating(rating.getUser(), rating.getGame(), rating.getRating(), rating.getBggRating());
      }
      else {
        String sql = "update ratings set rating = ?, bgg_rating = ? where user_id = ? and game_id = ?";
        stmt = conn.prepareStatement(sql);
        if (!Float.isNaN(rating.getRating())) {
          stmt.setFloat(1, rating.getRating());
        }
        else {
          stmt.setNull(1, Types.FLOAT);
        }
        if (!Float.isNaN(rating.getBggRating())) {
          stmt.setFloat(2, rating.getBggRating());
        }
        else {
          stmt.setNull(2, Types.FLOAT);
        }        
        stmt.setInt(3, oldRating.getUser().getId());
        stmt.setInt(4, oldRating.getGame().getId());
        stmt.executeUpdate();
        return new BggRating(rating.getUser(), rating.getGame(), rating.getRating(), rating.getBggRating());
      }
    }
    finally {
      if (stmt != null) {
        try {stmt.close(); } catch (Exception e) { }
      }
    }
  }
  
  public BggGameRatingList getGameRatingList(BggGame game) throws Exception {    
    String sql = "select user_id, rating from ratings where game_id = ? order by user_id";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setInt(1, game.getId());
      ResultSet result = stmt.executeQuery();
      List<Integer> ids = new ArrayList<Integer>();
      List<Float> ratings = new ArrayList<Float>();
      while (result.next()) {
        ids.add(result.getInt(1));
        ratings.add(result.getFloat(2));
      }
      return new BggGameRatingList(game, ids, ratings);
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }    
  }

  public BggGameCorrelation getCorrelation(BggGame aGame, BggGame anotherGame) throws Exception {
    BggGameCorrelation checkCorrelation = new BggGameCorrelation(aGame, anotherGame, Float.NaN, 0);
    String sql = "select correlation, common_ratings from game_correlations where game_id_a = ? and game_id_b = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {      
      stmt.setInt(1, checkCorrelation.getGameA().getId());
      stmt.setInt(2, checkCorrelation.getGameB().getId());
      ResultSet result = stmt.executeQuery();
      if (result.next()) { 
        return new BggGameCorrelation(checkCorrelation.getGameA(), checkCorrelation.getGameB(), result.getFloat(1), result.getInt(2));
      }
      else {
        return null;
      }
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }
  }

  /**
   * Slower than getCorrelatedGamesList but needed if correlated games have not been set
   */
  public List<BggGame> getCorrelatedGames() throws Exception {
    String sql = "select distinct id, name, bgg_id, thumb_url, avg_bgg_rating from games, game_correlations where id = game_id_a or id = game_id_b order by name";
    PreparedStatement stmt =  conn.prepareStatement(sql);
    try {
      ResultSet result = stmt.executeQuery();
      List<BggGame> games = new ArrayList<BggGame>();
      while (result.next()) {
        BggGame game = new BggGame(
            result.getInt(1), 
            result.getString(2), 
            result.getInt(3), 
            result.getString(4), 
            result.getFloat(5));
        games.add(game);
      }
      return games;
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }
  }

  public void setAvgBggRating(BggGame game, float rating) throws Exception {
    String sql = "update games set avg_bgg_rating = ? where id = ?";
    PreparedStatement stmt =  conn.prepareStatement(sql);
    try {
      stmt.setFloat(1, rating);
      stmt.setInt(2, game.getId());
      stmt.executeUpdate();
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }
  }
  
  public List<BggGameCorrelation> getGameCorrelations(BggGame game) throws Exception {
    String sql = "select game_id_a, a.name, a.bgg_id, a.thumb_url, a.avg_bgg_rating, game_id_b, b.name, b.bgg_id, b.thumb_url, b.avg_bgg_rating, correlation, common_ratings from game_correlations, games a, games b where (game_id_a = ? or game_id_b = ?) and (game_id_a = a.id and game_id_b = b.id) order by correlation desc";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {      
      stmt.setInt(1, game.getId());
      stmt.setInt(2, game.getId());
      ResultSet result = stmt.executeQuery();
      List<BggGameCorrelation> correlations = new ArrayList<BggGameCorrelation>();
      while (result.next()) {
        BggGameCorrelation correlation =
          new BggGameCorrelation(
              new BggGame(result.getInt(1), result.getString(2), result.getInt(3), result.getString(4), result.getFloat(5)), 
              new BggGame(result.getInt(6), result.getString(7), result.getInt(8), result.getString(9), result.getFloat(10)), 
            result.getFloat(11), result.getInt(12));
        if (correlation.getCorrelation() > 0f) {
          correlations.add(correlation);
        }
      }
      return correlations;
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }    
  }
  
  public BggGameCorrelationMap getAllCorrelations() throws Exception {
    String sql = "select a.id, a.name, a.bgg_id, b.id, b.name, b.bgg_id, correlation, common_ratings from games a, games b, (SELECT game_id_a, game_id_b, correlation, common_ratings FROM game_correlations g) as corrs where a.id = game_id_a and b.id = game_id_b order by a.name, correlation desc";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      ResultSet result = stmt.executeQuery();
      List<BggGameCorrelation> correlations = new ArrayList<BggGameCorrelation>();
      while (result.next()) {
        BggGame gameA = new BggGame(result.getInt(1), result.getString(2), result.getInt(3), result.getString(4), result.getFloat(5));
        BggGame gameB = new BggGame(result.getInt(6), result.getString(7), result.getInt(8), result.getString(9), result.getFloat(10));
        correlations.add(new BggGameCorrelation(gameA, gameB, result.getFloat(11), result.getInt(12)));
      }
      return new BggGameCorrelationMap(correlations);
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }
  }
    
  public BggGameCorrelation addCorrelation(BggGameCorrelation correlation) throws Exception {
    BggGameCorrelation oldCorrelation = getCorrelation(correlation.getGameA(), correlation.getGameB());
    PreparedStatement stmt = null;
    try {
      if (oldCorrelation == null) {
        String sql = "insert into game_correlations(game_id_a, game_id_b, correlation, common_ratings) values(?, ?, ?, ?)";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, correlation.getGameA().getId());
        stmt.setInt(2, correlation.getGameB().getId());
        if (!Float.isNaN(correlation.getCorrelation())) {
          stmt.setFloat(3, correlation.getCorrelation());
        }
        else {
          stmt.setNull(3, Types.FLOAT);
        }
        stmt.setInt(4, correlation.getCommonRatings());
        stmt.executeUpdate();
        return new BggGameCorrelation(correlation.getGameA(), correlation.getGameB(), correlation.getCorrelation(), correlation.getCommonRatings());
      }
      else {
        String sql = "update game_correlations set correlation = ? where game_id_a = ? and game_id_b = ?";
        stmt = conn.prepareStatement(sql);
        if (!Float.isNaN(correlation.getCorrelation())) {
          stmt.setFloat(1, correlation.getCorrelation());
        }
        else {
          stmt.setNull(1, Types.FLOAT);
        }        
        stmt.setInt(2, oldCorrelation.getGameA().getId());
        stmt.setInt(3, oldCorrelation.getGameB().getId());
        stmt.executeUpdate();
        return new BggGameCorrelation(correlation.getGameA(), correlation.getGameB(), correlation.getCorrelation(), correlation.getCommonRatings());
      }
    }
    finally {
      if (stmt != null) {
        try {stmt.close(); } catch (Exception e) { }
      }
    }    
  }
    
  public BggUserCorrelation getUserCorrelation(BggUser aUser, BggUser anotherUser) throws Exception {
    BggUserCorrelation checkCorrelation = new BggUserCorrelation(aUser, anotherUser, Float.NaN, 0, Float.NaN);
    String sql = "select correlation, common_ratings, tanimoto from user_correlations where user_id_a = ? and user_id_b = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {      
      stmt.setInt(1, checkCorrelation.getUserA().getId());
      stmt.setInt(2, checkCorrelation.getUserB().getId());
      ResultSet result = stmt.executeQuery();
      if (result.next()) { 
        return new BggUserCorrelation(checkCorrelation.getUserA(), checkCorrelation.getUserB(), result.getFloat(1), result.getInt(2), result.getInt(3));
      }
      else {
        return null;
      }
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }
  }
  
  public BggUserCorrelationMap getAllUserCorrelations(boolean orderByTanimoto) throws Exception {
    String sql = "select a.id, a.name, b.id, b.name, correlation, common_ratings, tanimoto from users a, users b, (SELECT user_id_a, user_id_b, correlation, common_ratings FROM user_correlations g) as corrs where a.id = user_id_a and b.id = user_id_b order by a.name, correlation desc";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      ResultSet result = stmt.executeQuery();
      List<BggUserCorrelation> correlations = new ArrayList<BggUserCorrelation>();
      while (result.next()) {
        BggUser userA = new BggUser(result.getInt(1), result.getString(2));
        BggUser userB = new BggUser(result.getInt(3), result.getString(4));
        BggUserCorrelation correlation = new BggUserCorrelation(userA, userB, result.getFloat(5), result.getInt(6), result.getFloat(7));
        if (correlation.getCorrelation() > 0f) {
          correlations.add(correlation);
        }
      }
      return new BggUserCorrelationMap(correlations, orderByTanimoto);
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }
  }
  
  public List<BggUser> getCorrelatedUsers() throws Exception {
    String sql = "select distinct id, name from users, user_correlations where id = user_id_a or id = user_id_b order by id";
    PreparedStatement stmt =  conn.prepareStatement(sql);
    try {
      ResultSet result = stmt.executeQuery();
      List<BggUser> users = new ArrayList<BggUser>();
      while (result.next()) {
        users.add(new BggUser(result.getInt(1), result.getString(2)));
      }
      return users;
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }
  }
  
  public List<BggUserCorrelation> getUserCorrelations(BggUser user, boolean orderByTanimoto) throws Exception {
    String orderByString = orderByTanimoto ? "order by tanimoto desc" : "order by correlation desc";
    String sql = "select user_id_a, a.name, user_id_b, b.name, correlation, common_ratings, tanimoto from user_correlations, users a, users b where (user_id_a = ? or user_id_b = ?) and (user_id_a = a.id and user_id_b = b.id) " + orderByString;;
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {      
      stmt.setInt(1, user.getId());
      stmt.setInt(2, user.getId());
      ResultSet result = stmt.executeQuery();
      List<BggUserCorrelation> correlations = new ArrayList<BggUserCorrelation>();
      while (result.next()) {
        BggUserCorrelation correlation =
          new BggUserCorrelation(new BggUser(result.getInt(1), result.getString(2)), new BggUser(result.getInt(3), result.getString(4)), 
            result.getFloat(5), result.getInt(6), result.getFloat(7));
        if (correlation.getCorrelation() > 0f) {
          correlations.add(correlation);
        }
      }
      return correlations;
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }    
  }
  
  public BggUserCorrelation addUserCorrelation(BggUserCorrelation correlation) throws Exception {
    BggUserCorrelation oldCorrelation = getUserCorrelation(correlation.getUserA(), correlation.getUserB());
    PreparedStatement stmt = null;
    try {
      if (oldCorrelation == null) {
        String sql = "insert into user_correlations(user_id_a, user_id_b, correlation, common_ratings, tanimoto) values(?, ?, ?, ?, ?)";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, correlation.getUserA().getId());
        stmt.setInt(2, correlation.getUserB().getId());
        if (!Float.isNaN(correlation.getCorrelation())) {
          stmt.setFloat(3, correlation.getCorrelation());
        }
        else {
          stmt.setNull(3, Types.FLOAT);
        }
        stmt.setInt(4, correlation.getCommonRatings());
        if (!Float.isNaN(correlation.getTanimoto())) {
          stmt.setFloat(5, correlation.getTanimoto());
        }
        else {
          stmt.setNull(5, Types.FLOAT);
        }
        stmt.executeUpdate();
        return new BggUserCorrelation(correlation.getUserA(), correlation.getUserB(), correlation.getCorrelation(), correlation.getCommonRatings(), correlation.getTanimoto());
      }
      else {
        String sql = "update user_correlations set correlation = ?, tanimoto = ? where user_id_a = ? and user_id_b = ?";
        stmt = conn.prepareStatement(sql);
        if (!Float.isNaN(correlation.getCorrelation())) {
          stmt.setFloat(1, correlation.getCorrelation());
        }
        else {
          stmt.setNull(1, Types.FLOAT);
        }        
        if (!Float.isNaN(correlation.getTanimoto())) {
          stmt.setFloat(2, correlation.getTanimoto());
        }
        else {
          stmt.setNull(2, Types.FLOAT);
        }
        stmt.setInt(3, oldCorrelation.getUserA().getId());
        stmt.setInt(4, oldCorrelation.getUserB().getId());
        stmt.executeUpdate();
        return new BggUserCorrelation(correlation.getUserA(), correlation.getUserB(), correlation.getCorrelation(), correlation.getCommonRatings(), correlation.getTanimoto());
      }
    }
    finally {
      if (stmt != null) {
        try {stmt.close(); } catch (Exception e) { }
      }
    }    
  }
  
  public List<BggUser> getMostRatedUsers(int minRatings) throws Exception {
    String sql = "select id, name, num_ratings from (select user_id, count(user_id) num_ratings from ratings group by user_id order by num_ratings desc) as ratings_counts, users where num_ratings >= ? and id = user_id;";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setInt(1, minRatings);
      ResultSet result = stmt.executeQuery();
      List<BggUser> users = new ArrayList<BggUser>();
      while (result.next()) { 
        BggUser user = new BggUser(result.getInt(1), result.getString(2));
        user.setNumRatings(result.getInt(3));
        users.add(user);
      }
      return users;
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }    
  }

  public BggUserRatingList getUserRatingList(BggUser user) throws Exception {    
    String sql = "select game_id, rating from ratings where user_id = ? order by game_id";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setInt(1, user.getId());
      ResultSet result = stmt.executeQuery();
      List<Integer> ids = new ArrayList<Integer>();
      List<Float> ratings = new ArrayList<Float>();
      while (result.next()) {
        ids.add(result.getInt(1));
        ratings.add(result.getFloat(2));
      }
      return new BggUserRatingList(user, ids, ratings);
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }    
  }

  public BggGameRecommendation getGameRecommendation(BggUser user, BggUser recommendingUser, int gameId) throws Exception {    
    String sql = "select rating, correlation, common_ratings, tanimoto from game_recommendations where user_id = ? and recommending_user_id = ? and game_id = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {      
      stmt.setInt(1, user.getId());
      stmt.setInt(2, recommendingUser.getId());
      stmt.setInt(3, gameId);
      ResultSet result = stmt.executeQuery();
      if (result.next()) { 
        return new BggGameRecommendation(user, new BggCorrelatedUser(recommendingUser, result.getFloat(2), result.getInt(3), result.getFloat(4)), gameId, result.getFloat(1));
      }
      else {
        return null;
      }
    }
    finally {
      try { stmt.close(); } catch (Exception e) {}
    }
  }

  public BggGameRecommendation addGameRecommendation(BggGameRecommendation recommendation) throws Exception {
    BggGameRecommendation oldRec = getGameRecommendation(recommendation.getUser(), recommendation.getRecommendingUser().getUser(), recommendation.getGameId());
    PreparedStatement stmt = null;
    try {
      if (oldRec == null) {
        String sql = "insert into game_recommendations(user_id, recommending_user_id, game_id, rating, correlation, common_ratings) values(?, ?, ?, ?, ?, ?)";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, recommendation.getUser().getId());
        stmt.setInt(2, recommendation.getRecommendingUser().getUser().getId());
        stmt.setInt(3, recommendation.getGameId());
        if (!Float.isNaN(recommendation.getRating())) {
          stmt.setFloat(4, recommendation.getRating());
        }
        else {
          stmt.setNull(4, Types.FLOAT);
        }
        if (!Float.isNaN(recommendation.getRecommendingUser().getCorrelation())) {
          stmt.setFloat(5, recommendation.getRecommendingUser().getCorrelation());
        }
        else {
          stmt.setNull(5, Types.FLOAT);
        }
        stmt.setInt(6, recommendation.getRecommendingUser().getCommonRatings());
        stmt.executeUpdate();
        return new BggGameRecommendation(recommendation.getUser(), recommendation.getRecommendingUser(), recommendation.getGameId(), recommendation.getRating());
      }
      else {
        String sql = "update game_recommendations set rating = ?, correlation = ?, common_ratings = ? where user_id = ? and recommending_user_id = ? and game_id = ?";
        stmt = conn.prepareStatement(sql);
        if (!Float.isNaN(recommendation.getRating())) {
          stmt.setFloat(1, recommendation.getRating());
        }
        else {
          stmt.setNull(1, Types.FLOAT);
        }
        if (!Float.isNaN(recommendation.getRecommendingUser().getCorrelation())) {
          stmt.setFloat(2, recommendation.getRecommendingUser().getCorrelation());
        }
        else {
          stmt.setNull(2, Types.FLOAT);
        }
        stmt.setInt(3, recommendation.getRecommendingUser().getCommonRatings());
        stmt.setInt(4, recommendation.getUser().getId());
        stmt.setInt(5, recommendation.getRecommendingUser().getUser().getId());
        stmt.setInt(6, recommendation.getGameId());
        stmt.executeUpdate();
        return new BggGameRecommendation(recommendation.getUser(), recommendation.getRecommendingUser(), recommendation.getGameId(), recommendation.getRating());
      }
    }
    finally {
      if (stmt != null) {
        try {stmt.close(); } catch (Exception e) { }
      }
    }    
  }
  
  public List<BggGameRecommendation> getGameRecommendations(BggUser user) throws Exception {
    String sql = "select id, name, game_id, rating, correlation, common_ratings, tanimoto from game_recommendations, users where user_id = ? and recommending_user_id = id";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.setInt(1, user.getId());
      ResultSet result = stmt.executeQuery();
      List<BggGameRecommendation> recommendations = new ArrayList<BggGameRecommendation>();
      while (result.next()) {
        BggUser recommendingUser = new BggUser(result.getInt(1), result.getString(2));
        BggCorrelatedUser correlatedUser = new BggCorrelatedUser(recommendingUser, result.getFloat(5), result.getInt(6), result.getFloat(7));
        recommendations.add(new BggGameRecommendation(user, correlatedUser, result.getInt(3), result.getFloat(4)));
      }
      return recommendations;
    }
    finally {
      if (stmt != null) {
        try { stmt.close(); } catch (Exception e) { }
      }
    }
  }
  
  public List<BggUser> getUsersWithRecommendations() throws Exception {
    String sql = "select id, name from users, game_recommendations where id = user_id";
    PreparedStatement stmt = conn.prepareStatement(sql);
    try {
      ResultSet result = stmt.executeQuery();
      List<BggUser> users = new ArrayList<BggUser>();
      while (result.next()) {
        BggUser user = new BggUser(result.getInt(1), result.getString(2));
        users.add(user);
      }
      return users;
    }
    finally {
      if (stmt != null) {
        try { stmt.close(); } catch (Exception e) { }
      }
    }
  }

  public void close() throws Exception {
    conn.close();
    closed = true;
  }  
  
  public boolean isClosed() {
    return closed;
  }
} 

<?php

class Game {
  var $id, $name, $bgg_id;
  function display_name() {
    return htmlentities($this->name, ENT_NOQUOTES, 'ISO-8859-1');
  }
  function bgg_link() {
    return "<a href='http://boardgamegeek.com/boardgame/" . $this->bgg_id . "'>" .
      $this->bgg_id . "</a>";
  }
  function correlations_link() {
    return "<a href='game_correlations.php?id=" . $this->id . "'>" .
      $this->display_name() . "</a>";
  }
}

class GameCorrelation {
  var $game, $correlation, $common_ratings;
}

class MysqlRep {
  var $connection;

  function MysqlRep() {
    $this->connection = mysql_connect('localhost', 'bgg_reader', 'bgg_reader');
    mysql_select_db("bgg", $this->connection);
  }

  function close() {
    mysql_close($this->connection);
  }

  function get_all_games() {
    $games = array();
    $result = mysql_query("select id, name, bgg_id, game_id from games, " .
       "(select game_id_a game_id from game_correlations union " .
       " select game_id_b game_id from game_correlations) as all_ids " .
       " where id = game_id order by name", $this->connection);
    if (!$result) {
      die('Query error: ' . mysql_error());
    }
    while ($row = mysql_fetch_array($result)) {
      $game = new Game();
      $game->id = $row['id'];
      $game->name = $row['name'];
      $game->bgg_id = $row['bgg_id'];
      $games[] = $game;
    }
    return $games;
  }

  function get_game($game_id) {
    $game_id = (int)$game_id;
    $result = mysql_query("select id, name, bgg_id from games where id = " . $game_id, $this->connection);
    if (!$result) {
      die('Query error: ' . mysql_error());
    }
    if ($row = mysql_fetch_array($result)) {
      $game = new Game();
      $game->id = $row['id'];
      $game->name = $row['name'];
      $game->bgg_id = $row['bgg_id'];
      return $game;
    }
    return null;
  }

  function get_correlations($game_id) {
    $game_id = (int)$game_id;
    $correlations = array();
    $result = mysql_query("select id, name, bgg_id, game_id, correlation, common_ratings from games, " .
      "(select game_id_a game_id, correlation, common_ratings from game_correlations " .
      "  where game_id_b = " . $game_id . " union " .
      " select game_id_b game_id, correlation, common_ratings from game_correlations " .
      "  where game_id_a = " . $game_id . ") as correlations " .
                          "where id = game_id order by correlation desc", $this->connection);
    if (!$result) {
      die('Query error: ' . mysql_error());
    }
    while ($row = mysql_fetch_array($result)) {
      $game = new Game();
      $game->id = $row['id'];
      $game->name = $row['name'];
      $game->bgg_id = $row['bgg_id'];
      $correlation = new GameCorrelation();
      $correlation->game = $game;
      $correlation->correlation = $row['correlation'];
      $correlation->common_ratings = $row['common_ratings'];
      $correlations[] = $correlation;
    }
    return $correlations;
  }
}

global $mysql_rep;
$mysql_rep = new MysqlRep();

?>
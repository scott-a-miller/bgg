<?php
require_once('models.inc');

$game_id = $_REQUEST['id'];
if ($game_id == null) {
  die("No game id was specified");
}

$game = $mysql_rep->get_game($game_id);
if ($game == null) {
  die("No known game for id: " . $game_id);
}

$correlations = $mysql_rep->get_correlations($game->id);
if ($correlations == null || count($correlations) == 0) {
  die("No correlations for: " . $game->display_name());
}
?>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><?= $game->display_name()?> BGG Correlated Games</title>
<body>
<p><a href='index.php'>Back to full list</a></p>
<h3><?= $game->display_name() ?> : correlated games from boardgamegeek.com</h3>
<table>
  <tr>
    <th>Name</th>
    <th>BGG Id</th>
    <th>Correlation</th>
    <th>Common Ratings</th>
  </tr>
<?php foreach ($correlations as $correlation) { ?>
  <tr>
    <td><?= $correlation->game->correlations_link() ?></td>
    <td><?= $correlation->game->bgg_link() ?></td>
    <td><?= $correlation->correlation ?></td>
    <td><?= $correlation->common_ratings ?></td>
  </tr>
<?php } ?>
</table>
<p><a href="about.html">About</a></p>
</body>
</html>

<?php
require_once('models.inc');

$games = $mysql_rep->get_all_games();
?>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<body>
<p><a href="about.html">About</a></p>
<h3>Correlated Games from boardgamegeek.com (updated July 2010)</h3>
<table>
  <tr>
    <th>Name</th>
    <th>BGG Id</th>
  </tr>
<?php foreach ($games as $game) { ?>
  <tr>
    <td><?= $game->correlations_link() ?></a></td>
    <td><?= $game->bgg_link() ?></td>
  </tr>
<?php } ?>
</table>
<p><a href="about.html">About</a></p>
</body>
</html>

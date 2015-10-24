$ views/header() $

<div class="section_header">
  Games correlated with <a href="$game.bggUrl$">$game.htmlName$</a> (bgg rating: $game.formattedAvg$)
</div>
$if(game.thumbUrl)$
<div class="game_thumb">
  <a href="$game.bggUrl$"><img src="$game.thumbUrl$"/></a>
</div>
$endif$

<table class="data_table">
  <tr>
    <th>Other Game</th>
    <th class="numeric_column" title="The Pearson product-moment correlation coefficient - a value between 0 and 1.">Correlation</th>
    <th class="numeric_column" title="The number of ratings in common.">Ratings</th>
    <th class="numeric_column" title="The bayesian avg rating for all users on the geek." style="text-align:right">Bgg Rating</th>
  </th>
$games:{correlated_game|
  <tr>
    <td><a href="$app_root$/game_correlations/$correlated_game.game.id$">$correlated_game.game.htmlName$</a></td>
    <td class="numeric_column">$correlated_game.formattedCorrelation$</td>
    <td class="numeric_column">$correlated_game.commonRatings$</td>
    <td style="text-align:right"><a href="$correlated_game.game.bggUrl$">$correlated_game.game.formattedAvg$</a></td>
  </tr>
}$
</table>

$ views/footer() $
$ views/header() $

<div class="section_header"
$if (orderByTanimoto)$
     title="Similarity is based on tanimoto similarity of favorite games">
  Recommendations for <a href="$user.bggUrl$">$user.htmlName$</a> 
  (<a href="$app_root$/user_correlations/$user.name$/tanimoto">see all correlated users</a>)
  (<a href="$app_root$/recommended_games/$user.name$" 
      title="Switches to use the Pearson correlation coefficient">switch to pearson correlation coefficient</a>)
$else$
     title="Correlation is based on the Pearson correlation coefficient">
  Recommendations for <a href="$user.bggUrl$">$user.htmlName$</a> based on correlation 
  (<a href="$app_root$/user_correlations/$user.name$">see all correlated users</a>)
  (<a href="$app_root$/recommended_games/$user.name$/tanimoto" 
        title="Switches to use tanimoto similarity based on favorite games">switch to tanimoto similarity</a>)
$endif$
</div>

<table class="data_table">
  <tr>
    <th colspan="2">Game</th>
    <th class="numeric_column">
    $if (!sorted_by_avg_rating)$ 
      <a href="$app_root$/recommended_games/$user.name$/$similarity$/avgRating">Avg. Rating</a> 
    $else$
      Avg. Rating
    $endif$
    </th>
    <th class="numeric_column">
    $if (!sorted_by_weighted_rating)$
      <a href="$app_root$/recommended_games/$user.name$/$similarity$">Weighted Rating</a>
    $else$
      Weighted Rating
    $endif$
    </th>
    <th class="numeric_column">
    $if (!sorted_by_num_ratings)$
      <a href="$app_root$/recommended_games/$user.name$/$similarity$/numRatings"># Ratings</a>
    $else$
      # Ratings
    $endif$
    </th>
    <th></th>
  </th>
$recommendations:{rec_game|
  <tr>
    <td colspan="2"><a href="$rec_game.game.bggUrl$">$rec_game.game.htmlName$</a></td>
    <td class="numeric_column">$rec_game.formattedAvgRating$</td>
    <td class="numeric_column">$rec_game.formattedWeightedRating$</td>
    <td class="numeric_column">$rec_game.numRatings$ 
      <a id="open_$rec_game.game.id$" href="#" onclick="return false;" 
         title="Click to see related ratings from correlated users.">+</a></td>
      <script>
        \$('#open_$rec_game.game.id$').bind('click', function() {
          show_children(this, '.correlated_user_rating_game-$rec_game.game.id$', '#open_$rec_game.game.id$');
        });
      </script> 
    <td></td>
  </tr>
  <tr class="correlated_user_rating_game-$rec_game.game.id$" style="display:none">
    <td class="recommended_thumb" rowspan="$rec_game.recommendationsPlusTwo$">
      <a href="$rec_game.game.bggUrl$"><img src="$rec_game.game.thumbUrl$"/></a>
    </td>
    <td style="text-align:right">Correlated User</td>
    <td class="numeric_column">Rating</td>
$if (orderByTanimoto)$
    <td class="numeric_column">Similarity</td>
$else$
    <td class="numeric_column">Correlation</td>
$endif$
    <td class="numeric_column"># Ratings</td>
  </tr>
  $rec_game.recommendations:{rec|  
  <tr class="correlated_user_rating_game-$rec_game.game.id$" style="display:none">
    <td style="text-align:right">
      <a href="$rec.recommendingUser.user.bggUrl$">$rec.recommendingUser.user.htmlName$</a>
    </td>
    <td class="numeric_column">$rec.rating$</td>
$if (orderByTanimoto)$    
    <td class="numeric_column">$rec.recommendingUser.formattedTanimoto$</td>
$else$
    <td class="numeric_column">$rec.recommendingUser.formattedCorrelation$</td>
$endif$ 
    <td class="numeric_column">$rec.recommendingUser.commonRatings$
  </tr>    
  }$  
  <tr class="correlated_user_rating_game-$rec_game.game.id$" style="display:none">
    <td colspan="4"><div style="height:16px"/></td>
  </tr>
}$
</table>
$ views/footer() $
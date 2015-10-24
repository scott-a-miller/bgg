$ views/header() $

<div class="section_header"
$if (orderByTanimoto)$
     title="Similarity is based on tanimoto similarity of favorite games">
  Users similar to (by tanimoto) <a href="$user.bggUrl$">$user.htmlName$</a>
    (<a href="$app_root$/recommended_games/$user.name$/tanimoto">see game recommendations</a>)
    (<a href="$app_root$/user_correlations/$user.name$" 
        title="Switches to use the Pearson correlation coefficient">switch to pearson correlation coefficient</a>)
$else$
     title="Correlation is based on the Pearson correlation coefficient">
  Users correlated with <a href="$user.bggUrl$">$user.htmlName$</a>
    (<a href="$app_root$/recommended_games/$user.name$">see game recommendations</a>)
    (<a href="$app_root$/user_correlations/$user.name$/tanimoto" 
        title="Switches to use tanimoto similarity based on favorite games">switch to tanimoto similarity</a>)
$endif$
</div>

<table class="data_table">
  <tr>
    <th>Other User</th>
    <th class="numeric_column" title="The Pearson product-moment correlation coefficient - a value between 0 and 1.">Correlation</th>
    <th class="numeric_column" title="The number of ratings in common.">Ratings</th>
    <th class="numeric_column" title="The tanimoto similarity for superlative games">Tanimoto Similarity</th>    
    <th>Boardgamegeek.com</th>
  </tr>
$users:{correlated_user|
  <tr>
    <td><a href="$app_root$/user_correlations/$correlated_user.user.name$">$correlated_user.user.htmlName$</td>
    <td class="numeric_column">$correlated_user.formattedCorrelation$</td>
    <td class="numeric_column">$correlated_user.commonRatings$</td>
    <td class="numeric_column">$correlated_user.formattedTanimoto$</td>
    <td style="text-align:right"><a href="$correlated_user.user.bggUrl$">Profile</a></td>
  </tr>
}$
</table>

$ views/footer() $
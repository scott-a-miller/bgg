$ views/header() $

<div class="section_header">Geeks</div>

<form method="post" action="$app_root$/user_correlations_list">
<div id="search">
  Geek Username: <input type="text" name="query" size="30" maxlength="128" value="$query$"> <input type="submit" value="Search" name="Search"/>
</div>
</form>

$if(!users)$
$if(has_query)$
<div class="info_paragraph">
Sorry, there are no correlated users with that user name.
</div>

<div class="info_paragraph">
The only users that have been correlated,
are those that have rated at least 100 games - one of which must be in the top 100 of boardgamegeek.
</div>

<div class="info_paragraph">
<a href="$app_root$/user_correlations_list/all">Show All Correlated Users</a>
</div> 
$endif$
$endif$

<table class="data_table">
$users:{user|
  <tr>
    <td><a href="$app_root$/recommended_games/$user.name$">$user.htmlName$</a></td>
    <td style="width:200px; text-align:right"><a href="$user.bggUrl$">Profile</a></td>
  </tr>    
}$
</table>

$ views/footer() $
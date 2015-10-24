$ views/header() $

<div class="section_header">Games</div>
<table class="data_table">
$games:{game|
  <tr>
    <td><a href="$app_root$/game_correlations/$game.id$">$game.htmlName$</a></td>
  </tr>
}$
</table>

$ views/footer() $
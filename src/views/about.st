$ views/header() $

<div class="text_section">

<div class="paragraph_header">About the correlations</div>
<p>All of the data was pulled from boardgamegeek.com in early January 2011 using their
<a href="http://www.boardgamegeek.com/xmlapi">XML API</a> web services. Only users
with at least 100 ratings were considered for correlation. For each pair of users
we find each game that was rated by both players, that pair of values becomes a data
point in the correlation between the two users. The correlation values
are calculated as the Pearson product-moment correlation coefficient 
(<a href="http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient">Wikipedia</a>).
The images on the Wikipedia page give a great visual description of what this is all about.
A high correlation indicates that if one user gave a high rating to a game (relative to their
other ratings), then it is likely that the other user also gave the game a high rating (again - relative
to their other ratings). This same relationship must also hold for their lower rated games as
well (and in a linear way). 
</p>

<div class="paragraph_header">About the recommendations</div>
<p>To determine some recommended games, we find your most correlated users (up to 40) that
have at least 25 ratings in common with you and a correlation value of 0.5 or greater. We go through those
correlated users ratings and find games that you have not rated. We calculate an average
rating (mean) and a weighted average (mean of the rating * the correlation value). We sort
by the weighted average, but only games with at least 3 ratings get included in the
top. The others are put at the bottom (still sorted by weighted average). As you
look through the suggested games, remember that these metrics are kind of made up (not
arbitrary, but just a guess as to what will be useful). Browse through the games, and take
a look the individual ratings underneath. It's all transparent; you can take a look at the profiles
on boardgamegeek.com and decide which users ratings you prefer to trust.
</p>

<div class="paragraph_header">Problem with correlation based recommendations</div>
<p>One of the problems with the Pearson coefficient is that it's difficult to compare
values when the coefficients are based on a lower number of ratings. Hence, the minimum threshold
of 25 ratings. This isn't quite the statistical significance that would be desired, but throwing
out more of the highly correlated users would have resulted in too few recommendations. I've read
about a few approaches for weighting the correlation based on the number of common ratings, but
none of them based in solid theory. Be sure to take recommendations with a grain of salt for correlated users that
have a few number of ratings and moderate correlation.
</p>

<div class="paragraph_header">About the tanimoto similarity</div>
<p>As an alternative approach for finding similar users, I recently added the tanimoto similarity coefficient. In this
approach, I find all of the games that a user deems to be "superlative", and use the tanimoto coefficient to
determine how similar this list is to other users' superlative lists. In the current implementation, superlative
is defined as a rating of 8 or more. I've considered other ways of defining these (e.g. top quartile, over two standard
deviations above the mean, etc.) - and might try them in the future. You can read a full definition of the metric 
at: <a href="http://en.wikipedia.org/wiki/Tanimoto_coefficient">Tanimoto Coefficient
on Wikipedia</a>.
</p>  

<div class="paragraph_header">Thanks</div>
<p>Much thanks to boardgamegeek.com for their web services api that made this task
a lot easier.
</p>

<div class="paragraph_header">Cheers</div>
<p>
Scott Miller<br/>
Blog: <a href="http://blog.brokenmodel.com">http://blog.brokenmodel.com</a><br/>
On the geek: <a href="http://boardgamegeek.com/user/CitizenBob">CitizenBob</a><br/>
</p>



</div>

$ views/footer() $
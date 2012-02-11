<div class="artist">
  <div class="artist_title">
	<button class="expand_button">a</button> 
	<h3><a href='http://example.com/${mbid}'>${name}</a></h3>
  </div>
  <div class="albums">
	  <ul>
	  {{each albums}}
		<div class='album'> 
			<li> <a href='http://example.com/${mbid}'>${name}</a> </li>
		</div>
	  {{/each}}
	  </ul>
  </div>
</div>

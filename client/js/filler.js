artists = {"name":"Rammstein","mbid":"b2d122f9-eadb-4930-a196-8f221eeb0c66","albums":[{"name":"The Very Best of Rammstein","mbid":"7bea3d88-533d-4684-ad3a-8eee7b5079e8","tracks":[]},{"name":"Rammstein","mbid":"4ed9a62a-2bf5-4744-ba1b-9a23f27e06c7","tracks":[]}]}
$.template('artistfiller', '<div class="head"> <button class="expand_button">a</button> <h3><a href=http://example.com/${mbid}>${name}</a> </h3></div>');
$.tmpl('artistfiller', artists).appendTo('#artists');


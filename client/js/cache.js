	var objectsCache = [];
	var loadedTabs = [];
	
	function clearCache() {
		objectsCache = [];
		loadedTabs = [];
	}
	
	function checkLoadedTabs(type) {
		var res = $.inArray(type, loadedTabs);
		return (res > 0);
	}
	
	function checkLoadedObject(mbid) {
		return ($.inArray(mbid, objectsCache) > 0);
	}
	
	function getObject(mbid) {
		return objectsCache[mbid];
	}
	
	function cacheArtists(artists) {
		loadedTabs.push(1);
		for (i = 0; i < artists.length; ++i) {
			objectsCache[artists[i].mbid] = {
				type : "artist",
				loaded : false,
				name : artists[i].name,
				albums_mbids : []
			};
		}
	}
	
	function cacheAlbums(albums) {
		loadedTabs.push(2);
		for (i = 0; i < albums.length; ++i) {
			objectsCache[albums[i].mbid] = {
				type : "album",
				loaded : false,
				name : albums[i].name,
				tracks_mbids : []
			};
		}
	}
	
	function cacheTracks(tracks) {
		loadedTabs.push(3);
		for (i = 0; i < tracks.length; ++i) {
			objectsCache[tracks[i].mbid] = {
				type : "track",
				loaded : false,
				url: tracks[i].url,
				name : tracks[i].name
			};
		}
	}
	
	function cacheSearchResult(result) {
		clearCache();
		cacheAtrists(result.artists);
		cacheAlbums(result.albums);
		cacheTracks(result.tracks);
	}
	

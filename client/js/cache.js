	var objectsCache = [];
	var loadedTabs = [];
	
	function clearCache() {
		objectsCache = [];
		loadedTabs = [];
	}
	
	function checkTabStatus(type) {
		if (loadedTabs[type] == null) return "empty";
		return loadedTabs[type].status;
	}
	
	function checkObjectStatus(mbid) {
		return objectsCache[mbid].status;
	}
	
	function getObject(mbid) {
		return objectsCache[mbid];
	}
	
	function setLoadingTabs (type) {
		if (loadedTabs[type] == null) loadedTabs[type]  = {status: "loading"};
	}
	
	function setLoadingObject (mbid) {
		objectsCache[mbid].status = "loading";
	}
	
	function updateArtist (loadedArtist) {
		objectsCache[loadedArtist.mbid].status = "loaded";
		objectsCache[loadedArtist.mbid].albums_mbids = loadedArtist.albums;
	}
	
	function updateAlbum (loadedAlbum) {
		objectsCache[loadedAlbum.mbid].status = "loaded";
		objectsCache[loadedAlbum.mbid].tracks_mbids = loadedAlbum.tracks;
	}
	
	function updateTrack (loadedTrack) {
		objectsCache[loadedTrack.mbid].status = "loaded";
		objectsCache[loadedTrack.mbid].url = loadedTrack.url;
	}
	
	function cacheArtists(artists) {
		loadedTabs["1"] = {status: "loaded"};
		for (i = 0; i < artists.length; ++i) {
			objectsCache[artists[i].mbid] = {
				type : "artist",
				status : "empty",
				name : artists[i].name,
				albums_mbids : []
			};
		}
	}
	
	function cacheAlbums(albums) {
		loadedTabs["2"] = {status: "loaded"};
		for (i = 0; i < albums.length; ++i) {
			objectsCache[albums[i].mbid] = {
				type : "album",
				status : "empty",
				name : albums[i].name,
				tracks_mbids : [], 
				artist_name : albums[i].artistName,
				artist_mbid : albums[i].artistMbid
			};
		}
	}
	
	function cacheTracks(tracks) {
		loadedTabs["3"] = {status: "loaded"};
		for (i = 0; i < tracks.length; ++i) {
			objectsCache[tracks[i].mbid] = {
				type : "track",
				status : "empty",
				url: tracks[i].url,
				name : tracks[i].name,
				artist_name : tracks[i].artistName,
				artist_mbid : tracks[i].artistMbid,
				album_name : tracks[i].albumName,
				album_mbid : tracks[i].albumMbid
			};
		}
	}
	
	function cacheSearchResult(result) {
		clearCache();
		cacheAtrists(result.artists);
		cacheAlbums(result.albums);
		cacheTracks(result.tracks);
	}
	
	

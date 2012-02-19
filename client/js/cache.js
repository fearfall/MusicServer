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
	
	function setLoadingObject (mbid, recursive) {
		if (recursive) {
			objectsCache[mbid].status = "recursive_loading";
		} else {
			objectsCache[mbid].status = "loading";
		}
		
	}
	
	function updateArtist (loadedArtist) {
		objectsCache[loadedArtist.mbid].status = "loaded";
		objectsCache[loadedArtist.mbid].albums_mbids = loadedArtist.albums;
		for (i = 0; i < loadedArtist.albums.length; ++i) {
			if (objectsCache[loadedArtist.albums[i].mbid] == null) {
				loadedArtist.albums[i].artistName = loadedArtist.name;
				loadedArtist.albums[i].artistMbid = loadedArtist.mbid;
				tryCacheAlbum(loadedArtist.albums[i]);
			}
		}
	}
	
	function updateAlbum (loadedAlbum) {
		objectsCache[loadedAlbum.mbid].status = "loaded";
		objectsCache[loadedAlbum.mbid].tracks_mbids = loadedAlbum.tracks;
		for (i = 0; i < loadedAlbum.tracks.length; ++i) {
			if (objectsCache[loadedAlbum.tracks[i].mbid] == null) {
				loadedAlbum.tracks[i].albumName = loadedAlbum.name;
				loadedAlbum.tracks[i].albumMbid = loadedAlbum.mbid;
				loadedAlbum.tracks[i].artistName = loadedAlbum.artistName;
				loadedAlbum.tracks[i].artistMbid = loadedAlbum.artistMbid;
				tryCacheTrack(loadedAlbum.tracks[i]);
			}
		}
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
	
	function tryCacheAlbum(album) {
		if (objectsCache[album.mbid] == null) {
			objectsCache[album.mbid] = {
				type : "album",
				status : "empty",
				name : album.name,
				tracks_mbids : [], 
				artist_name : album.artistName,
				artist_mbid : album.artistMbid
			};
		}
	}
	
	function cacheAlbums(albums) {
		loadedTabs["2"] = {status: "loaded"};
		for (i = 0; i < albums.length; ++i) {
			tryCacheAlbum(albums[i]);
		}
	}
	
	function tryCacheTrack(track) {
		if (objectsCache[track.mbid] == null) {
			objectsCache[track.mbid] = {
				type : "track",
				status : "empty",
				url: track.url,
				name : track.name,
				artist_name : track.artistName,
				artist_mbid : track.artistMbid,
				album_name : track.albumName,
				album_mbid : track.albumMbid
			};
		}
	}
	
	function cacheTracks(tracks) {
		loadedTabs["3"] = {status: "loaded"};
		for (i = 0; i < tracks.length; ++i) {
			tryCacheTrack(tracks[i]);
		}
	}
	
	function cacheSearchResult(result) {
		clearCache();
		cacheAtrists(result.artists);
		cacheAlbums(result.albums);
		cacheTracks(result.tracks);
	}
	
	

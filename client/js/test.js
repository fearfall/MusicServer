$(document).ready(function(){
	var loadedItems = [];
	var loadedObjects = [];
	var loadedElements = [];
	var ip = "192.168.211.228";
	var searchUrl    = "http://"+ ip +":6006/search/?format=json&jsoncallback=?";
	var getArtistUrl = "http://"+ ip +":6006/get/artist?format=json&jsoncallback=?";
	var getAlbumUrl  = "http://"+ ip +":6006/get/album?format=json&jsoncallback=?";
	
	function addArtists (newArtists, blockId, needEmpty, needWrap) {
		var block = "#"+blockId;
		if (needEmpty) {
			$(block).empty();
		}
		var ulOpenTag = "";
		var ulCloseTag = "";
		for (i = 0; i < newArtists.length; ++i) {
			if (needWrap && i == 0) {
				ulOpenTag = "<ul>"; 
				ulCloseTag = "";
			} else if (needWrap && i == newArtists.length -1) {
				ulOpenTag = ""; 
				ulCloseTag = "</ul>";
			} else {
				ulOpenTag = "";
				ulCloseTag = "";
			}
			var elementId = blockId+"_"+newArtists[i].mbid;
			var branches = $(ulOpenTag +
				"<li><span class='folder' id='"+elementId
				+"' name='"+newArtists[i].mbid +"'>"
				+newArtists[i].name+"</span></li>"
				+ulCloseTag).appendTo(block);
			$("#"+elementId).click(function(){
				var itemId = $(this).attr('name');
				var element = $(this).attr('id');
				if ($.inArray(itemId, loadedItems) < 0) {
					//$.getJSON(getArtistUrl, {'id' : itemId}, function(json) {
					$.getJSON("caps/albums", function(json) {
						addAlbums(json.albums, element, false, true);
						loadedElements.push(element);
						loadedItems.push(itemId);
						loadedObjects.push(json);
					});
				}
			});
		}
		//$(block).treeview();
	} 
	
	function addAlbums (newAlbums, blockId, needEmpty, needWrap) {
		var block = "#"+blockId;
		if (needEmpty) {
			$(block).empty();
		}
		var ulOpenTag = "";
		var ulCloseTag = "";
		for (i = 0; i < newAlbums.length; ++i) {
			if (needWrap && i == 0) {
				ulOpenTag = "<ul>"; 
				ulCloseTag = "";
			} else if (needWrap && i == newAlbums.length -1) {
				ulOpenTag = ""; 
				ulCloseTag = "</ul>";
			} else {
				ulOpenTag = "";
				ulCloseTag = "";
			}
			var elementId = blockId+"_"+newAlbums[i].mbid;
			var branches = $(
					ulOpenTag +
					"<li><span class='folder' id='"+elementId+"' name='"+newAlbums[i].mbid+"'>"
					+newAlbums[i].name	
					+"</span></li>"+ulCloseTag
				).appendTo(block);
			$("#"+elementId).click(function(){
				var element = $(this).attr('id');
				var itemId = $(this).attr('name');
				if ($.inArray(itemId, loadedItems) < 0) {
					//$.getJSON(getAlbumUrl, {'id' : itemId}, function(json) {
					$.getJSON("caps/tracks", function(json) {
						loadedItems.push(itemId);
						loadedObjects.push(json);
						loadedElements.push(element);
						addTracks(json.tracks, element, false, true);
					});
				} else if ($.inArray(element, loadedElements) < 0){
					var tracks;
					for (j=0; j < loadedObjects.length; ++j) {
						if (loadedObjects[j].mbid == itemId) {
							tracks = loadedObjects[j].tracks;
						}
					}
					if (tracks != null) {
						loadedElements.push(element);
						addTracks(tracks, element, false, true);
					}
				}
				
			});
		}
		//$(block).treeview();
	}
	
	function addTracks (newTracks, blockId, needEmpty, needWrap) {
		var block = "#"+blockId;
		if (needEmpty) {
			$(block).empty();
		}
		var ulOpenTag = "";
		var ulCloseTag = "";
		for (i = 0; i < newTracks.length; ++i) {
			if (needWrap && i == 0) {
				ulOpenTag = "<ul>"; 
				ulCloseTag = "";
			} else if (needWrap && i == newTracks.length -1) {
				ulOpenTag = ""; 
				ulCloseTag = "</ul>";
			} else {
				ulOpenTag = "";
				ulCloseTag = "";
			}
			var elementId = blockId +"_"+newTracks[i].mbid;
			var branches = $(
				ulOpenTag + "<li>"
				+"<div class='clear'></div>"
				+"<div href="+newTracks[i].url+" style='width: 400px;' class='item file'>"// id='"+newTracks[i].url+"'><div>"
					+"<div>"
						+"<div class='fr duration'>02:06</div>"
						+"<div class='btn play'></div>"
						+"<div class='title'><b>"+newTracks[i].name+"</b></div>"
					+"</div>" 
					+"<div class='player inactive'></div>"
				+"</div>"
				+ulCloseTag).appendTo(block);
			$("#"+elementId).click(function(){
				var itemUrl = $(this).attr('href');
				var itemName = $(this).text();
				//addPlayListItem({name: itemName, mp3: itemUrl}, myPlayList, true);
				
			});
		}
		/*$(block).treeview();
		if ($("#searchresult").hasClass("playlist1")) {
			$("#searchresult").removeClass("playlist1");
		}*/
	}
	
	function clear() {
		//$("#jplayer_playlist ul").empty();
		loadedItems = [];
		loadedElements = [];
		loadedObjects = [];
	}
	
	function addItems(newItems, block, needEmpty) {
		if (needEmpty) {
			$(block).empty();
		}
		for (i = 0; i < newItems.length; ++i) {
			var branches = $("<li><span class='folder' id='" 
				+newItems[i].mbid
				+"'>"
				+newItems[i].name	
				+"</span></li>").appendTo(block);
				$("#"+newItems[i].mbid).click(function(){
					var itemId = $(this).attr('id');
					alert(itemId);
				});
		}
		$(block).treeview();
	}	
	
    $("#search").click(function(){
		clear();
		var query = $("#query").val();
		//$.getJSON(searchUrl, {'pattern' : query}, function(json) {
		$.getJSON("caps/search", function(json) {
			addArtists(json.artists, "artists", true, false);
			addAlbums (json.albums,  "albums",  true, false);
			addTracks (json.tracks, "tracks", true, false);
		}).error(function(x, e) {
			alert("x = " + x + " e = " + e); 
			});
	});
	

	//for authorization
	 $(".signin").click(function(e) {
		e.preventDefault();
		$("fieldset#signin_menu").toggle();
		$(".signin").toggleClass("menu-open");
     });
 
     $("fieldset#signin_menu").mouseup(function() {
		return false;
     });
     
     $(document).mouseup(function(e) {
		if($(e.target).parent("a.signin").length==0) {
			$(".signin").removeClass("menu-open");
			$("fieldset#signin_menu").hide();
		}
     });       
            
	//end for authorization
});



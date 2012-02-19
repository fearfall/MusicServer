	var ip = "192.168.211.73";
    var searchUrl    = "http://"+ ip +":6006/search/?format=json&jsoncallback=?";
    var getArtistUrl = "http://"+ ip +":6006/get/artist?format=json&jsoncallback=?";
	var getAlbumUrl  = "http://"+ ip +":6006/get/album?format=json&jsoncallback=?";

    function adjustPlaylist() {
        $(".playlist").scrollable({
		items:'.playlist',
		vertical:true,
		mousewheel: true
	});
    }
    
    var currentTab = 1;
    function initTabs() {
		$('.results').replaceWith("\
			<div class='results'>\
				 <div id='tabs'>\
					<ul> \
						<li><a href='#artists'>Artists</a></li> \
						<li><a href='#albums'>Albums</a></li>\
						<li><a href='#tracks'>Tracks</a></li>\
					</ul>\
					<div id='artists' class='content_item'/>\
					<div id='albums' class='content_item'/>\
					<div id='tracks' class='content_item'/> \
				</div>\
			</div>");
        var tabs = $( "#tabs" ).tabs();
        tabs.bind( "tabsselect", function(event, ui) {
			var selected = $(ui.index);
			if(selected.length === 0)
				selected = 1;
			else {
				selected = selected[0] + 1;
				}
			currentTab = selected;
			if (checkTabStatus(selected)=="empty") {
				var pattern = $('#search_field').val();
				makeTypifiedSearch(selected, pattern);
			}
	    });
	}
    
    function clearTabs() {
		$("#artists").empty();
		$("#albums").empty();
		$("#tracks").empty();
	}
	
	function makeTypifiedSearch(type, pattern) {
		switch (type) {
			case 1:
				setLoadingTabs(1);
				$.getJSON(searchUrl, {'type': 1, 'pattern' : pattern}, function(json) {
					if(json[0] !== "Error") {
						cacheArtists(json.artists.models);
						applyTemplate("#artist_expanded_template", json.artists, "#artists");
						addHiding(".artist .title .expand_button");
						addButtons();
						playButtons();
					} else {
						$("#artists").append(json[0]);
					}
				});
				break
			case 2:
				setLoadingTabs(2);
				$.getJSON(searchUrl, {'type': 2, 'pattern' : pattern}, function(json) {
					if(json[0] !== "Error") {
						cacheAlbums(json.albums.models);
						applyTemplate("#album_expanded_template", json.albums, "#albums");
						addHiding(".album .title .expand_button");
						addButtons();
						playButtons();
					}
					else {
						$("#albums").append(json[0]);
					}
				});
				break
			case 3:
				setLoadingTabs(3);
				$.getJSON(searchUrl, {'type': 3, 'pattern' : pattern}, function(json) {
					if(json) {
						cacheTracks(json.tracks.models);
						applyTemplate("#tracks_template", json, "#tracks");
						addButtons();
						playButtons();
					}
				});
				break
			default:
				
		}
	}
    
    function viewSearchResults() {
		var search_field = $('#search_field');
		var pattern = search_field.val();
		clearCache();
        clearTabs();
        makeTypifiedSearch(currentTab, pattern);
	}
    
	function applyTemplate(templateName, data, element) {
	  if(data.length === 0)
		$(element).append("<div>No results</div>");
	  $(templateName).tmpl(data).appendTo(element);
    }
    
	function addButtons(){
        $(".add_button").button();
        $(".add_button").click(function() {                           
                            addToPlaylist("#clips", $(this), false);
                        });
    }  
    
    function playButtons(){
        $(".play_button").button();
        $(".play_button").click(function() {                           
                            addToPlaylist("#clips", $(this), true);
                        });
    } 
    
	function loadObject(mbid, recursive, needAppendToElement) {
		if (checkObjectStatus(mbid) == "empty") {
			setLoadingObject(mbid, recursive);
			switch (getObject(mbid).type) {
				case "artist": 
					var objectsList = $("#"+mbid+" .albums ul");
					$.getJSON(getArtistUrl, {'id' : mbid}, function(json) {
						updateArtist(json);
						if (needAppendToElement) {
							$("#album_template").tmpl(json).appendTo(objectsList);
						}
						if (recursive) {
							for (i = 0; i < json.albums.length; ++i) {
								loadObject(json.albums[i].mbid, recursive, false);
							}
						}
					});
					break
				case "album": 
					var objectsList = $("#"+mbid+" .tracks ul"); 
					$.getJSON(getAlbumUrl, {'id' : mbid}, function(json) {
						updateAlbum(json);
						if (needAppendToElement) {
							$("#album_tracks_template").tmpl(json).appendTo(objectsList);
						}
						if (recursive) {
							for (i = 0; i < json.tracks.length; ++i) {
								addTrackToPlaylist(json.tracks[i].mbid);
							}
						}
					});
					break
				default:
			}
		}
	}
	
	function addHiding(element) {
		$(element).click(function() {
				$(this).parent().parent().next().toggle();
				var mbid = $(this).parent().parent().parent().attr("id");
				loadObject(mbid, false, true);
				return false;
			}).parent().parent().next().hide();
	}
	
	function pushTrackToPlaylist(track) {
		var player = $f("footer_player");
		var template = "<a href=${url}> <span> ${name}</span> </a>";
		if(!player.isLoaded()) {
			player.load(function() {
				$("#clips").append(template);
				player.setPlaylist([track,]);
				player.playlist("#clips", {loop:true});
			});
		} else {				
			var position =  player.getPlaylist().length; 	
			player.addClip(track);	
		}
	}
	
	function addTrackToPlaylist(mbid) {
		var status = checkObjectStatus(mbid);
		switch (status) {
			case "empty": 
				var getTrackUrl  = "http://"+ ip +":6006/get/track?format=json&jsoncallback=?";
				$.getJSON(getTrackUrl, {'id' : mbid}, pushTrackToPlaylist);
				break
			case "loaded":
				var track = getObject(mbid);
				pushTrackToPlaylist(track);
				break
			default:
			
		}
	}
	
	function addComplexToPlaylist(mbid) {
		var status = checkObjectStatus(mbid);
		switch (status) {
			case "empty": 
				loadObject(mbid, true, true);
				break
			case "loading":
				while (status != "loaded" && status != "error") {
					status = checkObjectStatus(mbid);
				}
				addComplexToPlaylist(mbid);
				break
			case "loaded":
				loadObject(mbid, true, false);
				break
			default:
			
		}
	}
	
	function tryGetMbid(button) {
		var id = undefined;
		var attempt = 0;
		var elem = button.parent();
		while ( attempt <= 4 && id == undefined) {
			id = elem.parent().attr('id');
			++attempt;
			elem = elem.parent();
		} 
		return id;
	}
	
    function addToPlaylist(playlist_element, button, shouldPlay) {
		var id = tryGetMbid(button);
		var player = $f("footer_player");
		var object = getObject(id);
		switch (object.type) {
			case "artist": 
			case "album" : addComplexToPlaylist(id); break
			case "track" : addTrackToPlaylist(id); break
			default:
		}
    }
    
    function getUrlVars() {
        var vars = [], hash;
        var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
        for(var i = 0; i < hashes.length; i++)
        {
            hash = hashes[i].split('=');
            vars.push(hash[0]);
            vars[hash[0]] = hash[1];
        }
        return vars;
    }
    function initPlayer() {
		$("#footer_player").flowplayer("flowplayer/flowplayer-3.2.7.swf", {
			clip:  {
				autoPlay: false,
				autoBuffering: true
			}, 
			playlist: [
			{
				url: '/music/1.mp3',
				name: 'Palm trees and the sun'
			}],
			plugins: {
				controls: {
					playlist: true,
					backgroundColor: "transparent",
				   backgroundGradient: "none",
				   sliderColor: '#FFFFFF',
				   sliderBorder: '1.5px solid rgba(160,160,160,0.7)',
				   volumeSliderColor: '#FFFFFF',
				   volumeBorder: '1.5px solid rgba(160,160,160,0.7)',

				   timeColor: '#ffffff',
				   durationColor: '#535353',

				   tooltipColor: 'rgba(255, 255, 255, 0.7)',
				   tooltipTextColor: '#000000'

				}
			}
		});
	
    }

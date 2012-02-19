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
    
	function addHiding(element) {
		$(element).click(function() {
				$(this).parent().parent().next().toggle();
				var mbid = $(this).parent().parent().parent().attr("id");
				if (checkObjectStatus(mbid) == "empty") {
					setLoadingObject(mbid);
					switch (getObject(mbid).type) {
						case "artist": 
							var objectsList = $("#"+mbid+" .albums ul");
							$.getJSON(getArtistUrl, {'id' : mbid}, function(json) {
								$("#album_template").tmpl(json).appendTo(objectsList);
								updateArtist(json);
							});
							break
						case "album": 
							var objectsList = $("#"+mbid+" .tracks ul"); //???
							$.getJSON(getAlbumUrl, {'id' : mbid}, function(json) {
								$("#album_tracks_template").tmpl(json).appendTo(objectsList);
								updateAlbum(json);
							});
							break
						default:
					}
				}
				return false;
			}).parent().parent().next().hide();
	}
	
	function addTrackToPlaylist(mbid, playlist_element) {
		if (checkObjectStatus(mbid) == "empty") {
			
		}
		var getTrackUrl  = "http://"+ ip +":6006/get/track?format=json&jsoncallback=?";
		$.getJSON(getTrackUrl, {'id' : id}, function(json) {
								var player = $f("footer_player");
								if(!player.isLoaded()) {
									player.load(function() {
										$("#clips").append("<a href=${url}> <span> ${name}</span> </a>");
										player.setPlaylist([json,]);
										player.playlist("#clips", {loop:true});
									});
								} else {				
									var position =  player.getPlaylist().length; 	
									player.addClip(json);	
								}
                    }); 
	}
    
    function addToPlaylist(playlist_element, button, shouldPlay) {
       var id = button.parent().parent().attr('id');
       var player = $f("footer_player");
       var getTrackUrl  = "http://"+ ip +":6006/get/track?format=json&jsoncallback=?";
       $.getJSON(getTrackUrl, {'id' : id}, function(json) {
								var player = $f("footer_player");
								if(!player.isLoaded()) {
									player.load(function() {
										$("#clips").append("<a href=${url}> <span> ${name}</span> </a>");
										player.setPlaylist([json,]);
										player.playlist("#clips", {loop:true});
									});
								} else {				
									var position =  player.getPlaylist().length; 	
									player.addClip(json);	
								}
                    }); 
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
					playlist: true
				}
			}
		});
	
    }

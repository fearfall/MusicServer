-- selecting information about track: 
	-- aid(id in VK)
	-- mbid(id in MB) 
	-- url
	-- name
	-- album_id (album mbid)
	-- duration
------ start ------
select distinct
	aid.aid,  
	r.gid as mbid, 
	aid.url, 
	rl.gid as album_id,
	tn.name as name
	t.length
	
into simple_track_info

from release_group_type rlgt, release_group rlg,
 release rl, release_name rln, medium m, tracklist tl,
 recording r, track_name tn, track t, aid
 
where 
aid.mbid = r.gid 
and tn.id = t.name 
and t.recording = r.id 
and t.tracklist = tl.id 
and m.tracklist = tl.id 
and m.release = rl.id 
and rl.release_group = rlg.id 
and rln.id = rlg.name 
and rlg.type = rlgt.id;

------ finish ------



-- selecting information about album: 
	-- mbid(id in MB) 
	-- name
	-- artist_id (artist mbid)
	-- type
------ start ------ CHECK ME
select distinct
	rl.gid as mbid,
	a.gid as artist_id, 
	rln.name as name, 
	rlgt.name as type
	
into simple_album_info

from release_group_type rlgt, release_group rlg,
 release rl, release_name rln, artist_name an, artist a, artist_credit_name acn, artist_credit ac, aid
where 
-- aid.mbid = r.gid 
-- and tn.id = t.name 
-- and t.recording = r.id 
r.artist_credit = ac.id 
and ac.id = acn.artist_credit 
and acn.artist = a.id 
and a.name = an.id 
-- and t.tracklist = tl.id 
-- and m.tracklist = tl.id 
-- and m.release = rl.id 
and rl.release_group = rlg.id 
and rln.id = rlg.name 
and rlg.type = rlgt.id;
------ finish ------


-- selecting information about artist: 
	-- mbid(id in MB) 
	-- name

------ start ------ CHECK ME
select distinct 

	a.gid as mbid, 
	a.name as name
	
into simple_artist_info

from artist_name an, artist a, artist_credit_name acn, artist_credit ac, aid
where
-- aid.mbid = r.gid 
-- and tn.id = t.name 
-- and t.recording = r.id 
--and r.artist_credit = ac.id 
ac.id = acn.artist_credit 
and acn.artist = a.id 
and a.name = an.id ;
-- and t.tracklist = tl.id 
-- and m.tracklist = tl.id 
-- and m.release = rl.id 
-- and rl.release_group = rlg.id 
-- and rln.id = rlg.name 
-- and rlg.type = rlgt.id;

------ finish ------

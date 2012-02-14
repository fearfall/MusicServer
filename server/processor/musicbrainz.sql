select tn.name, r.length, r.gid from 
artist a, artist_name an,
 artist_credit_name acn, 
 artist_credit ac, 
recording r, track_name tn,
recording_puid rp,
puid p 
where 
a.name = an.id 
and an.name = 'Radiohead' 
and a.id = acn.artist 
and acn.artist_credit = ac.id 
and ac.id = r.artist_credit
and r.name = tn.id
and rp.recording = r.id
and p.id = rp.puid
and exists (select * from recording_puid rp where rp.recording=r.id) 
and length is not null 
and not exists ( select * from aid where aid.mbid = r.gid) 
and not exists ( select * from unfinished_song us where us.mbid = r.gid)
;



-- receiving tracks info for all songs
select aid.aid, r.gid as recording, 
tn.name as track, 
an.name as artist, 
rln.name as release, 
rlgt.name  as type
into simple_trackinfo
from release_group_type rlgt, release_group rlg,
 release rl, release_name rln, medium m, tracklist tl,
 recording r, track_name tn, track t, 
 artist_name an, artist a, artist_credit_name acn, artist_credit ac, aid
where aid.mbid = r.gid 
and tn.id = t.name 
and t.recording = r.id 
and r.artist_credit = ac.id 
and ac.id = acn.artist_credit 
and acn.artist = a.id 
and a.name = an.id 
and t.tracklist = tl.id 
and m.tracklist = tl.id 
and m.release = rl.id 
and rl.release_group = rlg.id 
and rln.id = rlg.name 
and rlg.type = rlgt.id
;


-- get artists names
select artist from simple_trackinfo sti where type = 'Album' group by artist;


-- get artist albums
select distinct release from simple_trackinfo sti where type = 'Album' and artist = 'Rihanna';

-- merge tracks (doesn't work)

merge into aid 
when matched and quality < 256 then update set quality = 256, aid = "134_256" 
when not matched then insert into (aid, rid, quality) values ("123_123", 8181, 256)

 -- duplicates
delete from aid where exists ( select 'x' from aid i where i.rid = aid.rid  and i.quality < aid.quality )
--duplicates real =)
delete from all_tracks_info where id not in ( select distinct on(recording) id from all_tracks_info);
DELETE FROM all_tracks_info
       WHERE recording NOT IN (SELECT min(recording)
                        FROM all_tracks_info
                        GROUP BY hash HAVING count(*) >= 1)
-- artist's tracks names and length
select r.gid, tn.name, r.length from 
artist a, artist_name an, 
artist_credit_name acn, 
artist_credit ac, 
recording r, track_name tn
where 
a.name = an.id 
and an.name = 'Lady Gaga' and a.id = acn.artist 
and acn.artist_credit = ac.id 
and ac.id = r.artist_credit 
and r.name = tn.id
and exists (select * from recording_puid rp where rp.recording=r.id);

-- get puids for track
select p.puid from puid p, recording_puid rp, recording r
where rp.recording = r.id and p.id = rp.puid and r.gid = '51efc4c3-37bf-359a-bfa3-dd115c05abe2';




select r.id, tn.name, p.puid from 
 recording r, track_name tn,
recording_puid rp,
puid p 
where 
r.name = tn.id
and rp.recording = r.id
and p.id = rp.puid
and r.gid = '921cad08-47ba-4e71-9303-de3b868922ea';

-- create aid
create table aid (id serial, aid varchar(40), mbid uuid, url varchar(200), quality integer, questionable integer);
-- create unfinished songs
create table unfinished_song (id serial, artist varchar(100), name varchar(200), mbid uuid, error varchar(500));
-- create 
create table res_time(id serial, mbid uuid, res_time integer, res_type varchar(10));

-- select tracks of artist's albums
select distinct r.gid, 
tn.name, 
r.length 
from release_group_type rlgt, release_group rlg,
 release rl, release_name rln, medium m, tracklist tl,
 recording r, track_name tn, track t, 
 artist_name an, artist a, artist_credit_name acn, artist_credit ac
where
an.name = 'The Beatles'
and tn.id = t.name 
and t.recording = r.id 
and r.artist_credit = ac.id 
and ac.id = acn.artist_credit 
and acn.artist = a.id 
and a.name = an.id 
and t.tracklist = tl.id 
and m.tracklist = tl.id 
and m.release = rl.id 
and rl.release_group = rlg.id 
and rln.id = rlg.name 
and rlg.type = rlgt.id
and rlgt.name  = 'Album'
and exists (select * from recording_puid rp where rp.recording=r.id) 
and r.length is not null 
and not exists ( select * from aid where aid.mbid = r.gid) 
and not exists ( select * from unfinished_song us where us.mbid = r.gid)
;

-- select puids of all songs for artist

select p.puid, r.gid
from puid p, recording_puid rp, recording r, 
artist_credit ac, artist_credit_name acn, artist a, artist_name an
where
an.name = 'Radiohead'
and an.id = a.name
and acn.artist = a.id
and acn.artist_credit = ac.id
and r.artist_credit = ac.id
and r.id = rp.recording
and p.id = rp.puid;   



-- into puids

select an.name as artist, r.gid, p.puid into puid_for_track
from puid p, recording_puid rp, recording r, 
artist_credit ac, artist_credit_name acn, artist a, artist_name an
where
an.id = a.name
and acn.artist = a.id
and acn.artist_credit = ac.id
and r.artist_credit = ac.id
and r.id = rp.recording
and p.id = rp.puid;


--easy track info access

select distinct an.name as artist, r.gid, 
tn.name,                                                                              
r.length into easy_access_track_info
from release_group_type rlgt, release_group rlg,
 release rl, release_name rln, medium m, tracklist tl,
 recording r, track_name tn, track t, 
 artist_name an, artist a, artist_credit_name acn, artist_credit ac
where tn.id = t.name 
and t.recording = r.id 
and r.artist_credit = ac.id 
and ac.id = acn.artist_credit 
and acn.artist = a.id 
and a.name = an.id 
and t.tracklist = tl.id 
and m.tracklist = tl.id 
and m.release = rl.id 
and rl.release_group = rlg.id 
and rln.id = rlg.name 
and rlg.type = rlgt.id
and rlgt.name  = 'Album'
and exists (select * from recording_puid rp where rp.recording=r.id) 
and r.length is not null 
and not exists ( select * from aid where aid.mbid = r.gid) 
and not exists ( select * from unfinished_song us where us.mbid = r.gid);




select s, e1, e2, e3, e4 from 
(select count(distinct(mbid)) as s, mbid as ids from res_time where res_type = 'Success' group by mbid) as res0,
(select count(distinct(mbid)) as e1, mbid as ide1  from res_time where res_type = 'Error 1' group by mbid) as res1,
(select count(distinct(mbid)) as e2, mbid as ide2  from res_time where res_type = 'Error 2' group by mbid) as res2,
(select count(distinct(mbid)) as e3, mbid as ide3  from res_time where res_type = 'Error 3' group by mbid ) as res3,
(select count(distinct(mbid)) as e4, mbid as ide4  from res_time where res_type = 'Error 4' group by mbid) as res4
where ids != ide1 and ide1 != ide2 and ide2 != ide3 and ide3 != ide4 ;

-- selection of times of results
select s, e1, e2, e31, e32, e33, e4 from 
(select avg(res_time) as s from res_time where res_type = 'Success') as res0,
(select avg(res_time) as e1 from res_time where res_type = 'Error 1') as res1,
(select avg(res_time) as e2 from res_time where res_type = 'Error 2') as res2,
(select avg(res_time) as e31 from res_time where res_type = 'Error 3.1') as res31,
(select avg(res_time) as e32 from res_time where res_type = 'Error 3.2') as res32,
(select avg(res_time) as e33 from res_time where res_type = 'Error 3.3') as res33,
(select avg(res_time) as e4 from res_time where res_type = 'Error 4') as res4;







-- ratio: album tracks/tracks

select distinct r.gid , a.name, rlg.type
into artist_types
from release_group_type rlgt, release_group rlg,
 release rl, release_name rln, medium m, tracklist tl,
 recording r, track_name tn, track t, 
 artist_name an, artist a, artist_credit_name acn, artist_credit ac
where
tn.id = t.name 
and t.recording = r.id 
and r.artist_credit = ac.id 
and ac.id = acn.artist_credit 
and acn.artist = a.id 
and a.name = an.id 
and t.tracklist = tl.id 
and m.tracklist = tl.id 
and m.release = rl.id 
and rl.release_group = rlg.id 
and rln.id = rlg.name 
and rlg.type = rlgt.id) as res;

select a_tracks, tracks 
from 
	(select count(distinct(gid)) as a_tracks 
	from artist_types 
	where type = 1 
	group by name ) as res,
	(select count(distinct(gid)) as tracks 
	from artist_types  
	group by name ) as res1;

-- analysys
select count(distinct(res_time.mbid)), res_type from  res_time where not exists ( select * from aid where aid.mbid = res_time.mbid) group by res_type;

DELETE  FROM easy_access_track_info USING aid where aid.mbid = gid;
-- svemarch@yandex.ru GoodLuck 5187286 2217192
-- http://api.vkontakte.ru/oauth/authorize?client_id=2217192&scope=audio,offline&redirect_uri=http://api.vkontakte.ru/blank.html&display=page&response_type=token
-- access_token=cc01b085cc4e9653cc13ca9f68cc6f42bb4cc4ecc4f965b5274b10a1c737a99&expires_in=0&user_id=5187286

select 

	aid.aid, 
	r.gid as mbid, 
	url, 
	rl.gid as album_id,
	tn.name as name
	 
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




select 

rl.gid as mbid,
a.gid as artist_id, 
rln.name as name, 
rlgt.name as type

into simple_album_info

from release_group_type rlgt, release_group rlg,
 release rl, release_name rln, medium m, tracklist tl,
 recording r, track_name tn, track t, 
 artist_name an, artist a, artist_credit_name acn, artist_credit ac, aid
where 
aid.mbid = r.gid 
and tn.id = t.name 
and t.recording = r.id 
and r.artist_credit = ac.id 
and ac.id = acn.artist_credit 
and acn.artist = a.id 
and a.name = an.id 
and t.tracklist = tl.id 
and m.tracklist = tl.id 
and m.release = rl.id 
and rl.release_group = rlg.id 
and rln.id = rlg.name 
and rlg.type = rlgt.id;


select 
a.gid as mbid, 
a.name as name
into simple_artist_info
from release_group_type rlgt, release_group rlg,
 release rl, release_name rln, medium m, tracklist tl,
 recording r, track_name tn, track t, 
 artist_name an, artist a, artist_credit_name acn, artist_credit ac, aid
where aid.mbid = r.gid 
and tn.id = t.name 
and t.recording = r.id 
and r.artist_credit = ac.id 
and ac.id = acn.artist_credit 
and acn.artist = a.id 
and a.name = an.id 
and t.tracklist = tl.id 
and m.tracklist = tl.id 
and m.release = rl.id 
and rl.release_group = rlg.id 
and rln.id = rlg.name 
and rlg.type = rlgt.id;








--for tracks

select 
	aid.aid, 
	aid.url, 
	aid.mbid as track_mbid,
	track_name.name as track_name, 
	release.gid as album_mbid,
	release_name.name as album_name,
	a.gid as artist_mbid,
	artist_name.name as artist_name
	
into simple_track_info
	
from aid, recording, track, track_name, release, release_name, medium, artist_name, 
artist a, artist_credit_name acn, artist_credit ac
where 
	recording.gid = aid.mbid 
and recording.name = track_name.id
and recording.id = track.recording
and track.tracklist = medium.tracklist
and medium.release = release.id
and release.name = release_name.id
and recording.artist_credit = ac.id 
and ac.id = acn.artist_credit 
and acn.artist = a.id 
and a.name = artist_name.id 
and track.artist_credit = acn.artist_credit
;

--for albums
select 
	release.gid as album_mbid,
	release_name.name as album_name,
	rgt.name as type,
	artist.gid as artist_mbid,
	artist_name.name as artist_name
	
into simple_album_info

from 
	release, release_name, release_group, release_group_type rgt, 
	artist, artist_name, artist_credit_name acn
where 
	--release.gid = :mbid and
    release.name = release_name.id
and release.release_group = release_group.id
and release_group.type = rgt.id
and release.artist_credit = acn.artist_credit
and artist.id = acn.artist
and artist.name = artist_name.id;



--for artist
select
	artist.gid as artist_mbid,
	artist_name.name 

into simple_artist_info

from 
	artist, artist_name
where
	--artist.gid = :mbid and
	artist.name = artist_name.id;

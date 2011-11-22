import urllib2
import urllib
import re
import cookielib
import hashlib
from mutagen.mp3 import MP3
import simplejson
import os
import sys
import musicdns, musicdns.cache 
import time
import datetime
import threading
#import Queue

#from vkapi import *
import psycopg2, psycopg2.pool as pool
from operator import itemgetter, attrgetter
from xml.dom.minidom import parseString
import xml.etree.ElementTree as etree
from pprint import pprint	
from multiprocessing import Process, Queue
import re;

##########################################UTILITY_FUNCTIONS###############################
connection_pool =  pool.ThreadedConnectionPool(14, 14, "dbname='musicbrainz_db' user='musicbrainz'")

#import vkapi

#iitem_pool = Queue.Queue (0)
#lock = threading.Lock()
cache = musicdns.cache.MusicDNSCache()
#log_file = open("errors.txt", "a+r")	
params = []	
def log(message):
	#print ""
#	log_file.write(str(datetime.datetime.now()) + " " + message + " \n")
	print str(datetime.datetime.now()) + " " + message

def log_error(artist, name, mbid, error):
	log(error)
	save_error(artist, name, mbid, error)
	log("#############################TRACK_FINISHED_ERROR########################################")

def save_error(artist, name, mbid, error):
	add_row_query = "insert into unfinished_song (artist, name, mbid, error) values (%s, %s, %s, %s)";
	params = (artist, name, mbid, error)
	execute_query(add_row_query, params, True, False)

mybegin = 0	
def save_time(mbid, begin, res_type):
	#global mybegin
	new_begin = time.time()
	delta = new_begin - begin

	#if begin == 0:
		#   delta = 0

		#mybegin = new_begin
	save_time_query	= "insert into res_time(mbid, res_time, res_type) values (%s, %s, %s)"
	params = (mbid, delta*1000, res_type)
	execute_query(save_time_query, params, True, False)

def execute_query(query, params, commit, fetch):
	try:
		conn = psycopg2.connect("dbname='musicbrainz_db' user='musicbrainz'");
		#conn = connection_pool.getconn()
		cur = conn.cursor()
		if commit :
			try:
				cur.execute(query, params)
			except:
				pass
				log("ERROR: Inserting of "  +str(params) + " failed: " + str(sys.exc_info()[1]))
		elif params is None:
			cur.execute(query)
		else:
			cur.execute(query, params)
		if fetch:
			return cur.fetchall()
	except:
		log( " ERROR:I am unable to execute query " + query + " because "+str(sys.exc_info()[1]))
	finally:
		conn.commit()
		cur.close()
	   # connection_pool.putconn(conn)
		conn.close()

##########################################END_UTILITY_FUNCTIONS###############################	

##########################################TRACK_PROCESSOR###############################

class Processor(Process):#threading.Thread):

   # def __init__(self, params, group=None, target=None, name=None, args=(), kwargs=None, verbose=None):
   #	 super(Processor, self).__init__(group, target, name, args, kwargs, verbose)
   #	 print params
		#self.vkapi = PyVkApi(email=params[0], password=params[1], uid=params[2], api_id=params[3])

 #	   self.vkapi = (params[0], params[1])#PyVkApi(email=params[0], password=params[1], uid=params[2], api_id=params[3])
#
	def __init__(self, group=None, target=None, name=None, args=(), kwargs={}):
		super(Processor, self).__init__(group, target, name, args, kwargs)	
		self.vkapi = (args[0], args[1])#PyVkApi(email=params[0], password=params[1], uid=params[2], api_id=params[3])
   #	#params = uid, access_token


	def run(self):
		self.process_track()
		super(Processor, self).start()


	def save_track(self, aid, mbid, quality, url, questionable):
		add_row_query = "insert into aid (aid, mbid, quality, url, questionable) values (%s, %s, %s, %s, %s)"
		params = (aid, mbid, int(quality), url, int(questionable))
		execute_query(add_row_query, params, True, False)


	def sort_by_length(self, songs, length):
	#	result = []
	#	for song in songs:
	#		url = song['url']
	#		length = int(urllib2.urlopen(url).info()['Content-Length'])
	#		if length not in result:result.append((length, song))
		seen = set()
		songs_delta = [((abs(int(x['duration']) - length/1000)), x)
						for x in songs
						if (abs(int(x['duration']) - length/1000)) not in seen
						and not seen.add((abs(int(x['duration']) - length/1000)))
						]
		return sorted(songs_delta)

	def get_track_info(self, item):
		track = item[0]
		length = item[2]
		log("INFO: Starting receiving songs names " + str(length))
		html_escape_table = {
		"&": " ",
		'"': " ",
		"'": " ",
		">": " ",
		"<": " ",
		"#": " ",
		"+": " ",
		}
		track =  "".join(html_escape_table.get(c,c) for c in track)
		print track
		result = []
		#try:
			#result = self.vkapi.call(['method=audio.search', 'q='+repr(track), 'sort=1', 'count=200' ])
		url = 'https://api.vkontakte.ru/method/audio.search?&q=' + urllib.quote(repr(track)) + '&uid=' + str(self.vkapi[0]) + '&access_token=' + str(self.vkapi[1])
		#print url
		result = simplejson.loads(urllib2.urlopen(url).read())
		#print result
		log("INFO: Finished receiving songs names")
		#except:
		# print "ERROR ASCII"
		if not result:
			error = "ERROR: Song " + track + " doesn't exist in VK"
			log_error("", item[0], item[1], error)
			return (None , -1)
		seen = set()
		#FIX ME: magic number
		
		result =  result['response']
		#print result
		if(result == [0]):
			log("INFO: result is null")
			m = re.search(r'[([{].*[)\]}]', track)
			if m is not None:
				track = re.sub(r'[([{].*[)\]}]', '', track)
				return self.get_track_info((track, item[1], item[2]))
			return (None, -2)
		cleaned_result = [x
							for x in result[1:]
							if x['duration'] not in seen
							and not seen.add(x['duration'])
							and (abs(int(x['duration']) - length/1000) < 5)
							]
		if cleaned_result:
			log("INFO: Size of songs array is " + str(len(cleaned_result)))
			return (self.sort_by_length(cleaned_result, length), 1)
		else:
			log("INFO: no songs with same length")
			return (None, -3)

	def retrieve (self, track_to_retrieve):
		dirname = os.getcwd() + "/tracklist/";
		if not os.path.exists(dirname):
			os.mkdir(dirname)
		path = dirname + track_to_retrieve['aid'] + ".mp3";
		urllib.urlretrieve(track_to_retrieve['url'], path)
		log("INFO: Downloading of " + track_to_retrieve['title'] + " finished")
		mp3 = MP3(path)
		quality = mp3.info.bitrate / 1000
		received_puid = self.get_puid(path)
		if received_puid is not None:
			log("INFO: PUID for this song is: " + received_puid)
			return path, quality, received_puid
		else:
			os.remove(path)
			return None, None, None
			
	def add_mbids(self, puid, url): 
		get_mbids_query = "select gid from puid_for_track where puid = (%s)"
		rows = execute_query(get_mbids_query, (puid, ), False, True)
		if len(rows) == 0:
			return None
		save_mbids_query = "insert into questionable_mbids(puid, mbid, url) values (%s, %s, %s)"
		for row in rows:
			execute_query(save_mbids_query, (puid, row[0], url), True, False)
		
	def process_track(self):
		while True:
			data = item_pool.get()
			item = data[0] # id, gid, length
			puids = data[1]
			log("#############################TRACK_BEGINS##########################")
			t1 = time.time()
			try:
				log( "INFO: receiving " + item[0] + " with MBID " + item[1])
				mbid = item[1]
				(tracks_to_retrieve, result_code) = self.get_track_info(item)
				if result_code > 0:
					for longest in tracks_to_retrieve:
						path, quality, received_puid = self.retrieve(longest[1])
						if received_puid is not None:
							questionable = True
							if received_puid in puids:
								questionable = False
								save_time(mbid, t1, "Success")
								log("#############################TRACK_FINISHED_SUCCESS############################")						
								self.save_track(
									longest[1]['owner_id'] + "_" + longest[1]['aid'],
									mbid,
									quality,
									longest[1]['url'],
									questionable
									)
								os.remove(path)
								break
							else: #save if this is the last one, and no match was found as questionable.. or the first one??
								questionable = True
								save_time(mbid, t1, "Error 1")
								error = "ERROR: i can't add song because it's PUID not in set of expected PUIDs  "
								self.add_mbids(received_puid, longest[1]['url']);
								log_error("", item[0], item[1], error)
								continue

						else:
							save_time(mbid, t1, "Error 2")
							error = "ERROR: i can't generate PUID (it's None)"
							log_error("", item[0], item[1], error)
				else:
					if result_code == -1 : 
						save_time(mbid, t1, "Error 3.1")
						error = "ERROR: There are no song with same length (retrieved result is none)"
						log_error("", item[0], item[1], error)
					if result_code == -2 : 
						save_time(mbid, t1, "Error 3.2")
						error = "ERROR: There are no song with same length really"
						log_error("", item[0], item[1], error)
					if result_code == -3 : 
						save_time(mbid, t1, "Error 3.3")
						error = "ERROR: ERROR: There are no song with same length after cleaning "
						log_error("", item[0], item[1], error)

			except:
				save_time(mbid, t1, "Error 4")
				error = "ERROR: Error occured with song " + item[0] + ": " + str(sys.exc_info()[1])
				print error
				log_error("", item[0], item[1], error)

	def get_puid(self, filename):
		try:
			puid, duration = cache.getpuid((filename).encode("utf8"), "a7f6063296c0f1c9b75c7f511861b89b")
			return puid
		except:
			error = ""
			log( "ERROR: Failed to get PUID for song AID:"+ filename + " because "+str(sys.exc_info()[1]))
##########################################END_TRACK_PROCESSOR###############################

def get_puids_from_bd(artist):
		print artist
		get_puids_query = '''select gid, puid
	from puid_for_track where artist = (%s)'''
		try:
			result = execute_query(get_puids_query, (artist, ), False, True)
			log("INFO: Sorting PUIDs")
			#pprint(result)

			puids = {}
			for x in result:
				if x[0] in puids:
					puids[x[0]].append(x[1])
				else:
					puids[x[0]] = [x[1]]
			log("INFO: Sorting PUIDs was successfully finished")
			return puids
		except:
			log("ERROR: receiving PUIDS for artist failed")
			return None

def get_top_artist_tracks(artist_name):
	try:
		get_tracks_query = "select gid, name, length from easy_access_track_info where artist = (%s)"
		rows = execute_query(get_tracks_query, (artist_name, ), False, True)
		if len(rows) == 0:
			return ([],None)
		puids = get_puids_from_bd(artist_name)
		info = []
		for row in rows:
			if artist_name == '[unknown]':
				id = row[1]
			else :
				id = artist_name + " " + row[1]
			info.append((id, row[0], row[2]))
		return info, puids
	except:
		error = "ERROR: Can't receive " + artist_name + " track names "
		log(error)


def add_wiki_artists(artists, cmcontinue):
   url = "http://ru.wikipedia.org/w/api.php?action=query&prop=categories&list=categorymembers&rvprop=content&format=xml&cmtitle=%D0%9A%D0%B0%D1%82%D0%B5%D0%B3%D0%BE%D1%80%D0%B8%D1%8F:%D0%9C%D1%83%D0%B7%D1%8B%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5_%D0%BA%D0%BE%D0%BB%D0%BB%D0%B5%D0%BA%D1%82%D0%B8%D0%B2%D1%8B_%D0%BF%D0%BE_%D0%B0%D0%BB%D1%84%D0%B0%D0%B2%D0%B8%D1%82%D1%83&cmlimit=500&"
   if cmcontinue is not None:
	   url = url + "cmcontinue=" + cmcontinue
   tree = etree.parse(urllib2.urlopen(url))
   root = tree.getroot()
   items = root.getiterator("cm")
   artists.extend([x.get("title") for x in items ])
   querycontinue = root.find("query-continue/categorymembers")
   if querycontinue is not None:
	   cmcontinue = querycontinue.get("cmcontinue")
	   artists = add_wiki_artists(artists, cmcontinue.encode("utf-8"))
   return artists


def get_top_artists():
   #last.fm top artists
   url = "http://ws.audioscrobbler.com/2.0/?method=chart.getTopArtists&api_key=332c084e0c3fe0f2cf6a913d21d84404&limit=1000"
   tree = etree.parse(urllib2.urlopen(url))
   raw_artists = tree.getroot().getiterator("name")
   artists = [x.text for x in raw_artists]
   #wikipedia music category
#	artists = self.add_wiki_artists(artists, None)
   #print str(artists)
   return artists


def add():
	artists = get_top_artists()
	track_count = 0
	for artist in artists:
		try:
			info, puids = get_top_artist_tracks(artist.encode('utf8'))
			track_count = track_count + len(info)
			log("INFO: " + artist +" tracks number: " + str(len(info)) + '/' + str(track_count));
			for item in info:
				item_pool.put((item, puids[item[1]]))
		except:
			error = "ERROR: Some unhandled error occured with artist " + artist + ": " + str(sys.exc_info()[1])
			log(error)


def process_tracks():
	p = Process(target=add)
	p.start()
	print "I'm going)"
	for x in xrange (1):
		Processor(args=params[x]).start()
	#p.join()

#def prepare_database(): 
#	for letter in string.letters:
#		 select_tracks_query = "select * into" + \
#							  letter + "_easy_access_track_info where artist  = (%s)"
#		   rows = execute_query(get_tracks_query, (artist_name, ), False, True)
def main():
	global params
	global item_pool
	item_pool = Queue()
	musicdns.initialize()
	params = []
	with open("params.txt", "a+r") as f:
		lines = f.readlines()
		for line in lines:
			params.append(line.strip().split(' '))
	#print params
	process_tracks()

if __name__ == '__main__':
  main()


#python vkprep.py fearfall@gmail.com dfhbif[1989] 317437 2217192


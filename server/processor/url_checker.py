import urllib2
import urllib
import re
import cookielib
import hashlib
import simplejson
import os
import sys
import psycopg2, psycopg2.pool as pool
import time
import datetime
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
def execute_query(query, params, commit, fetch):
	#try:
	conn = psycopg2.connect("dbname='musicbrainz_db' user='musicbrainz'");
	#conn = connection_pool.getconn()
	cur = conn.cursor()
	if commit :
		try:
			cur.execute(query, params)
		except:
			pass
			#log("ERROR: Inserting of "  +str(params) + " failed: " + str(sys.exc_info()[1]))
	elif params is None:
		cur.execute(query)
	else:
		cur.execute(query, params)
	if fetch:
		return cur.fetchall()
	#except:
		#log( " ERROR:I am unable to execute query " + query + " because "+str(sys.exc_info()[1]))
	#finally:
	conn.commit()
	cur.close()
	# connection_pool.putconn(conn)
	conn.close()

def update_url(params, mbid):
	get_tracks_query = "select aid from simple_track_info where mbid = (%s)"
	rows = execute_query(get_tracks_query, (mbid,), False, True)
	if len(rows) == 0:
		return ([],None)
	result = {}
	for row in rows:
		try:
			aid = row[0]
			print aid
			url = 'https://api.vkontakte.ru/method/audio.getById?&audios='+ str(aid) +'&uid=317437&access_token=76b4cf3676fbe9e076fbe9e0f376da3d08f76fb76fae9e804bf890503cbd8ca';
			result = simplejson.loads(urllib2.urlopen(url).read())
			result =  result['response'][0]['url']
			print (result, aid)
			insert_url_query = "update simple_track_info set url = (%s) where aid = (%s)"
			execute_query(insert_url_query, (result, aid), True, False) 
			return result
		except:
			pass	
		
class URLUpdateHandler(BaseHTTPRequestHandler):
	
	def do_GET(s):
		print s.path;
		s.send_response(200)
		s.send_header("Content-type", "plain/text")
		s.end_headers()
		pos = s.path.find('up/')
		if(pos != -1):
			url = update_url(None, s.path[(pos+3):])
			s.wfile.write(url)
		
	#http://cs4755.vkontakte.ru/u71990408/audio/488e386200fa.mp3
	#http://cs4755.vkontakte.ru/u71990408/audio/991fdf8c2044.mp3
def main():
	#global params
	params = []
	try: 
		server = HTTPServer(('', 6007), URLUpdateHandler)
		server.serve_forever()
	except KeyboardInterrupt:
		server.socket.close()
	with open("params.txt", "a+r") as f:
		lines = f.readlines()
		for line in lines:
			params.append(line.strip().split(' '))
	#update_url(params, mbid)

if __name__ == '__main__':
  main()



#update all_tracks_info set url = 'http://cs4429.vkontakte.ru/u4035288/audio/e155cbf9ee3c.mp3' where aid = '-10250870_64689320'

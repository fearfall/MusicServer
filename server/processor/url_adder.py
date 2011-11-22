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

def get_urls(params):
	get_tracks_query = "select aid_2810 from all_tracks_info where url is null"
	rows = execute_query(get_tracks_query, (), False, True)
	if len(rows) == 0:
		return ([],None)
	result = {}
	for row in rows:
		try:
			aid = row[0]
			url = 'https://api.vkontakte.ru/method/audio.getById?&audios='+ str(aid) +'&uid=317437&access_token=76b4cf3676fbe9e076fbe9e0f376da3d08f76fb76fae9e804bf890503cbd8ca';
			result = simplejson.loads(urllib2.urlopen(url).read())
			result =  result['response'][0]['url']
			print (result, aid)
			insert_url_query = "update all_tracks_info set url = (%s) where aid = (%s)"
			execute_query(insert_url_query, (result, aid), True, False) 
		except:
			pass	
		
def main():
	#global params
	params = []
	with open("params.txt", "a+r") as f:
		lines = f.readlines()
		for line in lines:
			params.append(line.strip().split(' '))
	get_urls(params)

if __name__ == '__main__':
  main()


#update all_tracks_info set url = 'http://cs4429.vkontakte.ru/u4035288/audio/e155cbf9ee3c.mp3' where aid = '-10250870_64689320'

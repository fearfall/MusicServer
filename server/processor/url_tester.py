import os
import sys
import httplib2
import psycopg2, psycopg2.pool as pool
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
		
def test_urls():
	
		get_urls_query = 'select from all_tracks_info'
		urls = execute_query(get_urls_query, None, False, True)
		for url in urls:
			try:
				h = httplib2.Http()
				resp = h.request(url[0], 'HEAD')
				print url, resp[0]['status']
			except:
				continue

				
def main():
	test_urls()

if __name__ == '__main__':
  main()

import os
import sys
import httplib2
import musicdns, musicdns.cache 

cache = musicdns.cache.MusicDNSCache()

def get_puid(filename):
	try:
		puid, duration = cache.getpuid((filename).encode("utf8"), "a7f6063296c0f1c9b75c7f511861b89b")
		print puid
	except:
		print "ERROR: Failed to get PUID for song AID:"+ filename + " because "+str(sys.exc_info()[1])
				
def main():
	args = sys.argv[1:];
	
	musicdns.initialize()
	print args[0]
	get_puid(args[0])

if __name__ == '__main__':
  main()

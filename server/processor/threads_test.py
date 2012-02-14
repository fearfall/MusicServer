import os
import sys, traceback
import Queue	
import time
#from multiprocessing import Process, Queue
import re
#from threading import Thread
import threading
item_pool = Queue.Queue (0)

def fact(n):
	res = 1
	for x in xrange(n - 1):
		res = res*(x + 1)
	return res

class Processor(threading.Thread):#Process):#

	def __init__(self, group=None, target=None, name=None, args=(), kwargs=None, verbose=None):
		super(Processor, self).__init__(group, target, name, args, kwargs, verbose)
		
	def run(self):
		self.process_track()
		super(Processor, self).start()
	
	
	def process_track(self):
		while True:
			track = item_pool.get()
			print self.getName(), " got ", track
			time.sleep(track)
			res = fact(track)
			print self.getName(), " leaved ", track
			#print res;

def add():
	for x in xrange(1000000):
		try:
			item_pool.put(x)
			print threading.current_thread().getName()
		except:
			traceback.print_exc(file=sys.stdout)
			print "ERROR: ", x

def main():
	#p = Process(target=add)
	p = threading.Thread(target = add)
	p.start()
	print "I'm going)"
	for x in xrange (5):
		Processor().start()


if __name__ == '__main__':
  main()

# Python script to be used to create threads to downolad the e-mails
# from markmail.
import threading
import Queue
import time
import sys

import FetchMarkMail as FMM
import BrowseMarkMail as BMM
import CouchDBConnection as cdb

# Constants
numberOfThreads=20
numberOfThreadsCouchDB=20

# definition of the thread to fetch a list mail from mark mail and
# stores it in couchdb
class MailFetcher(threading.Thread):
    # constructor
    def __init__(self, maillist, servName, mailIDToProcess, name, queue):
        threading.Thread.__init__(self)
        self.maillist = maillist # the mailing-list name
        self.servName= servName
        self.mailIDToProcess = mailIDToProcess # a list of mail's IDs to download
        self.name = name # the thread name, useful for debug (I hope!)
        self.queue = queue #queue where the thread is stored

    #run method
    def run(self):
        n=len(self.mailIDToProcess)
        # get all the mail
        self.queue.get()
        for i in range(n):
            #print self.name+" "+self.mailIDToProcess[i]
            mail = FMM.getMailMessage(self.mailIDToProcess[i], self.maillist)
            # insert the mail in couchdb
            if not(mail==None):
                cdb.unsafeSaveMailCouchdb(mail, self.servName)
        self.queue.task_done()
        print " Thread "+str(self.name)+" finished."
# END CLASS

# fetch all the mails in the mailing list
def fetchMails(maillist, serverNamePort="http://localhost:5984"):	
    #print time.clock()
    ID=BMM.getAllMessageIDForMailingList(maillist, serverNamePort)
    cdb.createDatabase(maillist, serverNamePort)
    #print time.clock()
    # fetch and insert emails in the database 
    n=len(ID)/numberOfThreadsCouchDB
    q= Queue.Queue()
    # the first in the list is also the last mail read in 
    if len(ID)>0:
        t=MailFetcher(maillist, serverNamePort, [ID[0]], "First", q)
        t.start()
        q.put(t) # put in the queue
        q.join() # wait to finish    
        # update last element in couchdb
        doc={'_id': 'lastEmail', 'key':ID[0]}
        print "last "+ID[0]+" "+str(doc)
        cdb.updateDocCouchDB(doc, maillist, serverNamePort)

    # create the pool for the other emails
    print "Fetching the mails"
    if n==0:
        n=1
    for i in range(0, len(ID), n):
        if i+n+1>=len(ID):
            t=MailFetcher(maillist, serverNamePort, ID[i+1:len(ID)], "Block-"+str(i+1)+"-"+str(len(ID)), q)
        else:
            t=MailFetcher(maillist, serverNamePort, ID[i+1:i+n+1], "Block-"+str(i+1)+"-"+str(i+n+1), q)
        t.start()
        q.put(t)
    q.join()

#fetchMails("org.freenetproject.devl")
#fetchMails("org.w3.public-lod", "http://localhost:5988")

# Main method
def main():
	try:
		maillist=sys.argv[1]
		try:
			serv=sys.argv[2]
			fetchMails(maillist, serv)
		except IndexError:
			print "using default couchdb server-> http://localhost:5984"
			fetchMails(maillist)
	except IndexError:
		print "Mailing list address must be inserted!"
	
    
# To call the main method 
if __name__ == "__main__":
    main()

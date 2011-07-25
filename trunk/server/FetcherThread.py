# Python script to be used to create threads to downolad the e-mails
# from markmail.
import threading
import Queue

import FetchMarkMail as FMM
import CouchDBConnection as cdb

# Constants
numberOfThreads=20
numberOfThreadsCouchDB=5

# a container class for the list of result
# which ensures concurrency access
class ListResult:
    # construct an empty container
    def __init__(self, lastID):
        self.IDs=[]
        self.threadIDs=[]
        self.lock=threading.RLock()
        # variables to tell if it has to stop fetching mails
        self.lastID=lastID
        self.foundLast=False

    # check if the lastID has been found
    def checkFoundLast(self, ids):
        for ID in ids:
            if(ID==self.lastID):
                self.foundLast=True
                break

    # append lists
    def append(self, ids, threadids):
        self.lock.acquire()
        self.IDs.extend(ids)
        self.threadIDs.extend(threadids)
        self.checkFoundLast(ids)
        self.lock.release()

    # return the two lists
    def getLists(self):
        return self.IDs, self.threadIDs
#END CLASS

# definition of the threader's class to fetch the list of emails
# in a page of markmail
class Fetcher(threading.Thread):
    # constructor
    def __init__(self, pageNum, queue, listResult):
        threading.Thread.__init__(self)
        self.pageNum=pageNum
        self.queue=queue #queue where the thread is stored
        self.listResult=listResult #list where to save te results
        self.name=pageNum
        
    # run method
    def run(self):
        self.queue.get()
        #print "looking at page:"+str(self.pageNum)
        IDs, threadIDs = FMM.getIDandThread('org.w3.public-lod', self.pageNum)
        self.listResult.append(IDs, threadIDs)
        self.queue.task_done()
#END CLASS

# definition of the thread to fetch a list mail from mark mail and
# stores it in couchdb
class MailFetcher(threading.Thread):
    # constructor
    def __init__(self, maillist, mailIDToProcess, mailThIDToProcess, name, queue):
        threading.Thread.__init__(self)
        self.maillist = maillist # the mailing-list name
        self.mailIDToProcess = mailIDToProcess # a list of mail's IDs to download
        self.mailThIDToProcess = mailThIDToProcess # a list of mail thread ID relative to the mail's IDs
        self.name = name # the thread name, useful for debug (I hope!)
        self.queue = queue #queue where the thread is stored

    #run method
    def run(self):
        n=len(self.mailIDToProcess)
        # get all the mail
        self.queue.get()
        for i in range(n):
            #print self.name+" "+str(i)
            mail = FMM.getMailMessage(self.mailIDToProcess[i], self.mailThIDToProcess[i], self.maillist,)
            # insert the mail in couchdb
            cdb.saveMailCouchdb(mail)
        self.queue.task_done()
# END CLASS


# create the queue (Thread Pool) and retrieve the list of message to fetch
def getTheListOfMessage(numThreads, numPages, lastId):
    results=ListResult(lastId);
    q= Queue.Queue()
    # populates the result with the mails in the first thread
    # this ensures that the first element in the list is the last
    # mail arrived and fetched
    t=Fetcher(1,q,results)
    t.start()
    q.put(t) # put in the queue
    q.join() # wait to finish           
    
    cont=2 # stores the page number to be read
    # create threads
    while cont<=numPages:
        if (cont-1+numThreads)<=numPages:
            for i in range(numThreads):
                t=Fetcher(cont,q,results)
                t.start()
                q.put(t) # put in the queue
                cont=cont+1
        else:
            n=numPages-(cont-1)
            for i in range(1,n+1):
                t=Fetcher(cont,q,results)
                t.start()
                q.put(t) # put in the queue
                cont=cont+1
        # wait for all thread to finish
        q.join()
        print " fetched "+str(cont-1)+ " pages."
        # if found the last inserted into couchdb ...
        if results.foundLast:
            # ... stop.
            print "Found last"
            break
    
    print "Number of mails to fetch "+str(len(results.IDs))
    return results


# fetch all the mails in the mailing list
def fetchMail(maillist):
    # get the last id stored in the database 
    lastId=""
    # get the list of mails to fetch
    pages=FMM.getNumPages(maillist)
    print "Getting the list of mail to fetch:"
    result=getTheListOfMessage(numberOfThreads, pages, lastId)
    ID, threadID = result.getLists()
    
    # fetch and insert emails in the database 
    n=len(ID)/numberOfThreadsCouchDB
    q= Queue.Queue()
    # the first in the list is also the last mail read in  
    t=MailFetcher(maillist, [ID[0]], [threadID[0]], "First", q)
    t.start()
    q.put(t) # put in the queue
    q.join() # wait to finish    
    # update last element in couchdb
    #TODO
    # create the pool for the other emails
    print "Fetching the mails"
    for i in range(0, len(ID), n):
        t=MailFetcher(maillist, ID[i+1:i+n], threadID[i+1:i+n], "Block-"+str(i+1), q)
        t.start()
        q.put(t)
    q.join()

#tests
#lastID="jsud6yvtbpxdjfrh"
#pages=FMM.getNumPages('org.w3.public-lod')
#ThreadPool(5,pages,lastID)
fetchMail('org.w3.public-lod')


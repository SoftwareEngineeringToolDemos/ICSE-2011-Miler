# Python script used to browse markMarmail, since the markmail's "API" are broken by design

import sys
import urllib
import math
import threading
import Queue
from xml.etree import ElementTree

import CouchDBConnection as cdb

# some constants
browseUrl="http://markmail.org/browse/"
lock=threading.RLock()
foundLast=False

# a class defining a date for MarkMail
class MMDate:
    def __init__(self, date, cont):
        self.date=date
        self.messageCounter=int(cont)
    #return the date in a nice format
    def getDate(self):
        split=self.date.split(" ")
        month=""
        if split[1]=="January":
            month="01"
        elif split[1]=="February":
            month="02"
        elif split[1]=="March":
            month="03"
        elif split[1]=="April":
            month="04"
        elif split[1]=="May":
            month="05"
        elif split[1]=="June":
            month="06"
        elif split[1]=="July":
            month="07"
        elif split[1]=="August":
            month="08"
        elif split[1]=="September":
            month="09"
        elif split[1]=="October":
            month="10"
        elif split[1]=="November":
            month="11"
        elif split[1]=="December":
            month="12"
        return split[0]+"-"+month

    def __str__(self):
        return self.getDate()+" ("+str(self.messageCounter)+")"
#END CLASS

# a class creating a thread to get all the message
class GetIDs(threading.Thread):
    # constructor
    def __init__(self, maillist, date, queue, listResult, lastID):
        threading.Thread.__init__(self)
        self.maillist=maillist
        self.date=date
        self.lastID=lastID
        self.queue=queue #queue where the thread is stored
        self.listResult=listResult #list where to save te results

    # run method
    def run(self):
        global foundLast
        self.queue.get()
        mailsids=getMessageListForDate(self.maillist, self.date)
        #self.listResult.extend(mailsids)
        for mid in mailsids:
            lock.acquire()
            if foundLast or mid==self.lastID:
                foundLast=True
                break
            else:
                self.listResult.append(mid)
            lock.release()
        self.queue.task_done()

# given the maillist, the date and the page number (date and page are optional),
# returns the address of the corresponding page containing a list of message 
def getPageAddress(maillist, date="", page=""):
    if(date==""):
        return browseUrl+maillist
    else:
        return browseUrl+maillist+"/"+date+"/"+str(page)

# get a list of dates in wich emails are sorted for the given list
# return the list sorted in reverse order (most recent date first)
def getDateList(maillist):
    address=getPageAddress(maillist)
    page=urllib.urlopen(address) # get the corresponding web page
    tree = ElementTree.parse(page)

    # retrieve the xml elements containing all the dates
    body=tree.find("body")
    divList=body.findall("div")
    mainDiv=None
    for div in divList:
        tag=div.get("id")
        if tag=="main": # get the div containing the content we are looking for
            mainDiv=div
    table=mainDiv.find("table") # get a container table
    dateTable=None
    #for i in table.iter():
    for i in table.getiterator():
        if i.tag=="table" and not(i.get("id")=="browse-wrapper"): 
            dateTable=i # now we have the table we were looking for
            break
    elemList=dateTable.findall("tr")
    dateList=[]
    for el in elemList:
        tds=el.findall("td")
        # get date
        d=tds[0].find("a").text
        # get message counter
        c=tds[1].text.replace(",","")
        dateList.append(MMDate(d,c))
    dateList.reverse()
    return dateList

# return a list of message-id in the page for a date (string) and a page number (optional)
# the list returned is sorted in reverse order (most recent date first)
def getMessageListInPage(maillist, stringDate, pageNumb=""):
    address=getPageAddress(maillist, stringDate, pageNumb)
    page=urllib.urlopen(address) # get the corresponding web page
    tree = ElementTree.parse(page)
    
    # get the message's ids
    body=tree.find("body")
    divList=body.findall("div")
    mainDiv=None
    for div in divList:
        tag=div.get("id")
        if tag=="main": # get the div containing the content we are looking for
            mainDiv=div
    table=mainDiv.find("table") # get a container table
    dateTable=None
    #for i in table.iter():
    for i in table.getiterator():    
        if i.tag=="table" and not(i.get("id")=="browse-wrapper"): 
            dateTable=i # now we have the table we were looking for
            break
    ml=[]
    try:
        elemList=dateTable.findall("tr/td")
        #print elemList
        for el in elemList:
            a=el.find("span/a")
            addr=a.get("href")
            id=addr[addr.rfind("/")+1:]
            ml.append(id)
        ml.reverse()
    except AttributeError:
        print address
    
    return ml
    

# given a mailinglist and a date (a MMDate object) it returns a list
# of message from the mailig list arrived at the date
def getMessageListForDate(maillist, date):
    messageList=[]
    if date.messageCounter<=100:
        # less than 100 messages, means we have to scan only one page
        messageList=getMessageListInPage(maillist, date.getDate())
    else:
        # more than 100 messages, means we have to scan more pages
        n=math.ceil(date.messageCounter/100.0)
        for page in range(1,int(n+1)):
            messageList.extend(getMessageListInPage(maillist, date.getDate(), page))  
    return messageList

# get all the message id for the give list
def getAllMessageIDForMailingList(maillist, threadsNumb=25):
    global foundLast
    # get all the date pages
    dateList=getDateList(maillist)
    messList=[]
    q=Queue.Queue()
    #get the last message in couchdb
    lastId=cdb.getLastEmailKey(maillist)
    # get all the messages relatives to the first date
    t=GetIDs(maillist, dateList[0], q, messList, lastId)
    t.start()
    q.put(t)
    q.join()
    # get all the rest using threads
    if not(foundLast):
        for i in range(1,len(dateList),threadsNumb):
            for j in range(0,threadsNumb):
                if i+j>=len(dateList):
                    break
                else:
                    t=GetIDs(maillist, dateList[i+j], q, messList, lastId)
                    t.start()
                    q.put(t)
            q.join()
            
    print "Messages to be downloaded: "+str(len(messList))
    return messList


#l=getDateList("org.freenetproject.devl")
#for i in l:
#    print str(i)
#l=getMessageListForDate("org.freenetproject.devl", MMDate("2000 May", 773))
#print l
#l=getAllMessageIDForMailingList("org.freenetproject.devl")
#print l

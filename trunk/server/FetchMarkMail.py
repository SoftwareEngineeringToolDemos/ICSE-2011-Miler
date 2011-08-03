# Python script used to fetch emails form MarkMail

import sys
import urllib2 as urllib
import json
from xml.etree import ElementTree

from EmailParser import MailMessage

# Some constants
url = "http://markmail.org/results.xqy?q="
urlMessage = "http://markmail.org/message.xqy?"

# get the number of pages given the mailinglist to search
def getNumPages(maillist):
    param='list:'+maillist
    get = urllib.urlopen(url+"%s&mode=json" % param) #open connection
    jsonStringResponse = get.read() #get request
    jsonResponse = json.loads(jsonStringResponse, strict=False) #string->json
    # strict=False is needed: http://bugs.python.org/issue4785
    numpages=int(jsonResponse['search']['numpages'])
    return numpages

# get the array of mails from a page of the mailing list
def getMailsInPageList(maillist, page):
    param='list:'+maillist
    #print url+"%s&page=%d&mode=json" % (param, page)
    get = urllib.urlopen(url+"%s&page=%d&mode=json" % (param, page)) #open connection
    jsonStringResponse = get.read() #get request
    jsonResponse = json.loads(jsonStringResponse, "utf-8", strict=False) #string->json
    # strict=False is needed: http://bugs.python.org/issue4785
    return jsonResponse['search']['results']['result']

# get id and thread-id associated with all mails
# return two array, fist containing id, second with the thread id  
def getIDandThread(maillist, page):
    results=getMailsInPageList(maillist, page)
    IDs=[]
    threadIDs=[]
    for r in results:
        IDs.append(r['id'])
        threadIDs.append(r['thread_id'])
    #print "id:"+str(IDs)
    #print "thread:"+str(threadIDs)
    return IDs, threadIDs

# return the message with the specified id as two XML element
# the table containing headers and the body
def getXMLMessage(messID, maxTimeout=20):
    param="id="+messID
    #print urlMessage+"%s" % param
    try:
        get = urllib.urlopen(urlMessage+"%s" % param, None, 10) #open connection
        text=get.read()
        text=text.replace("<br/>", "\n");
        elem = ElementTree.XML(text)
        tree=ElementTree.ElementTree(elem)
        headers=tree.find("content/div/table")
        body=tree.find("content/div/div")
        return headers, body
    except IOError:
        if maxTimeout==0:
            print "timed out for "+str(messID)
            return None, None
        else:
            return getXMLMessage(messID, maxTimeout-1)

# add the > sign to quoted elements
def addQuoting(elem):
    for j in range (1, 20):
        if ("class" in elem.attrib.keys()) and (elem.attrib["class"]== ("quote quote-%d" % j)):
            return ">"
    return ""

# Creates a text iterator. Does the same thing of Element.itertext()
# this function is intended to be used only in python version < 2.7
# where the function Element.itertext() is not present 
def textiterator(elem):
    if elem.text:
        yield elem.text
    for e in elem:
        for s in textiterator(e):
            yield s
        if e.tail:
            yield e.tail

# get the text content of the element 
def getTextFromInnerElements(element):
    if sys.version_info >= (2, 7):
        iter=element.itertext()
    else:
        iter=textiterator(element)
    s=""
    for i in iter:
        s=s+i
    return s

# extract a dictionary of headers form the XML header table 
def extractHeaders(headers):
    hds= {}
    #extract all tr element s in table
    if sys.version_info >= (2, 7):
        lines = list(headers.iter("tr"))
    else:
        lines = list(headers.getiterator("tr"))
    #extract everything which is inside
    for l in lines:
        title=l.find("th")
        content=l.find("td")
        hds[title.text]= getTextFromInnerElements(content)
    return hds

# replace the newline in text with the content in "\n"+quote
def replaceNewLine(quote, text):
    return text.replace("\n", "\n"+quote)

# extract the content inside the body
# (recursive)
def extractInnerBody(inner):
    # how many sub element?
    num=len(list(inner))
    if num==0:
        return inner.text  
    if not(inner.tag=='div'):
        return getTextFromInnerElements(inner)
    else:
        # add quote (">")
        m=""
        for sub in inner:
            quote=addQuoting(sub)
            m=m+quote+replaceNewLine(quote, extractInnerBody(sub))
        return m

# extract the body's content from the XML body
def extractBody(body):
    m=""
    for sub in body:
        m=m+extractInnerBody(sub)
    return m

# return the mail message with the specified (markmail) id and thread_id
# for the given mailing-list
def getMailMessage(messID, maillist):
    # get message
    headers, body = getXMLMessage(messID)
    if headers==None or body==None:
        return None
    # extract headers
    headDict=extractHeaders(headers)
    # extract body
    bodyMess=extractBody(body)
    bodyMess.encode("utf-8")
    # create mail message
    email = MailMessage(maillist)
    email.key = messID
    for k,value in headDict.iteritems():
        email.header.append(k+value)
    email.finishedHeaders=True
    email.body=bodyMess
    return email

#test
#print getNumPages('org.w3.public-lod')
#IDs, threadIDs = getIDandThread('org.w3.public-lod', 1)
#print IDs
#print threadIDs
#headers, body = getXMLMessage(IDs[0])
#headDict=extractHeaders(headers)
#print headDict
#print "---------------------------"
#bodyMess=extractBody(body)
#bodyMess.encode("utf-8")
#print bodyMess


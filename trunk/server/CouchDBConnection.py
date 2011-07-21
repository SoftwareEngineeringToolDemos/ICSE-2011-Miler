# Used to connect to couchdb and insert new arrived e-mails
import sys
import json
import couchdb
import EmailParser

CouchDBServer="http://localhost:5984"
namePrefix="at(remail)"

#get a valid databse name: replace @ and . with, repectively - and _
def getValidDBName(name):
    valid=name.replace(".","_")
    valid=valid.replace("@","-")
    return namePrefix+valid


# create a database, if already exists do nothing
def createDatabase(databaseName):
    try:
        databaseName=getValidDBName(databaseName)
        server=couchdb.Server(CouchDBServer)
        server.create(databaseName)
        print "Database '"+databaseName+"' created"
    except couchdb.http.PreconditionFailed:
        #already exists
        print "Database '"+databaseName+"' already exists"

#create a document from a MailMessage
def createDocumentFromMail(mail):
    doc = {'key': mail.key, 'header': str(mail.header), 'body': mail.body }
    return doc

#check if an email already exist in couchdb
#return True if exists, False otherwise
def checkMailExists(database, mail):
    map_fun = '''function(doc) { if(doc.key=="'''+str(mail.key)+'''") emit(doc); }'''
    #print map_fun
    results=database.query(map_fun)
    if(len(results)==0):
        return False
    else:
        return True

#save a mail message into couchdb
def saveMailCouchdb(mail):
    server=couchdb.Server(CouchDBServer)    
    createDatabase(mail.mailingList)
    database=server[getValidDBName(mail.mailingList)]    
    if not(checkMailExists(database, mail)):
        doc=createDocumentFromMail(mail)
        database.save(doc)
        print "Mail "+mail.key+" stored in "+mail.mailingList
    else:
        print "Mail "+mail.key+" already present in "+mail.mailingList
    
#stores an array or emails in couch db
#every emil is stored in the database dependins on its name
def saveListOfMailCouchdb(listMail):
    for mail in listMail:
        saveMailCouchdb(mail)

#tests
#mail = EmailParser.MailMessage("devl@freenetproject.org")
#mail.key="2"
#mail.header=["id: 2", "type: test"]
#mail.body="Hello"
#saveMailCouchdb(mail)
       

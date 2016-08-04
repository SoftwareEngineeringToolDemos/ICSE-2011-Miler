# Used to connect to couchdb and insert new arrived e-mails
import sys
import time
import json
import couchdb
import couchdb.design 
import EmailParser

# Constants
CouchDBServer="http://localhost:5984"
namePrefix="at(remail)"

# get a valid databse name: replace @ and . with, repectively - and _
def getValidDBName(name):
    valid=name.replace(".","_")
    valid=valid.replace("@","-")
    return namePrefix+valid

# create LastInserted document and key view:
def createUsefulDoc(databaseName, serverNamePort=CouchDBServer):
    server=couchdb.Server(serverNamePort)
    db=server[databaseName]
    # create last inserted
    doc={'_id': 'lastEmail', 'key':''}
    db.save(doc)
    # create keyview
    view=couchdb.design.ViewDefinition('keyview', 'keyview', '''function(doc){emit(doc._id, doc.key)}''')
    view.sync(db)
    print " desing created"

# create a database, if already exists do nothing
def createDatabase(databaseName, serverNamePort=CouchDBServer):
    if sys.version_info >= (2, 7):
        try:
            databaseName=getValidDBName(databaseName)
            server=couchdb.Server(serverNamePort)
            server.create(databaseName)
            createUsefulDoc(databaseName, serverNamePort)
            print "Database '"+databaseName+"' created"
        except couchdb.http.PreconditionFailed:
            pass
            #already exists
            #print "Database '"+databaseName+"' already exists"
    else:
        try:
            databaseName=getValidDBName(databaseName)
            server=couchdb.Server(serverNamePort)
            server.create(databaseName)
            createUsefulDoc(databaseName, serverNamePort)
            print "Database '"+databaseName+"' created"
        except couchdb.PreconditionFailed:
            pass
            #already exists
            #print "Database '"+databaseName+"' already exists"

# create a document from a MailMessage
def createDocumentFromMail(mail):
    doc = {'key': mail.key, 'header': str(mail.header), 'body': mail.body }
    return doc

# check if an email already exist in couchdb
# return True if exists, False otherwise
def checkMailExists(database, mail):
    #map_fun = '''function(doc) { if(doc.key=="'''+str(mail.key)+'''") emit(doc); }'''
    #print map_fun
    #results=database.query(map_fun)
    #if(len(results)==0):
    #    return False
    #else:
    #    return True
    v=database.view('_design/keyview/_view/keyview')
    if v.total_rows==0:
        return False
    for row in v.rows:
        if(row.value == mail.key):
            return True
    return False

# save a mail message into couchdb
def saveMailCouchdb(mail, serverNamePort=CouchDBServer):
    server=couchdb.Server(serverNamePort)    
    createDatabase(mail.mailingList, serverNamePort)
    try:
        database=server[getValidDBName(mail.mailingList)] 
    except: #server error retry
        print "server error"
        time.sleep(1)
        saveMailCouchdb(mail, serverNamePort)
    if not(checkMailExists(database, mail)):
        doc=createDocumentFromMail(mail)
        try:
            database.save(doc)
        except AttributeError:
            print "  Database.save not found"
            database.create(doc)
        #print "Mail "+mail.key+" stored in "+mail.mailingList
    else:
        print "Mail "+mail.key+" already present in "+mail.mailingList

# save a mail message into couchdb without check for duplicates
# use only if you know exactly what you are doing
def unsafeSaveMailCouchdb(mail, serverNamePort=CouchDBServer):
    server=couchdb.Server(serverNamePort)    
    createDatabase(mail.mailingList, serverNamePort)
    try:
        database=server[getValidDBName(mail.mailingList)] 
    except: #server error retry
        print "server error"
        time.sleep(1)
        unsafesaveMailCouchdb(mail, serverNamePort)
    doc=createDocumentFromMail(mail)
    try:
        database.save(doc)
    except AttributeError:
        print "  Database.save not found"
        database.create(doc)
    
# update the given document in the database identified by maillist
# to be used to update the lastEmail document
def updateDocCouchDB(doc, maillist, serverNamePort=CouchDBServer):
    server=couchdb.Server(serverNamePort) 
    database=server[getValidDBName(maillist)] 
    doc2=database.get(doc["_id"])
    database.delete(doc2)
    database.save(doc)
    #print " updated"

# return the key value of the LastEmail document in the
# database identified by maillist
def getLastEmailKey(maillist, serverNamePort=CouchDBServer):
    try:
        server=couchdb.Server(serverNamePort) 
        database=server[getValidDBName(maillist)]
        doc=database.get('lastEmail')
        return doc["key"]
    except AttributeError: #the doc is None
        return ''
    except couchdb.http.ResourceNotFound:
        return ''

# stores an array or emails in couch db
# every emil is stored in the database dependins on its name
def saveListOfMailCouchdb(listMail, serverNamePort=CouchDBServer):
    for mail in listMail:
        saveMailCouchdb(mail, serverNamePort)

#tests
#mail = EmailParser.MailMessage("devl@freenetproject.org")
#mail.key="2"
#mail.header=["id: 2", "type: test"]
#mail.body="Hello"
#saveMailCouchdb(mail)
       

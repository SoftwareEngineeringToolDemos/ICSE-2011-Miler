# Used to connect to couchdb and insert new arrived e-mails
import sys
import time
import json
import couchdb
import couchdb.design 
import EmailParser

# Constants
CouchDBServer="http://localhost:5988"
namePrefix="at(remail)"

# get a valid databse name: replace @ and . with, repectively - and _
def getValidDBName(name):
    valid=name.replace(".","_")
    valid=valid.replace("@","-")
    return namePrefix+valid

# create LastInserted document and key view:
def createUsefulDoc(databaseName):
    server=couchdb.Server(CouchDBServer)
    db=server[databaseName]
    # create last inserted

    # create keyview
    #doc={'_id': 'desing_1/keyview', 'views': {'keyview':{'map':"function(doc) { emit(null, doc.key)}"}}}
    #db.save(doc)
    view=couchdb.design.ViewDefinition('keyview', 'keyview', '''function(doc){emit(doc._id, doc.key)}''')
    view.sync(db)
    print " desing created"

# create a database, if already exists do nothing
def createDatabase(databaseName):
    if sys.version_info >= (2, 7):
        try:
            databaseName=getValidDBName(databaseName)
            server=couchdb.Server(CouchDBServer)
            server.create(databaseName)
            createUsefulDoc(databaseName)
            print "Database '"+databaseName+"' created"
        except couchdb.http.PreconditionFailed:
            pass
            #already exists
            #print "Database '"+databaseName+"' already exists"
    else:
        try:
            databaseName=getValidDBName(databaseName)
            server=couchdb.Server(CouchDBServer)
            server.create(databaseName)
            createUsefulDoc(databaseName)
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
def saveMailCouchdb(mail):
    server=couchdb.Server(CouchDBServer)    
    createDatabase(mail.mailingList)
    try:
        database=server[getValidDBName(mail.mailingList)] 
    except: #server error retry
        print "server error"
        time.sleep(1)
        saveMailCouchdb(mail)
    if not(checkMailExists(database, mail)):
        doc=createDocumentFromMail(mail)
        try:
            database.save(doc)
        except AttributeError:
            print "  Database.save not found"
            database.create(doc)
        #print "Mail "+mail.key+" stored in "+mail.mailingList
    #else:
        #print "Mail "+mail.key+" already present in "+mail.mailingList
    
# stores an array or emails in couch db
# every emil is stored in the database dependins on its name
def saveListOfMailCouchdb(listMail):
    for mail in listMail:
        saveMailCouchdb(mail)

#tests
#mail = EmailParser.MailMessage("devl@freenetproject.org")
#mail.key="2"
#mail.header=["id: 2", "type: test"]
#mail.body="Hello"
#saveMailCouchdb(mail)
       

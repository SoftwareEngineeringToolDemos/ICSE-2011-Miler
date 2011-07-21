# Small POP client to connect to a POP server and get the e-mails
import sys
import poplib
import yaml

import EmailParser
import CouchDBConnection

# some constants
fileConfigLocation = "mailinglist.config" 

#check if the given line could belong to a mailing list
def checkLineInMaillist(line, maillist):
    if line.startswith("From:"):
        return not(line.find(maillist)==-1)
    elif line.startswith("To:"):
        return not(line.find(maillist)==-1)
    elif line.startswith("Delivered-To:"):
        return not(line.find(maillist)==-1)
    else:
        return False

# Read the configuration file
config = yaml.load(open(fileConfigLocation, 'r'))

# use all accounts
for account in config['accounts']:
    #collect some information
    username = account['username']
    password = account['password']
    server = account['server']
    port = int(account['port'])
    maillists = account['mailing_lists']

    #print username
    #print password
    #print server
    #print port
    #print maillists
    #print " "

    #connects to the mail server and retrieves new emails
    print "Connecting to "+username+" at "+server+" ..."
    mail=poplib.POP3_SSL(server, port)
    print " "+mail.getwelcome()
    mail.user(username)
    mail.pass_(password)
    numMess=len(mail.list()[1])
    print " Found "+str(numMess)+" messages"

    #get the messages
    mails = [] #empty array where messages will be stored
    for i in range(numMess):
        s="" #string containing a message
        append=False
        db="" #the maillist the mail belong to
        for j in mail.retr(i+1)[1]:
            s=s+j+"\n"
            #check if the message belong to the mailing list
            for m in maillists:
                if checkLineInMaillist(j, m['name']):
                    db=m['name']
                    append=True
        if(append):
            mails.append(EmailParser.MapMessageMailList(db, s))

    #close the connection
    mail.quit()

    #convert the mail as text in a nicer object
    emailsObjects = EmailParser.convertTextArrayToMailArray(mails)
    if(len(emailsObjects)==0):
        print " No relevant mails found"
    else:
        print "Store in database"
        CouchDBConnection.saveListOfMailCouchdb(emailsObjects)


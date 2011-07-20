# Small POP client to connect to a POP server and get the e-mails
import sys
import poplib
import EmailParser
import getMailList
import CouchDBConnection

server= getMailList.getServer();
port= getMailList.getPort();
username= getMailList.getUsername();
password= getMailList.getPassword();

mail = poplib.POP3_SSL(server, port)
print mail.getwelcome()
mail.user(username)
mail.pass_(password)
numMessages = len(mail.list()[1])
mails = [] #empty array of e-mails

listMailList=getMailList.getListOfMailinglist();

for i in range(numMessages):
    #print "New Mail:"
    s=""
    append=False
    for j in mail.retr(i+1)[1]:
        #print "  "+j
        s=s+j+"\n"
        #check if the message belong to the mailing list
        for mailList in listMailList:
            if (j.startswith("From:") and (not(j.find(mailList)==-1))) or (j.startswith("To:") and (not(j.find(mailList)==-1))):
                append=True
    if(append):    
        mails.append(MapMessageMailList(mailList, s))
mail.quit()

print "Finished fetching e-mails"
#print mails
#for m in mails:
#    print m+"\n"

print "Create E-Mail object"
emailsObjects = EmailParser.convertTextArrayToMailArray(mails)
cont=1
for em in emailsObjects:
    print "EMail "+str(cont)+":"
    print " - Mailing List:"+em.mailingList
    print " - Key:"+em.key
    print " - Headers:"
    print " "+str(em.header)
    print " - Body:"
    print " "+em.body
    cont=cont+1

print "Store in database"
CouchDBConnection.saveListOfMailCouchdb(emailsObjects)

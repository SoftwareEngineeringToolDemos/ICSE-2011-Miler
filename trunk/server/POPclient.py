# Small POP client to connect to a POP server and get the e-mails
import sys
import poplib
import EmailParser
import getMailList

#username = "remail@sback.it"
#password = "randomvariables"
#mailList = "devl@freenetproject.org"
#server = "pop.gmail.com" #"mail.sback.it"
#port=995

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
        for maiList in listMailList:
            if (j.startswith("From:") and (not(j.find(mailList)==-1))) or (j.startswith("To:") and (not(j.find(mailList)==-1))):
                append=True
    if(append):    
        mails.append(s)
mail.quit()

print "Finished fetching e-mails"
#print mails
#for m in mails:
#    print m+"\n"

print "Create E-Mail object"
emailsObjects = EmailParser.convertTextArrayToMailArray(mails)
cont=1
for em in emailsObjects:
    print " - Key:"
    print " "+em.key
    print "EMail "+str(cont)+":"
    print " - Headers:"
    print " "+str(em.header)
    print " - Body:"
    print " "+em.body
    cont=cont+1

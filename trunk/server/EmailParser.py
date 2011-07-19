# It takes an array of e-mails and return an array of Mail Class that
# can be stored in couchdb

import sys

# A class defining a mail message 
class MailMessage:
    #construct an empty message to be filled
    def __init__(self):
        self.finishHeaders=False
        self.key=""
        self.header=[]
        self.body=""

    #create a nicely formatted Mail message out of an unformatted message
    def createMessage(self, rawMessage):
        #split the raw message in lines
        lines=rawMessage.split("\n")
        for line in lines:
            self.addLine(line)

    #add a line to the message
    def addLine(self, line):
        if line=="" and self.finishHeaders==False: 
            #first blank line -> end of headers and start of body
            self.finishHeaders=True
        elif self.finishHeaders==False:
            #add the line to the headers
            self.header.append(line)
            #check if it is the Message-ID:
            if line.startswith("Message-ID:"):
                self.key=line
        else:
            self.body=self.body+"\n"+line
#END CLASS

# function to convert a text in a Mail class
def convertTextToMail(text):
    mail = MailMessage()
    mail.createMessage(text)
    return mail

# function to convert an array of texts into an array of mail messages
def convertTextArrayToMailArray(array):
    mailList=[]
    for text in array:
        mail=convertTextToMail(text)
        mailList.append(mail)
    return mailList

# It takes an array of e-mails and return an array of Mail Class that
# can be stored in couchdb

import sys

# A class defining a mail message 
class MailMessage:
    #construct an empty message to be filled
    def __init__(self, mailingList):
        self.mailingList=mailingList #the mailing list to which the e-mail belongs to 
        self.finishHeaders=False #tells if the headers are finished
        self.key="" #is the Message-ID wich is used as identifier
        self.header=[] #an array containing all the headers
        self.body="" #the body of the message

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
            if line.startswith("Message-ID:") or line.startswith("Message-Id:"):
                self.key=line
        else:
            self.body=self.body+"\n"+line
#END CLASS

#a class mapping a raw mail message to a mailing list
class MapMessageMailList:
    def __init__(self, mailingList, message):
        self.mailingList=mailingList
        self.message=message
#END CLASS
        

# function to convert a text in a Mail class
def convertTextToMail(text):
    mail = MailMessage(text.mailingList)
    mail.createMessage(text.message)
    return mail

# function to convert an array of MapMessageMailList into an array of mail messages
def convertTextArrayToMailArray(array):
    mailList=[]
    for text in array:
        mail=convertTextToMail(text)
        mailList.append(mail)
    return mailList

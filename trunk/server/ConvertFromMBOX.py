# This should read an mbox file, parse it
# and store it into the database

import sys
import json
import couchdb

try:
    import jsonlib2 as json # much faster then Python 2.6.x's stdlib
except ImportError:
    import json

# class defining a message read from MBOX file    
class Message:
	# constructor
	def __init__(self, key):
		self.startMessage=key
		self.messageHeader=[]
		self.messageBody=[]
		self.firstBlankLine=False
	# add line to the messageBody or to the messageHeader of the message
	def addLine(self, line):
		if self.firstBlankLine:
			self.messageBody.append(line)
		else:
			if not line=="\n":
				self.messageHeader.append(line)
			else:
				self.firstBlankLine=True			
#END CLASS

# check if the line's read is a new one
def isNewMessage(line, oldLine):
	return (line.startswith("From ") and (oldLine=="\n" or oldLine==""))

# class defining a Message Document
# to be added/read easily to/from the database
from couchdb.mapping import Document
from couchdb.mapping import TextField
class MessageDocument(Document):
	key=TextField()
	header = TextField()
	body = TextField()
#END CLASS

# add a message to the database
def addMessageToDatabase(msg):
	
	# encode the text in JSON
	jsonHeaderEncoded=json.dumps(msg.messageHeader)
	jsonMessageEncoded=''.join(msg.messageBody)
	
	emailMessage=MessageDocument(header=jsonHeaderEncoded, body=jsonMessageEncoded, key=msg.startMessage)
	emailMessage.store(db)

# server where couchdb is
try:
	servName=sys.argv[3]
except IndexError:
	servName='http://localhost:5984/'
server = couchdb.Server(servName)
# create the database on the server
# name passed as second parameter
print sys.argv[2]
db = server.create(sys.argv[2])	
print "Database created"

#file passed as argument
MBOX = sys.argv[1]
# open file
print "Opening mbox"
mboxFile=open(MBOX, 'r')
msg=None 
oldLine=""
numMessages=0
for line in mboxFile:
	line=line.decode('utf-8', 'ignore')
	if isNewMessage(line, oldLine):
		if msg is None:
			# add to the database
			msg=Message(line)
		else:
			# add the message to the database
			addMessageToDatabase(msg)
			numMessages=numMessages+1
			if((numMessages%10)==0):
				print(str(numMessages)+' messagges added')
			# create a new message
			msg=Message(line)				
	else:
		msg.addLine(line)
	oldLine=line		
# add the last message to the database
addMessageToDatabase(msg)
print(str(numMessages)+'messagge added\n')
print("Finished Importing!")

# Read the mail list configuration and get the list of mailing list
import sys
#import bz2

fileConfigurationPath="mailinglist.config"

#get the list of mailing list
def getListOfMailinglist():
    maillist=[]
    file = open (fileConfigurationPath, 'r')
    #read line by line
    for line in file:
        #skip comments and blank line
        if not(line.startswith("#") or line.startswith("-") or line=="\n"):
            maillist.append(line)
    return maillist

#get the server address
def getServer():
    maillist=[]
    server=""
    file = open (fileConfigurationPath, 'r')
    #read line by line
    for line in file:
        if line.startswith("-SERVER:"):
            server=line.replace("-SERVER: ","");
            server=server.strip()
            break
    return server

#get the server port
def getPort():
    maillist=[]
    port=""
    file = open (fileConfigurationPath, 'r')
    #read line by line
    for line in file:
        if line.startswith("-PORT:"):
            port=line.replace("-PORT: ","");
            port=port.strip()
            break
    return int(port)

#get username
def getUsername():
    maillist=[]
    user=""
    file = open (fileConfigurationPath, 'r')
    #read line by line
    for line in file:
        if line.startswith("-USERNAME:"):
            user=line.replace("-USERNAME: ","");
            user=user.strip()
            break
    return user

#get password
def getPassword():
    maillist=[]
    passwd=""
    file = open (fileConfigurationPath, 'r')
    #read line by line
    for line in file:
        if line.startswith("-PASSWORD:"):
            passwd=line.replace("-PASSWORD: ","");
            passwd=passwd.strip()
            break
    #passwd=bz2.decompress(passwd)
    return passwd

#print "SERVER:"
#print getServer()
#print "PORT:"
#print getPort()
#print "USERNAME:"
#print getUsername()
#print "PASSWORD:"
#print getPassword()
#print "MAILING LISTS:"
#print getListOfMailinglist()

# Read the mail list configuration and get the list of mailing list
import sys

fileConfigurationPath="mailinglist.config"

def getListOfMailinglist():
    maillist=[]
    file = open (fileConfigurationPath, 'r')
    #read line by line
    for line in file:
        #skip comments and blank line
        if not(line.startswith("#") or line=="\n"):
            maillist.append(line)
    print "Mailinglists:"
    print maillist
    return maillist


#getListOfMailinglist()

#!/usr/bin/env python

import sys
import subprocess
import yaml

try:
	import pygtk
	#tell pyGTK, if possible, that we want GTKv2
	pygtk.require("2.0")
except:
    #Some distributions come with GTK2, but not pyGTK
	pass
try:
  	import gtk
  	import gtk.glade
except:
	print "You need to install pyGTK or GTKv2 ",
	print "or set your PYTHONPATH correctly."
	print "try: export PYTHONPATH=",
	print "/usr/local/lib/python2.2/site-packages/"
	sys.exit(1)
	

# convert a list of mailing list in a list of string
def convert_MailList_to_String(mailList):
	   strings=[]
	   for s in mailList:
	   		 strings.append(s["name"])
	   return strings

# class responsible to create and handle the gui
class appgui:
	def __init__(self):
		gladefile="REmailConfigurator.glade"
		self.interface =gtk.glade.XML(gladefile)

		#defines the callbacks
		self.dic = { 
			"on_importMBox_clicked" : self.importMBox_clicked,
			"on_importMarkMail_clicked": self.importMarkMail_clicked,
			"on_EditConfFile_clicked": self.createEditWindow,
			"on_checkEmails_clicked":self.checkEmails_clicked,
			"on_checkDaemon_clicked": self.checkDaemon_clicked,
			"on_REmailConfigurator_destroy" : self.on_REmailConfigurator_destroy,
			"on_accountList_changed": self.comboChanged,
			"on_saveChanges_clicked":self.saveConfiguration,
			 "on_exit_clicked":self.destroyChild
		}		
		self.interface.signal_autoconnect( self.dic )

		self.main_window = self.interface.get_widget('REmailConfigurator')
		self.main_window.show()
	
	# handler for colosing the window
	def on_REmailConfigurator_destroy(self, data):
		print "Goodbye"
		gtk.main_quit()

	# handler for the import button for MBox
	def importMBox_clicked(self, data):
		print "import from MBox"
		serverAddress=self.interface.get_widget("serverMBox").get_text()
		print "  -- server: "+serverAddress
		databaseName=self.interface.get_widget("databaseNameMBox").get_text()
		print "  -- database:"+databaseName
		filePath=self.interface.get_widget("fileNameMBox").get_filename()
		print "  --  file:'"+filePath+"'"
		#call("python ../ConvertFromMBOX.py '"+filePath+"' '"+databaseName+"' '"+serverAddress+"'", shell=True)
		command="python ../ConvertFromMBOX.py '"+filePath+"' '"+databaseName+"' '"+serverAddress+"'"
		child = subprocess.Popen("xterm -e "+command, shell=True)
		
	#handler for import button for markmail
	def importMarkMail_clicked(self, data):
		   print "import from MarkMail"
		   serverAddress=self.interface.get_widget("serverMarkMail").get_text()
		   print "  -- server: "+serverAddress
		   mailList=self.interface.get_widget("mailListMarkMail").get_text()
		   print "  -- mailList: "+mailList
		   command= "python ../FetcherThread.py '"+mailList+"' '"+serverAddress+"'"
		   child = subprocess.Popen("xterm -e "+command, shell=True)		

	#handler for check emails usign POP button
	def checkEmails_clicked(self,data):
		   print "import using POP"
		   command="python ../POPclient.py"
		   child = subprocess.Popen("xterm -e "+command, shell=True)
		
	# handler to check if the daemon to fetch email is running	
	def checkDaemon_clicked(self,data):
		   print "check daemon"
		   lock=open("../REmail/REmail_lock")
		   content=lock.read()
		   #print "'"+content.strip()+"'"
		   #open popup
		   d = gtk.Dialog()
		   d.add_buttons(gtk.STOCK_OK,1)
		   if content.strip()=="":
				 label = gtk.Label('\n The daemon is not running \n')
				 label.show()
		   else:
				 label = gtk.Label('\n The daemon is running at PID '+str(content.strip())+" \n")
				 label.show()
		   d.vbox.pack_start(label)
		   reply=d.run()
		   if(reply==1):
		   		 d.destroy()
		   		 
	#handler for creating the editor window
	def createEditWindow(self,data):
		   print "Open editor"
		   self.editor_window = self.interface.get_widget('ConfigurationEditor')
		   self.editor_window.show()
		   self.config = yaml.load(open("../mailinglist.config", 'r'))
		   #print config
		   #fill the combo box
		   accountCombo=self.interface.get_widget('accountList')
		   for account in self.config['accounts']:
		   		 accountCombo.append_text(account['username'])
		   		 
		   
	#listeners for changes on the combobox
	def comboChanged(self, data):
		   accountSelected=self.interface.get_widget('accountList').get_active_text()
		   if not(accountSelected=="-- New --"):
		   		 for account in self.config['accounts']:
		   		 	    #print "'"+account['username']+"' '"+accountSelected+"'"
		   		 	    if accountSelected==account['username']:
		   		 	    		 self.interface.get_widget('accName').set_text(account['username'])
							 self.interface.get_widget('accPassword').set_text(account['password'])
							 self.interface.get_widget('emailServ').set_text(account['server'])
							 self.interface.get_widget('emailPort').set_text(str(account['port']))
							 self.interface.get_widget('remailServ').set_text(account['remail-server'])
							 self.interface.get_widget('remailPort').set_text(str(account['remail-port']))
							 textBuffer=self.interface.get_widget('listMailingList').get_buffer() # convert_MailList_to_String(account['mailing_lists']))
							 textArr=convert_MailList_to_String(account['mailing_lists'])
							 text=""
							 first=True
							 for t in textArr:
							 	    if first:
							 	    		  text=t
							 	    		  first=False
							 	    else:
							 	    		  text=text+"\n"+t
							 textBuffer.set_text(text)
		   else:
		  		self.interface.get_widget('accName').set_text("")
				self.interface.get_widget('accPassword').set_text("")
				self.interface.get_widget('emailServ').set_text("")
				self.interface.get_widget('emailPort').set_text("")
				self.interface.get_widget('remailServ').set_text("")
				self.interface.get_widget('remailPort').set_text("")
				self.interface.get_widget('listMailingList').get_buffer().set_text("")	   
	
	#utility function to set the account
	def _setAccount(self,account):
		   account['username']=self.interface.get_widget('accName').get_text()
		   account['password']=self.interface.get_widget('accPassword').get_text()
		   account['server']=self.interface.get_widget('emailServ').get_text()
		   account['port']=self.interface.get_widget('remailPort').get_text()
		   account['remail-server']=self.interface.get_widget('remailServ').get_text()
		   account['remail-port']=self.interface.get_widget('remailPort').get_text()
		   buff=self.interface.get_widget('listMailingList').get_buffer()
		   text=buff.get_text(buff.get_start_iter(), buff.get_end_iter())
		   arr=text.split("\n")
		   account['mailing_lists']=[] #reset
		   for s in arr:
		   		 account['mailing_lists'].append({"name":s})
		   #print account['mailing_lists']
	
	#save the configuration file
	def saveConfiguration(self, data):	   		
		   print "saving config..."
		   newAccount=True
		   usr=self.interface.get_widget('accName').get_text()
		   for account in self.config['accounts']:		   		
		   		 if(usr==account['username']):
		   		 	    #edit the account
		   		 	    newAccount=False
		   		 	    self._setAccount(account)
		   if newAccount:
		   		 account={'username':'','password':'','server':'','port':'','remail-server':'','remail-port':'','mailing_lists':''}
		   		 self.config['accounts'].append(account)
		   		 self._setAccount(account)
		   stream = file('../mailinglist.config', 'w')		 
		   yaml.dump(self.config, stream)
		   		 	    
	#handler to destroy the editor window	   
	def destroyChild(self, data):
		   self.editor_window.hide()
		   
		

# we start the app like this...
app=appgui()
gtk.mainloop()

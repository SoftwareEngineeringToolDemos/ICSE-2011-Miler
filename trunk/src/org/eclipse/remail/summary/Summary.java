package org.eclipse.remail.summary;

import org.eclipse.remail.Mail;

public class Summary {
	
	private Mail mail;
	private String summary;
	
	public Summary(Mail mailToSumm){
		mail = mailToSumm;
		summary = summUpMail();
	}
	
	
	private String summUpMail(){
		FeaturesExtractor fe = new FeaturesExtractor(mail);
		return mail.getText();
	}
	
	
	public String getSummary(){
		return summary;
	}
	
	
	

}

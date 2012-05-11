package org.eclipse.remail.summary;

import org.eclipse.remail.Mail;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Summary {
	
	private Mail mail;
	private String summary;
	private MaxentTagger tagger;
	
	public Summary(Mail mailToSumm, MaxentTagger taggerMail){
		mail = mailToSumm;
		tagger = taggerMail;
		summary = summUpMail();
	}
	
	
	private String summUpMail(){
		String summary = "";
		FeaturesExtractor fe = new FeaturesExtractor(mail, tagger);
		for(int i=0; i<fe.getMailLength(); i++){
			if(fe.getRelevanceAtPosition(i) == 1.0)
				summary += fe.getSentenceAtPosition(i) + "\n";
		}
		return summary;
	}
	
	
	public String getSummary(){
		return summary;
	}
	
	
	

}

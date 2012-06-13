package org.eclipse.remail.summary;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.StringTokenizer;

import org.eclipse.remail.Mail;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class FeaturesExtractor {

	private String subject;

	// Key: id sentence (from 0). Value: sentence.
	private HashMap<Integer, String> sentencesTable;

	// Col 0: chars, Col 1: num_stopw_norm, Col 2: num_verbs_norm, Col 3: rel_pos_norm, Col 4: subj_words_norm , Col 5: relevant, Col 6: depth
	private double[][] featuresTable;

	private HashSet<String> stopwords;

	private MaxentTagger tagger;


	public FeaturesExtractor(Mail mail, MaxentTagger mailTagger){
		tagger = mailTagger;
		createStopwords();
		subject = mail.getSubject();
		createSentencesTable(mail.getText());
		createFeaturesTable();
	}


	/**
	 * Create a table storing the id (integer starting from 0) of a sentence and 
	 * the sentence itself (String).
	 * @param fullContent The content of the email.
	 */
	private void createSentencesTable(String fullContent){
		String[] contentLines = fullContent.split("\n");
		String newContent = "";
		for(int i=0; i<contentLines.length; i++){
			if(contentLines[i] != null && contentLines[i].length() > 0){
				if(contentLines[i].charAt(0) != '>' && !(contentLines[i].startsWith("On ") && contentLines[i].endsWith(" wrote:")
						&& i+1<contentLines.length && contentLines[i+1].length() > 0 && contentLines[i+1].charAt(0) == '>')){
					newContent += contentLines[i];
				}
			}
		}
		BreakIterator bi = BreakIterator.getSentenceInstance(Locale.US);
		bi.setText(newContent);
		int index = 0;
		int start = bi.first();
		sentencesTable = new HashMap<Integer, String>();
		for (int end = bi.next(); end != BreakIterator.DONE; start = end, end = bi.next()) {
			sentencesTable.put(index, newContent.substring(start,end));
			index++;
		}
	}


	/**
	 * For each sentence, compute how many characters it is composed of 
	 * and insert this value in the column 0 of the Features table.
	 */
	private void extractNumberChars(){
		for(int i=0; i<featuresTable.length; i++){
			featuresTable[i][0] = (double)sentencesTable.get(i).length();
		}
	}


	/**
	 * Add to the set stopwords all the English stopwords.
	 */
	private void createStopwords(){
		stopwords = new HashSet<String>();
		stopwords.add("a");
		stopwords.add("about");
		stopwords.add("above");
		stopwords.add("after");
		stopwords.add("again");
		stopwords.add("against");
		stopwords.add("all");
		stopwords.add("am");
		stopwords.add("an");
		stopwords.add("and");
		stopwords.add("any");
		stopwords.add("are");
		stopwords.add("as");
		stopwords.add("at");
		stopwords.add("be");
		stopwords.add("because");
		stopwords.add("been");
		stopwords.add("before");
		stopwords.add("being");
		stopwords.add("below");
		stopwords.add("between");
		stopwords.add("both");
		stopwords.add("but");
		stopwords.add("by");
		stopwords.add("can");
		stopwords.add("did");
		stopwords.add("do");
		stopwords.add("does");
		stopwords.add("doing");
		stopwords.add("don");
		stopwords.add("down");
		stopwords.add("during");
		stopwords.add("each");
		stopwords.add("few");
		stopwords.add("for");
		stopwords.add("from");
		stopwords.add("further");
		stopwords.add("had");
		stopwords.add("has");
		stopwords.add("have");
		stopwords.add("having");
		stopwords.add("he");
		stopwords.add("her");
		stopwords.add("here");
		stopwords.add("hers");
		stopwords.add("herself");
		stopwords.add("him");
		stopwords.add("himself");
		stopwords.add("his");
		stopwords.add("how");
		stopwords.add("i");
		stopwords.add("if");
		stopwords.add("in");
		stopwords.add("into");
		stopwords.add("is");
		stopwords.add("it");
		stopwords.add("its");
		stopwords.add("itself");
		stopwords.add("just");
		stopwords.add("me");
		stopwords.add("more");
		stopwords.add("most");
		stopwords.add("my");
		stopwords.add("myself");
		stopwords.add("no");
		stopwords.add("nor");
		stopwords.add("not");
		stopwords.add("now");
		stopwords.add("of");
		stopwords.add("off");
		stopwords.add("on");
		stopwords.add("once");
		stopwords.add("only");
		stopwords.add("or");
		stopwords.add("other");
		stopwords.add("our");
		stopwords.add("ours");
		stopwords.add("ourselves");
		stopwords.add("out");
		stopwords.add("over");
		stopwords.add("own");
		stopwords.add("s");
		stopwords.add("same");
		stopwords.add("she");
		stopwords.add("should");
		stopwords.add("so");
		stopwords.add("some");
		stopwords.add("such");
		stopwords.add("t");
		stopwords.add("than");
		stopwords.add("that");
		stopwords.add("the");
		stopwords.add("their");
		stopwords.add("theirs");
		stopwords.add("them");
		stopwords.add("themselves");
		stopwords.add("then");
		stopwords.add("there");
		stopwords.add("these");
		stopwords.add("they");
		stopwords.add("this");
		stopwords.add("those");
		stopwords.add("through");
		stopwords.add("to");
		stopwords.add("too");
		stopwords.add("under");
		stopwords.add("until");
		stopwords.add("up");
		stopwords.add("very");
		stopwords.add("you");
		stopwords.add("your");
		stopwords.add("yours");
		stopwords.add("yourself");
		stopwords.add("was");
		stopwords.add("we");
		stopwords.add("were");
		stopwords.add("what");
		stopwords.add("when");
		stopwords.add("where");
		stopwords.add("which");
		stopwords.add("while");
		stopwords.add("who");
		stopwords.add("whom");
		stopwords.add("why");
		stopwords.add("will");
		stopwords.add("with");
	}


	/**
	 * @param stringToRemoveEnding The String where the given char must be removed.
	 * @param endingChar The ending character to be removed from the String.
	 * @return The initial String without the given character.
	 */
	private String removePunctuation(String stringToRemoveEnding, String endingChar){
		String[] ws = stringToRemoveEnding.split(".");
		String w = "";
		for(int i = 0; i<ws.length; i++){
			w += ws[i];
		}
		return w;
	}


	/**
	 * @param wordToNormalize A word where the ending characters (like ".", ",", "?", "!", ":", ";", "(", ")", "[", "]", "{", "}", ".")
	 * @return The word without the disturbing ending characters.
	 */
	private String normalizeWord(String wordToNormalize){
		while(wordToNormalize.endsWith(".") || wordToNormalize.endsWith(",") || wordToNormalize.endsWith("?") || wordToNormalize.endsWith("!") || 
				wordToNormalize.endsWith(":") || wordToNormalize.endsWith(";") || wordToNormalize.endsWith("(") || wordToNormalize.endsWith(")") ||
				wordToNormalize.endsWith("[") || wordToNormalize.endsWith("]") || wordToNormalize.endsWith("{") || wordToNormalize.endsWith("}")){
			if(wordToNormalize.endsWith(".")){		
				wordToNormalize = removePunctuation(wordToNormalize, ".");
			} else if(wordToNormalize.endsWith(",")){
				wordToNormalize = removePunctuation(wordToNormalize, ",");
			} else if(wordToNormalize.endsWith("?")){
				wordToNormalize = removePunctuation(wordToNormalize, "?");
			} else if(wordToNormalize.endsWith("!")){
				wordToNormalize = removePunctuation(wordToNormalize, "!");
			} else if(wordToNormalize.endsWith(":")){
				wordToNormalize = removePunctuation(wordToNormalize, ":");
			} else if(wordToNormalize.endsWith(";")){
				wordToNormalize = removePunctuation(wordToNormalize, ";");
			} else if(wordToNormalize.endsWith("(")){
				wordToNormalize = removePunctuation(wordToNormalize, "(");
			} else if(wordToNormalize.endsWith(")")){
				wordToNormalize = removePunctuation(wordToNormalize, ")");
			} else if(wordToNormalize.endsWith("[")){
				wordToNormalize = removePunctuation(wordToNormalize, "[");
			} else if(wordToNormalize.endsWith("]")){
				wordToNormalize = removePunctuation(wordToNormalize, "]");
			} else if(wordToNormalize.endsWith("{")){
				wordToNormalize = removePunctuation(wordToNormalize, "{");
			} else if(wordToNormalize.endsWith("}")){
				wordToNormalize = removePunctuation(wordToNormalize, "}");
			}
		}
		return wordToNormalize;
	}


	/**
	 * For each sentence, compute normalized number of stop-words the sentence contains 
	 * and insert this value in the column 0 of the Features table.
	 */
	private void extractNumberStopwordsNorm(){
		for(int i=0; i<sentencesTable.size(); i++){
			String[] words = sentencesTable.get(i).split(" ");
			int numStopws = 0;
			for(int j=0; j<words.length; j++){
				words[j] = normalizeWord(words[j].trim());
				if(stopwords.contains(words[j])){
					numStopws++;
				}
			}
			featuresTable[i][1] = (double)numStopws/(double)words.length;
		}
	}

	
	/**
	 * For each sentence, compute normalized number of verbs the sentence contains 
	 * and insert this value in the column 0 of the Features table.
	 */
	private void extractNumberVerbsNorm(){
		for(int i=0; i<sentencesTable.size(); i++){
			int numVerbs = 0;
			StringTokenizer st = new StringTokenizer(sentencesTable.get(i));
			String s = "";
			while(st.hasMoreTokens()){
				String token = st.nextToken();
				if(!token.contains("0") && !token.contains("1") && !token.contains("2") && !token.contains("3") && 
						!token.contains("4") && !token.contains("5") && !token.contains("6") && !token.contains("7") && 
						!token.contains("8") && !token.contains("9"))
					s = s + token + " ";
			}
			String[] taggedWords = tagger.tagString(s).split(" ");
			for(int j=0; j<taggedWords.length; j++){
				if(taggedWords[j].endsWith("/VBG") || taggedWords[j].endsWith("/VBD") || taggedWords[j].endsWith("/VBN") || 
						taggedWords[j].endsWith("/VBP") || taggedWords[j].endsWith("/VBZ") || taggedWords[j].endsWith("/VB"))
					numVerbs++;
			}
			featuresTable[i][2] = (double)numVerbs/(double)taggedWords.length;
		}
	}


	/**
	 * For each sentence, compute normalized relative position of the sentence in the mail
	 * and insert this value in the column 0 of the Features table.
	 */
	private void extractRelativePosNorm(){
		for(int i=0; i<sentencesTable.size(); i++){
			featuresTable[i][3] = (double)i/(double)sentencesTable.size();
		}
	}

	
	/**
	 * For each sentence, compute normalized number of words of the thread subject the sentence contains
	 * and insert this value in the column 0 of the Features table.
	 */
	private void extractSubjectWordsNorm(){
		String[] subj = subject.split(" ");
		for(int k=0; k<subj.length; k++)
			subj[k] = normalizeWord(subj[k].trim());
		for(int i=0; i<sentencesTable.size(); i++){
			String[] sent = sentencesTable.get(i).split(" ");
			int subjwords = 0;
			for(int j=0; j<sent.length; j++){
				sent[j] = normalizeWord(sent[j].trim());
				for(int s=0; s<subj.length; s++){
					if(sent[j].equals(subj[s]))
						subjwords++;
				}
			}
			featuresTable[i][4] = (double)subjwords/(double)sent.length;
		}
	}


	/**
	 * For each sentence, compute the relevance according to some machine learning conditions
	 * and insert this value in the column 0 of the Features table.
	 */
	// Col 0: chars, Col 1: num_stopw_norm, Col 2: num_verbs_norm, Col 3: rel_pos_norm, Col 4: subj_words_norm , Col 5: relevant, Col 6: depth
	private void determineRelevance(){
		for(int i=0; i<featuresTable.length;i++){
			if(featuresTable[i][1] > 0.142857){
				if(featuresTable[i][4] > 0.045455){
					if(featuresTable[i][1] > 0.484848){
						if(featuresTable[i][3] > 0.633333){
							featuresTable[i][5] = 0.0;
							featuresTable[i][6] = 4.0;
						} else {
							featuresTable[i][5] = 1.0;
							featuresTable[i][6] = 4.0;
						}
					} else {
						featuresTable[i][5] = 1.0;
						featuresTable[i][6] = 3.0;
					}
				} else {
					if(featuresTable[i][3] > 0.756757){
						featuresTable[i][5] = 0.0;
						featuresTable[i][6] = 3.0;
					} else {
						if(featuresTable[i][1] > 0.555556){
							if(featuresTable[i][3] > 0.45){
								featuresTable[i][5] = 0.0;
								featuresTable[i][6] = 5.0;
							} else {
								if(featuresTable[i][3] > 0.153856){
									featuresTable[i][5] = 1.0;
									featuresTable[i][6] = 6.0;
								} else {
									featuresTable[i][5] = 0.0;
									featuresTable[i][6] = 6.0;
								}
							}
						} else {
							featuresTable[i][5] = 1.0;
							featuresTable[i][6] = 4.0;
						}
					}
				}	
			} else {
				if(featuresTable[i][2] > 0.153846){
					if(featuresTable[i][0] > 40.0){
						featuresTable[i][5] = 0.0;
						featuresTable[i][6] = 3.0;
					} else {
						if(featuresTable[i][2] > 0.4){
							featuresTable[i][5] = 0.0;
							featuresTable[i][6] = 4.0;
						} else {
							if(featuresTable[i][3] > 0.653061){
								featuresTable[i][5] = 0.0;
								featuresTable[i][6] = 5.0;
							} else {
								featuresTable[i][5] = 1.0;
								featuresTable[i][6] = 5.0;
							}
						}
					}
				} else {
					featuresTable[i][5] = 0.0;
					featuresTable[i][6] = 2.0;
				}
			}
		}

	}


	/**
	 * Create the table with all the values of the features for each sentence inserted.
	 * Col 0: chars, Col 1: num_stopw_norm, Col 2: num_verbs_norm, Col 3: rel_pos_norm, Col 4: subj_words_norm , Col 5: relevant, Col 6: depth
	 */
	private void createFeaturesTable(){
		featuresTable = new double[sentencesTable.size()][7];
		extractNumberChars();
		extractNumberStopwordsNorm();
		extractNumberVerbsNorm();
		extractRelativePosNorm();
		extractSubjectWordsNorm();
		determineRelevance();
		for(int i = 0; i<sentencesTable.size(); i++){
			System.out.println("KEY: " + i + ", VALUE: " + sentencesTable.get(i));
//			System.out.println("CHARS: " + featuresTable[i][0]);
//			System.out.println("STOPWORDS_NORM: " + featuresTable[i][1]);
//			System.out.println("NUM_VERBS_NORM: " + featuresTable[i][2]);
//			System.out.println("REL_POS_NORM: " + featuresTable[i][3]);
//			System.out.println("SUBJ_WORDS_NORM: " + featuresTable[i][4]);
			System.out.println("RELEVANCE: " + featuresTable[i][5]);
			System.out.println("DEPTH: " + featuresTable[i][6] + "\n");
		}
	}

	public int getMailLength(){
		return sentencesTable.size();
	}

	/**
	 * @param positionSentence Id/Position of the sentence in the table.
	 * @return The sentence in String format.
	 */
	public String getSentenceAtPosition(int positionSentence){
		return sentencesTable.get(positionSentence);
	}

	
	/**
	 * @param positionSentence Id/Position of the sentence in the table.
	 * @return The relevance of that sentence.
	 */
	public double getRelevanceAtPosition(int positionSentence){
		return featuresTable[positionSentence][5];
	}
	
	
	/**
	 * @param positionSentence Id/Position of the sentence in the table.
	 * @return The condition-depth where the relevance of the sentence 
	 *         is established in the method determineRelevance()
	 */
	public double getDepthAtPosition(int positionSentence){
		return featuresTable[positionSentence][6];
	}




}

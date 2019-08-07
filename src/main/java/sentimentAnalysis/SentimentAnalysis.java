package sentimentAnalysis;

import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import javax.swing.JOptionPane;

public class SentimentAnalysis {

	public static String[] main(String[] args) {

//     String text = "A year spent in artificial intelligence is enough to make one believe in God." + 
//     		"There is no reason and no way that a human mind can keep up with an artificial intelligence machine by 2035." +  
//     		"Is artificial intelligence less than our intelligence?";
     String text = args[0];
     String [] result=new String[7];
     SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
     sentimentAnalyzer.initialize();
     
     SentimentResult sentimentResult = sentimentAnalyzer.getSentimentResult(text);
       	 result[0]=sentimentResult.getSentimentClass().getVeryPositive()+"";
	 result[1]=sentimentResult.getSentimentClass().getPositive()+"";
	 result[2]=sentimentResult.getSentimentClass().getNeutral()+"";
	 result[3]=sentimentResult.getSentimentClass().getNegative()+"";
	 result[4]=sentimentResult.getSentimentClass().getVeryNegative()+"";
         result[5]=sentimentResult.getSentimentScore()+"";
	 result[6]=sentimentResult.getSentimentType();
         return result;
	}

}

package it.ncorti.emgvisualizer.Utilities;


import java.util.LinkedList;
import java.util.List;


public class SignSentence {

    private List<String> word_seq;

    public SignSentence(){
        word_seq = new LinkedList<>();

    }

    public void appendWord(String w){
        word_seq.add(w);
    }

    public void clearWords(){
        word_seq.clear();
    }


    public String getSentenceStr(){
        StringBuilder ret = new StringBuilder();
        ret.append("");
        for (String word: word_seq) {
            ret.append(word).append(" ");
        }
        return ret.toString();
    }
}

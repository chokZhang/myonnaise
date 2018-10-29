package it.ncorti.emgvisualizer.Utilities;

public class SignWord {
    private String content;
    private int id;

    public SignWord(int id, String content){
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}

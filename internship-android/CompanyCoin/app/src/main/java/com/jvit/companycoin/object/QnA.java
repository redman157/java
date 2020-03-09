package com.jvit.companycoin.object;

public class QnA {
    private String question;
    private int icon;
    private String reply;

    public QnA(String question, int icon, String reply) {
        this.question = question;
        this.icon = icon;
        this.reply = reply;
    }

    public String getQuestion() {
        return question;
    }

    public int getIcon() {
        return icon;
    }

    public String getReply() {
        return reply;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}

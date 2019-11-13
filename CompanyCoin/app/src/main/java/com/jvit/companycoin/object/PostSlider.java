package com.jvit.companycoin.object;

import java.io.Serializable;

public class PostSlider implements Serializable {
    private int id;
    private int reactions_count;
    private int token_amount;
    private boolean reaction;
    private String avatar;
    private String name;
    private String sent_at;
    private String content;

    public PostSlider(int id, int reactions_count, int token_amount,
                      boolean reaction,
                      String avatar, String name,
                      String sent_at,
                      String content) {
        this.id = id;
        this.reactions_count = reactions_count;
        this.token_amount = token_amount;
        this.reaction = reaction;
        this.avatar = avatar;
        this.name = name;
        this.sent_at = sent_at;
        this.content = content;
    }

    public boolean isReaction() {
        return reaction;
    }

    public void setReaction(boolean reaction) {
        this.reaction = reaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReactions_count() {
        return reactions_count;
    }

    public void setReactions_count(int reactions_count) {
        this.reactions_count = reactions_count;
    }

    public int getToken_amount() {
        return token_amount;
    }

    public void setToken_amount(int token_amount) {
        this.token_amount = token_amount;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSent_at(String sent_at) {
        this.sent_at = sent_at;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public String getSent_at() {
        return sent_at;
    }

    public String getContent() {
        return content;
    }
}

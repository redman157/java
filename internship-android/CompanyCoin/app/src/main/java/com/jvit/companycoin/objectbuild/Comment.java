package com.jvit.companycoin.objectbuild;

public class Comment {
    private int id;
    private String avatar;
    private String nameAvatar;
    private String create_at;
    private String content;
    private boolean reacted;
    private int reactions_count;
    private int token_amount;

    public Comment(int id, String avatar, String nameAvatar, String create_at,
                   String content, boolean reacted,
                   int reactions_count, int token_amount) {
        this.id = id;
        this.avatar = avatar;
        this.nameAvatar = nameAvatar;
        this.create_at = create_at;
        this.content = content;
        this.reacted = reacted;
        this.reactions_count = reactions_count;
        this.token_amount = token_amount;
    }

    public boolean isReacted() {
        return reacted;
    }

    public void setReacted(boolean reacted) {
        this.reacted = reacted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setNameAvatar(String nameAvatar) {
        this.nameAvatar = nameAvatar;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReactions_count(int reactions_count) {
        this.reactions_count = reactions_count;
    }

    public void setToken_amount(int token_amount) {
        this.token_amount = token_amount;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNameAvatar() {
        return nameAvatar;
    }

    public String getCreate_at() {
        return create_at;
    }

    public String getContent() {
        return content;
    }

    public int getReactions_count() {
        return reactions_count;
    }

    public int getToken_amount() {
        return token_amount;
    }
}

package com.jvit.companycoin.objectbuild;

import com.jvit.companycoin.CommentBuilder;

public class BuildComment implements CommentBuilder {
    private int id;
    private String avatar;
    private String nameAvatar;
    private String create_at;
    private String content;
    private boolean reacted;
    private int reactions_count;
    private int token_amount;
    @Override
    public CommentBuilder id(int id) {
        this.id = id;
        return this;
    }

    @Override
    public CommentBuilder avatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    @Override
    public CommentBuilder nameAvatar(String nameAvatar) {
        this.nameAvatar = nameAvatar;
        return this;
    }

    @Override
    public CommentBuilder create_at(String create_at) {
        this.create_at = create_at;
        return this;
    }

    @Override
    public CommentBuilder content(String content) {
        this.content = content;
        return this;
    }

    @Override
    public CommentBuilder reacted(boolean reacted) {
        this.reacted = reacted;
        return this;
    }

    @Override
    public CommentBuilder reactions_count(int reactions_count) {
        this.reactions_count = reactions_count;
        return this;
    }

    @Override
    public CommentBuilder token_amount(int token_amount) {
        this.token_amount = token_amount;
        return this;
    }

    @Override
    public Comment build() {
        return new Comment(id,avatar,nameAvatar,create_at,content,reacted,reactions_count,token_amount);
    }
}

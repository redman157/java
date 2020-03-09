package com.jvit.companycoin;

import com.jvit.companycoin.objectbuild.Comment;

public interface CommentBuilder {
    CommentBuilder id(int id);
    CommentBuilder avatar(String avatar);
    CommentBuilder nameAvatar(String nameAvatar);
    CommentBuilder create_at(String create_at);
    CommentBuilder content(String content);
    CommentBuilder reacted(boolean reacted);
    CommentBuilder reactions_count(int reactions_count);
    CommentBuilder token_amount(int token_amount);

    Comment build();
}
package com.jvit.companycoin.object;

import android.text.SpannableString;

public class Notification {
    private int icon;
    private String time;
    private SpannableString messageContent;
    private String mess;

    public Notification(int icon, String time, SpannableString messageContent, String mess) {
        this.icon = icon;
        this.time = time;
        this.messageContent = messageContent;
        this.mess = mess;
    }

    public SpannableString getMessageContent() {
        return messageContent;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public void setMessageContent(SpannableString messageContent) {
        this.messageContent = messageContent;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setTime(String title) {
        this.time = title;
    }


    public int getIcon() {
        return icon;
    }

    public String getTime() {
        return time;
    }



}

package com.jvit.companycoin.object;

public class TransactionHistory {
    private int iconTitle;
    private String avatarSend;
    private String nameSend;
    private String messageSend;
    private String avatarReceive;
    private String nameReceive;
    private String date;
    private int coin;

    public TransactionHistory(int iconTitle, String avatarSend, String nameSend
            , String messageSend, String avatarReceive, String nameReceive,
                              String date, int coin) {
        this.iconTitle = iconTitle;
        this.avatarSend = avatarSend;
        this.nameSend = nameSend;
        this.messageSend = messageSend;
        this.avatarReceive = avatarReceive;
        this.nameReceive = nameReceive;
        this.date = date;
        this.coin = coin;
    }

    public void setMessageSend(String messageSend) {
        this.messageSend = messageSend;
    }

    public String getMessageSend() {
        return messageSend;
    }

    public int getIconTitle() {
        return iconTitle;
    }

    public String getAvatarSend() {
        return avatarSend;
    }

    public String getNameSend() {
        return nameSend;
    }

    public String getAvatarReceive() {
        return avatarReceive;
    }

    public String getNameReceive() {
        return nameReceive;
    }

    public String getDate() {
        return date;
    }

    public int getCoin() {
        return coin;
    }

    public void setIconTitle(int iconTitle) {
        this.iconTitle = iconTitle;
    }

    public void setAvatarSend(String avatarSend) {
        this.avatarSend = avatarSend;
    }

    public void setNameSend(String nameSend) {
        this.nameSend = nameSend;
    }

    public void setAvatarReceive(String avatarReceive) {
        this.avatarReceive = avatarReceive;
    }

    public void setNameReceive(String nameReceive) {
        this.nameReceive = nameReceive;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }
}

package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FindUserKeyword {
    @SerializedName("data")
    private List<DataUser> dataUserList;

    public List<DataUser> getDataUserList() {
        return dataUserList;
    }

    public void setDataUserList(List<DataUser> dataUserList) {
        this.dataUserList = dataUserList;
    }

    public class DataUser{
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("avatar_path")
        private String avatar_path;
        @SerializedName("email")
        private String email;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar_path() {
            return avatar_path;
        }

        public void setAvatar_path(String avatar_path) {
            this.avatar_path = avatar_path;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}

package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class UploadAvatar {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data{
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("avatar_path")
        private String avatar_path;
        @SerializedName("email")
        private String email;
        @SerializedName("team")
        private String team;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getAvatar_path() {
            return avatar_path;
        }

        public String getEmail() {
            return email;
        }

        public String getTeam() {
            return team;
        }
    }
}

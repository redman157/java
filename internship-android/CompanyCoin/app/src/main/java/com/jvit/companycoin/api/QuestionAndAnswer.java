package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionAndAnswer {
    @SerializedName("data")
    private List<QnA> qnAList;

    public List<QnA> getQnAList() {
        return qnAList;
    }

    public void setQnAList(List<QnA> listQnA) {
        this.qnAList = listQnA;
    }

    public class QnA {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        @SerializedName("id")
        private int id;
        @SerializedName("quantity")
        private String quantity;
        @SerializedName("status")
        private int status;
        @SerializedName("created_at")
        private String created_at;
        @SerializedName("updated_at")
        private String updated_at;
        @SerializedName("question")
        private String question;
        @SerializedName("answer")
        private String answer;
    }
}

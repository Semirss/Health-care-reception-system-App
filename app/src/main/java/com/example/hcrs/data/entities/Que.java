package com.example.hcrs.data.entities;

import com.google.gson.annotations.SerializedName;

public class Que {
    private Integer status;
    @SerializedName("card_id")
    private int cardId;
    @SerializedName("queue_id")
    private int queueId;
    private String date;

    // Getter for status (raw Integer value)
    public Integer getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(Integer status) {
        this.status = status;
    }

    // Getter to convert Integer to boolean
    public boolean isStatus() {
        return status != null && status == 1;
    }

    // Getter and setter for cardId
    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    // Getter and setter for queueId
    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    // Getter and setter for date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
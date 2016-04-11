package com.eurexchange.clear.frontend;


public class ClearingRequest {

    private String replyToQueue;
    private String responseQueue;
    private String key;

    public void setReplyToQueue(String replyToQueue){
        this.replyToQueue = replyToQueue;
    }

    public void setResponseQueue(String responseQueue){
        this.responseQueue = responseQueue;
    }

    public void setKey(String key){
        this.key = key;
    }

    public String getReplyToQueue(){
        return replyToQueue;
    }

    public String getResponseQueue(){
        return responseQueue;
    }
    public String getKey(){
        return key;
    }

}

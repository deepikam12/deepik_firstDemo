package com.tobaccocessation.model;

import java.io.Serializable;

/**
 * Created by Deepika.Mishra on 1/7/2019.
 */

public class NotificationModel implements Serializable {
    private String accessToken_video;
    private String accessToken_chat;

    private String roomName;

    private String method;
    private String coach_id;
    private String title;
    private  String channel_name;

    public String getRoomName ()
    {
        return roomName;
    }

    public void setRoomName (String roomName)
    {
        this.roomName = roomName;
    }

    public String getMethod ()
    {
        return method;
    }

    public void setMethod (String method)
    {
        this.method = method;
    }


    public String getCoach_id() {
        return coach_id;
    }

    public void setCoach_id(String coach_id) {
        this.coach_id = coach_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAccessToken_video() {
        return accessToken_video;
    }

    public void setAccessToken_video(String accessToken_video) {
        this.accessToken_video = accessToken_video;
    }

    public String getAccessToken_chat() {
        return accessToken_chat;
    }

    public void setAccessToken_chat(String accessToken_chat) {
        this.accessToken_chat = accessToken_chat;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }
}

package com.icandothisallday2020.ex85firebasechatting;

public class MItem {
    String name,message,time,profileUrl;
    //      닉네임,메세지,작성시간,storage 에 저장된 프로필 이미지 URL(http://...)

    public MItem() {
    }

    public MItem(String name, String message, String time, String profileUrl) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.profileUrl = profileUrl;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}

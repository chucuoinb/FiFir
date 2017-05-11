package com.example.nam.minisn.ItemListview;

/**
 * Created by Nam on 5/10/2017.
 */

public class Status {
    private int id;
    private Friend friend;
    private long time;
    private String status;
    private boolean isLike;
    private int countComment;

    public int getCountLike() {
        return countLike;
    }

    public void setCountLike(int countLike) {
        this.countLike = countLike;
    }

    private int countLike;

    public Status() {
    }

    public Status(int id,Friend friend, long time, String status, boolean isLike, int countComment,int countLike) {
        this.id = id;
        this.friend = friend;
        this.time = time;
        this.status = status;
        this.isLike = isLike;
        this.countComment = countComment;
        this.countLike=countLike;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public int getCountComment() {
        return countComment;
    }

    public void setCountComment(int countComment) {
        this.countComment = countComment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

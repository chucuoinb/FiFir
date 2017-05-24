package com.example.nam.minisn.ItemListview;

/**
 * Created by Nam on 5/24/2017.
 */

public class Comment {
    private int idComment;
    private int idUser;
    private String comment;
    private long time;
    private String username;
    public Comment() {
    }

    public Comment(int idComment, int idUser,String username, String comment, long time) {

        this.idComment = idComment;
        this.idUser = idUser;
        this.username = username;
        this.comment = comment;
        this.time = time;
    }

    public int getIdComment() {
        return idComment;
    }

    public void setIdComment(int idComment) {
        this.idComment = idComment;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

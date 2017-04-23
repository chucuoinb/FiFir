package com.example.nam.minisn.ItemListview;

import java.util.ArrayList;

/**
 * Created by Nam on 2/21/2017.
 */

public class Conversation {
    private int id;
    private String nameConservation;
    private ArrayList<Friend> listFriends;

    public Conversation() {
    }

    public String getNameConservation() {
        return nameConservation;
    }

    public void setNameConservation(String nameConservation) {
        this.nameConservation = nameConservation;
    }

    public Conversation(String nameConservation, ArrayList<Friend> friends, int id) {
        this.id = id;
        this.listFriends = friends;
        this.nameConservation=nameConservation;
    }

    public ArrayList<Friend> getListFriends() {

        return listFriends;
    }

    public void setListFriends(ArrayList<Friend> listFriends) {
        this.listFriends = listFriends;
    }

    public void addFriend(Friend friend){
        listFriends.add(friend);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

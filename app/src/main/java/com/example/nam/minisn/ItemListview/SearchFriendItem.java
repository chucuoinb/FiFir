package com.example.nam.minisn.ItemListview;

/**
 * Created by Nam on 5/9/2017.
 */

public class SearchFriendItem {
    private Friend friend;
    private boolean isRequest;

    public SearchFriendItem() {
    }

    public SearchFriendItem(Friend friend, boolean isRequest) {
        this.friend = new Friend();
        this.friend=friend;
        this.isRequest = isRequest;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean request) {
        isRequest = request;
    }
}

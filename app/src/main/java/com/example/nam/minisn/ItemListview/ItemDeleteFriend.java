package com.example.nam.minisn.ItemListview;

/**
 * Created by Nam on 5/1/2017.
 */

public class ItemDeleteFriend {
    private Friend friend;
    private boolean isShowCheckBox;
    private int typeChoose;

    public ItemDeleteFriend(){

    }

    public ItemDeleteFriend(Friend friend, boolean isShowCheckBox, int typeChoose) {
        this.friend = friend;
        this.isShowCheckBox = isShowCheckBox;
        this.typeChoose = typeChoose;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public boolean isShowCheckBox() {
        return isShowCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        isShowCheckBox = showCheckBox;
    }

    public int getTpeChoose() {
        return typeChoose;
    }

    public void setTypeChoose(int typeChoose) {
        this.typeChoose = typeChoose;
    }
}

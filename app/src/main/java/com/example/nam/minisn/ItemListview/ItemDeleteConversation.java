package com.example.nam.minisn.ItemListview;

/**
 * Created by Nam on 5/1/2017.
 */

public class ItemDeleteConversation {
    private Conversation conversation;
    private boolean isShowCheckBox;
    private boolean isChoose;

    public ItemDeleteConversation(Conversation conversation, boolean isShowCheckBox, boolean isChoose) {
        this.conversation = conversation;
        this.isShowCheckBox = isShowCheckBox;
        this.isChoose = isChoose;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public boolean isShowCheckBox() {
        return isShowCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        isShowCheckBox = showCheckBox;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }
}

package com.buddhaclub.event;

import com.buddhaclub.model.Model_User_Account;

public interface EventMain {

    public void showLoading(boolean show);

    public void initChat();

    public void selectUser(Model_User_Account user);

    public void updateUser(Model_User_Account user);
}

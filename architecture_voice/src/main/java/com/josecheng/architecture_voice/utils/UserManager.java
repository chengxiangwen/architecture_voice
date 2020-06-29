package com.josecheng.architecture_voice.utils;


import com.josecheng.lib_base.ft_login.model.user.User;

public class UserManager {
    private static UserManager mInstance;
    private User mUser;

    public static UserManager getInstance(){
        if (mInstance == null){
            synchronized (UserManager.class){
                if (mInstance == null){
                    mInstance = new UserManager();
                }
            }
        }
        return mInstance;
    }

    public void saveUser(User user){
        mUser = user;
        saveLocal(user);
    }

    private void saveLocal(User user){

    }

    public User getUser(){
        return mUser;
    }

    /**
     * init the user
     */
    public void setUser(User user) {

        this.mUser = user;
    }

    public User getLocal(){
        return null;
    }

    public boolean hasLogin(){
        return getUser() == null ? false:true;
    }
    public boolean hasLogined() {

        return mUser == null ? false : true;
    }

    public void removeUser(){
        mUser = null;
        removeLocal();
    }

    private void removeLocal() {

    }

}

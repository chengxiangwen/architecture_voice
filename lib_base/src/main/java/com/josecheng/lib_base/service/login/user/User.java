package com.josecheng.lib_base.service.login.user;


import android.os.Parcel;
import android.os.Parcelable;

import com.josecheng.lib_base.BaseModel;

/**
 * 用户数据协议
 */
public class User extends BaseModel implements Parcelable {
  public int ecode;
  public String emsg;
  public UserContent data;

  protected User(Parcel in) {
    ecode = in.readInt();
    emsg = in.readString();
    data = in.readParcelable(UserContent.class.getClassLoader());
  }

  public static final Creator<User> CREATOR = new Creator<User>() {
    @Override
    public User createFromParcel(Parcel in) {
      return new User(in);
    }

    @Override
    public User[] newArray(int size) {
      return new User[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(ecode);
    dest.writeString(emsg);
    dest.writeParcelable(data, flags);
  }
}

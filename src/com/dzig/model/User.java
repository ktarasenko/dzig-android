package com.dzig.model;


import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

public class User implements Parcelable {

    private  final String id;
    private  final String email;
    private  final String nickName;


    public User(String id, String email, String nickName) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNickName() {
        return nickName;
    }

    @Override
    public int describeContents() {
        return 2003;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(email);
        out.writeString(nickName);
    }

    public static final ComplexCreator<User> CREATOR
            = new ComplexCreator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(
                    in.readString(),
                    in.readString(),
                    in.readString());

        }

        @Override
        public User createFromJSON(JSONObject in) {
            return new User(
                    in.optString("id"),
                    in.optString("email"),
                    in.optString("nickname"));
        }


        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}


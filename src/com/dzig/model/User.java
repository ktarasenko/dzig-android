package com.dzig.model;


import android.os.Parcel;
import android.os.Parcelable;
import com.dzig.app.DzigApplication;
import org.json.JSONObject;

public class User implements Parcelable {

    private  final String id;
    private  final String email;
    private  final String nickName;
    private  final String avatar;


    public User(String id, String email, String nickName, String avatar) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
        this.avatar = avatar;
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
    
    public String getAvatar() {
		return avatar;
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
        out.writeString(avatar);
    }

    public static User currentUser(){
        return DzigApplication.userManager().getCurrentUser();
    }

    public static final ComplexCreator<User> CREATOR
            = new ComplexCreator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString());

        }

        @Override
        public User createFromJSON(JSONObject in) {
            return new User(
                    in.optString("id"),
                    in.optString("email"),
                    in.optString("nickname"),
                    in.optString("avatar"));
        }


        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return nickName;
    }
}


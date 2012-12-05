package com.dzig.model;

import java.util.Date;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.dzig.api.ParseHelpers;

public class ChatMessage implements Parcelable{

    private final String id;
    private final String creator;
    private final Date date;
    private final String message;

    public ChatMessage(String id, String creator, Date date, String message) {
        this.id = id;
        this.creator = creator;
        this.date = date;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getCreator() {
        return creator;
    }

    public Date getDate() {
        return date;
    }

    public String getMessage() {
		return message;
	}

    @Override
    public int describeContents() {
        return 2002;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(creator);
        out.writeLong(date.getTime());
        out.writeString(message);
    }

    public static final ComplexCreator<ChatMessage> CREATOR = new ComplexCreator<ChatMessage>() {
        @Override
		public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(
                    in.readString(),
                    in.readString(),
                    new Date(in.readLong()),
                    in.readString());

        }

        @Override
		public ChatMessage createFromJSON(JSONObject in) {
            return new ChatMessage(
                    in.optString("id"),
                    in.optString("creator"),
                    ParseHelpers.parseDate(in.optString("date")),
                    in.optString("msg"));
        }


        @Override
		public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };
}

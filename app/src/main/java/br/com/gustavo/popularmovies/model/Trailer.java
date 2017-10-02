package br.com.gustavo.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gustavomagalhaes on 9/10/17.
 */

public class Trailer implements Parcelable{

    private String id;
    private String key;
    private String name;

    public Trailer(String id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    protected Trailer(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(key);
        parcel.writeString(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{id:" + id + ", " +
                "key:" + key + ", " +
                "name:" + name + ", " + "}";
    }
}

package com.marverenic.music.instances;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Genre implements Parcelable, Comparable<Genre> {

    public static final Parcelable.Creator<Genre> CREATOR = new Parcelable.Creator<Genre>() {
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };

    @SerializedName("genreId")
    public int genreId;
    @SerializedName("genreName")
    public String genreName;

    public Genre(final int genreId, final String genreName) {
        super();
        this.genreId = genreId;
        this.genreName = genreName;
    }

    private Genre(Parcel in) {
        genreId = in.readInt();
        genreName = in.readString();
    }

    @Override
    public int hashCode() {
        return genreId;
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj ||
                (obj != null && obj instanceof Genre && genreId == ((Genre) obj).genreId);
    }

    public String toString() {
        return genreName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(genreId);
        dest.writeString(genreName);
    }

    @Override
    public int compareTo(@NonNull Genre another) {
        return genreName.compareTo(another.genreName);
    }
}

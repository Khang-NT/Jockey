package com.marverenic.music.instances;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.marverenic.music.utils.Util;

public final class Genre implements Parcelable, Comparable<Genre> {

    public static final Parcelable.Creator<Genre> CREATOR = new Parcelable.Creator<Genre>() {
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };

    @SerializedName("genreId")
    public long genreId;
    @SerializedName("genreName")
    public String genreName;

    private Genre() {

    }

    private Genre(Parcel in) {
        genreId = in.readLong();
        genreName = in.readString();
    }

    public long getGenreId() {
        return genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    @Override
    public int hashCode() {
        return Util.hashLong(genreId);
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj
                || (obj != null && obj instanceof Genre && genreId == ((Genre) obj).genreId);
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
        dest.writeLong(genreId);
        dest.writeString(genreName);
    }

    @Override
    public int compareTo(@NonNull Genre another) {
        return genreName.compareTo(another.genreName);
    }
}

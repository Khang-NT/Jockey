package com.marverenic.music.instances;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public final class Artist implements Parcelable, Comparable<Artist> {

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @SerializedName("artistId")
    public int artistId;
    @SerializedName("artistName")
    public String artistName;

    private Artist() {

    }

    private Artist(Parcel in) {
        artistId = in.readInt();
        artistName = in.readString();
    }

    public int getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    @Override
    public int hashCode() {
        return artistId;
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj
                || (obj != null && obj instanceof Artist && artistId == ((Artist) obj).artistId);
    }

    public String toString() {
        return artistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(artistId);
        dest.writeString(artistName);
    }

    @Override
    public int compareTo(@NonNull Artist another) {
        String o1c = (artistName == null)
                ? ""
                : artistName.toLowerCase(Locale.ENGLISH);
        String o2c = (another.artistName == null)
                ? ""
                : another.artistName.toLowerCase(Locale.ENGLISH);
        if (o1c.startsWith("the ")) {
            o1c = o1c.substring(4);
        } else if (o1c.startsWith("a ")) {
            o1c = o1c.substring(2);
        }
        if (o2c.startsWith("the ")) {
            o2c = o2c.substring(4);
        } else if (o2c.startsWith("a ")) {
            o2c = o2c.substring(2);
        }
        return o1c.compareTo(o2c);
    }
}

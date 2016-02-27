package com.marverenic.music.instances;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.marverenic.music.utils.Util;

import java.util.Locale;

public final class Album implements Parcelable, Comparable<Album> {

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @SerializedName("albumId")
    protected long albumId;
    @SerializedName("albumName")
    protected String albumName;
    @SerializedName("artistId")
    protected long artistId;
    @SerializedName("artistName")
    protected String artistName;
    @SerializedName("year")
    protected int year;
    @SerializedName("artUri")
    protected String artUri;

    private Album() {

    }

    private Album(Parcel in) {
        albumId = in.readLong();
        albumName = in.readString();
        artistId = in.readLong();
        artistName = in.readString();
        year = in.readInt();
        artUri = in.readString();
    }

    public long getAlbumId() {
        return albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public long getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getYear() {
        return year;
    }

    public String getArtUri() {
        return artUri;
    }

    @Override
    public int hashCode() {
        return Util.hashLong(albumId);
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj
                || (obj != null && obj instanceof Album && albumId == ((Album) obj).albumId);
    }

    public String toString() {
        return albumName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(albumId);
        dest.writeString(albumName);
        dest.writeLong(artistId);
        dest.writeString(artistName);
        dest.writeInt(year);
        dest.writeString(artUri);
    }

    @Override
    public int compareTo(@NonNull Album another) {
        String o1c = (albumName == null)
                ? ""
                : albumName.toLowerCase(Locale.ENGLISH);
        String o2c = (another.albumName == null)
                ? ""
                : another.albumName.toLowerCase(Locale.ENGLISH);
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

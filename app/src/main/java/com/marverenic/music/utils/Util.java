package com.marverenic.music.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.audiofx.AudioEffect;
import android.os.Build;

import com.marverenic.music.PlayerController;
import com.marverenic.music.instances.Album;
import com.marverenic.music.instances.Library;
import com.marverenic.music.instances.Song;

import java.io.File;
import java.util.UUID;

public final class Util {

    /**
     * This UUID corresponds to the UUID of an Equalizer Audio Effect. It has been copied from
     * {@link AudioEffect#EFFECT_TYPE_EQUALIZER} for backwards compatibility since this field was
     * added in API level 18.
     */
    private static final UUID EQUALIZER_UUID;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            EQUALIZER_UUID = AudioEffect.EFFECT_TYPE_EQUALIZER;
        } else {
            EQUALIZER_UUID = UUID.fromString("0bed4300-ddd6-11db-8f34-0002a5d5c51b");
        }
    }

    /**
     * This class is never instantiated
     */
    private Util() {

    }

    public static Intent getSystemEqIntent(Context c) {
        Intent systemEq = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        systemEq.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, c.getPackageName());
        systemEq.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, PlayerController.getAudioSessionId());

        ActivityInfo info = systemEq.resolveActivityInfo(c.getPackageManager(), 0);
        if (info != null && !info.name.startsWith("com.android.musicfx")) {
            return systemEq;
        } else {
            return null;
        }
    }

    /**
     * Checks whether the current device is capable of instantiating and using an
     * {@link android.media.audiofx.Equalizer}
     * @return True if an Equalizer may be used at runtime
     */
    public static boolean hasEqualizer() {
        for (AudioEffect.Descriptor effect : AudioEffect.queryEffects()) {
            if (EQUALIZER_UUID.equals(effect.type)) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap fetchFullArt(Song song) {
        Album reference = Library.findAlbumById(song.albumId);
        if (reference != null) {
            File image = new File(reference.getArtUri());
            if (image.exists()) {
                return BitmapFactory.decodeFile(image.getAbsolutePath());
            }
        }
        return null;
    }

    public static int hashLong(long value) {
        return (int) (value ^ (value >>> 32));
    }

}

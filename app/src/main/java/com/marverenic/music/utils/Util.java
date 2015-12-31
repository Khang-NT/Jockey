package com.marverenic.music.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.AudioEffect;
import android.os.Build;

import com.marverenic.music.PlayerController;
import com.marverenic.music.instances.Song;

import java.util.UUID;

public class Util {

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

    public static Intent getSystemEqIntent(Context c) {
        Intent systemEqualizer = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        systemEqualizer.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, c.getPackageName());
        systemEqualizer.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, PlayerController.getAudioSessionId());

        ActivityInfo info = systemEqualizer.resolveActivityInfo(c.getPackageManager(), 0);
        if (info != null && !info.name.startsWith("com.android.musicfx")) {
            return systemEqualizer;
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


    public static Bitmap fetchFullArt(Song song){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(song.location);
            byte[] stream = retriever.getEmbeddedPicture();
            if (stream != null)
                return BitmapFactory.decodeByteArray(stream, 0, stream.length);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
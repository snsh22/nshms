package music.streaming.dev.snsh.musicstreaming.utly;

import android.app.Activity;
import android.util.TypedValue;

import com.afollestad.aesthetic.Aesthetic;
import com.orhanobut.hawk.Hawk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.act.NowPlayingActivity;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;

/**
 * Created by androiddeveloper on 26-Jan-17.
 */

public class MUtility {
    public static int getShuffleNumber(int size, int currentIndex) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++)
            list.add(i);
        Collections.shuffle(list);
        if (list.get(0) == currentIndex) {
            if (size == 1)
                return currentIndex;
            else
                return list.get(1);
        } else
            return list.get(0);
    }

    public static boolean hidePlayScreen() {
        Hawk.init(App.getApplication()).build();
        return Hawk.get(MConstants.GOTO_NOW_PLAYING, true);
    }

    public static void updateNowPlayingMediaItem(Activity activity, LinkedList<MediaItem> mediaItems, int nowPlayingIndex) {
        Hawk.init(activity).build();
        if (Hawk.contains(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST))
            Hawk.delete(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST);
        Hawk.put(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST, mediaItems);

        if (Hawk.contains(MConstants.NOW_PLAYING_INDEX))
            Hawk.delete(MConstants.NOW_PLAYING_INDEX);
        Hawk.put(MConstants.NOW_PLAYING_INDEX, nowPlayingIndex);

        if (!MUtility.hidePlayScreen())
            activity.startActivity(NowPlayingActivity.newIntent(activity));
    }

    public static void updateLastOnlineDate() {
        if (NetworkHelper.isOnline(App.CONTEXT)) {
            Calendar c = Calendar.getInstance();
//        Log.e("asdf", "Current time => " + c.getTime());
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(c.getTime());

            Hawk.init(App.CONTEXT).build();
            if (Hawk.contains(MConstants.DATE_LAST_ONLINE))
                Hawk.delete(MConstants.DATE_LAST_ONLINE);
            Hawk.put(MConstants.DATE_LAST_ONLINE, formattedDate);
        }
    }

    public static String getDateLastOnline() {
        Hawk.init(App.CONTEXT).build();
        return Hawk.get(MConstants.DATE_LAST_ONLINE, "01/01/2017");

    }

    public static int getWindowBackground(Activity activity) {
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.windowBackground, typedValue, true);
        if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // windowBackground is a color
            return typedValue.data;
        } else {
            // windowBackground is not a color, probably a drawable
//            Drawable d = activity.getResources().getDrawable(a.resourceId);
            return 0;
        }
    }

    public static int getColorPrimaryAesthetic() {
        final int[] colorPrimary = new int[1];
        Aesthetic.get()
                .colorPrimary()
                .take(1)
                .subscribe(color -> {
                    // Use color (an integer)
                    colorPrimary[0] = color;
                });
        return colorPrimary[0];
    }

    public static int getTextColorPrimaryAesthetic() {
        final int[] ints = new int[1];
        Aesthetic.get()
                .textColorPrimary()
                .take(1)
                .subscribe(color -> {
                    // Use color (an integer)
                    ints[0] = color;
                });
        return ints[0];
    }
}

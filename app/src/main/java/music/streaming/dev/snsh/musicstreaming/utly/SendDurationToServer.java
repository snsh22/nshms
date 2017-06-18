package music.streaming.dev.snsh.musicstreaming.utly;

import android.text.format.DateUtils;
import android.util.Log;

import com.devbrackets.android.playlistcore.service.PlaylistServiceCore;
import com.orhanobut.hawk.Hawk;

import java.util.Formatter;
import java.util.Locale;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.dto.PlayLog;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by androiddeveloper on 25-Mar-17.
 */

public class SendDurationToServer {

    private static StringBuilder formatBuilder = new StringBuilder();
    private static Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());


    public static boolean isPlayingSong() {

        PlaylistManager playlistManager = App.getPlaylistManager();
        PlaylistServiceCore.PlaybackState currentPlaybackState = playlistManager.getCurrentPlaybackState();
        if (currentPlaybackState != PlaylistServiceCore.PlaybackState.STOPPED &&
                currentPlaybackState != PlaylistServiceCore.PlaybackState.ERROR) {
            /*String mediaUrl = playlistManager.getCurrentItem().getMediaUrl();
            if (FileUtils.isFileExists(mediaUrl)) {
                FileUtils.deleteFile(mediaUrl);
                Log.e("asdf", "deleted encrypted file from send duration to server");
            }*/

            if (playlistManager.getCurrentItem().getAlbumImage().startsWith("/storage/")) {
                return false;
            } else
                return true;
        } else {
            return false;
        }
    }

    public static void sendDuration() {
        Hawk.init(App.getApplication()).build();
        PlaylistManager playlistManager = App.getPlaylistManager();

        if (Hawk.contains(MConstants.CUS_ID)) {
            final String cusId = Hawk.get(MConstants.CUS_ID);
            final String songID = playlistManager.getCurrentItem().getSongId();
            final String duration = formatMsToServer(playlistManager.getCurrentProgress().getPosition());
            RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
            Call<PlayLog> call = requestInterface2.sendDurationToServer(songID, cusId, duration);
            call.enqueue(new retrofit2.Callback<PlayLog>() {
                @Override
                public void onResponse(Call<PlayLog> call, Response<PlayLog> response) {
                    if (response.isSuccessful())
                        Log.e("sendDurationToServer", response.message() + " : " + songID + " : " + cusId + " : " + duration);
                }

                @Override
                public void onFailure(Call<PlayLog> call, Throwable t) {
                    Log.e("sendDurationToServer", t.getMessage());

                }
            });
        }
    }

    public static String formatMsToServer(long milliseconds) {
        if (milliseconds < 0) {
            return "00:00";
        }

        long seconds = (milliseconds % DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
        long minutes = (milliseconds % DateUtils.HOUR_IN_MILLIS) / DateUtils.MINUTE_IN_MILLIS;
        long hours = (milliseconds % DateUtils.DAY_IN_MILLIS) / DateUtils.HOUR_IN_MILLIS;

        formatBuilder.setLength(0);
        /*if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        }*/

        return formatter.format("%02d:%02d", minutes, seconds).toString();
    }
}

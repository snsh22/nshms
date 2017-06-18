package music.streaming.dev.snsh.musicstreaming.utly;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

import music.streaming.dev.snsh.musicstreaming.dto.LowQualityDTO;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Response;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

/**
 * Created by androiddeveloper on 01-Jun-17.
 */

public class PlayLowQty {

    public static void loadPlayLowQty(final PlaylistManager playlistManager, final ArrayList<MediaItem> mediaItemList, final int selectedIndex) {
        RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
        Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(mediaItemList.get(selectedIndex).getSongId()));
        call.enqueue(new retrofit2.Callback<LowQualityDTO>() {

            @Override
            public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                LinkedList<MediaItem> mediaItems = new LinkedList<>();
                MediaItem mediaItem = new MediaItem(String.valueOf(mediaItemList.get(selectedIndex).getSongId()),
                        mediaItemList.get(selectedIndex).getSongName(), mediaItemList.get(selectedIndex).getArtistName(),
                        mediaItemList.get(selectedIndex).getAlbumName(), mediaItemList.get(selectedIndex).getSongHighPath(),
                        DOMAIN_TEST + response.body().getPath().getSong_low_path(), mediaItemList.get(selectedIndex).getAlbumImage());

                mediaItems.add(mediaItem);

                playlistManager.setParameters(mediaItems, 0);
                playlistManager.setId(20);
                playlistManager.play(0, false);

            }

            @Override
            public void onFailure(Call<LowQualityDTO> call, Throwable t) {
                Log.e("asdf", "error in low qty " + t.getMessage());
            }
        });
    }
}

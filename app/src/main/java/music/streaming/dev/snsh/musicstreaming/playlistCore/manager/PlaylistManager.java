package music.streaming.dev.snsh.musicstreaming.playlistCore.manager;

import android.app.Application;
import android.app.Service;
import android.support.annotation.NonNull;

import com.devbrackets.android.playlistcore.manager.ListPlaylistManager;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;
import music.streaming.dev.snsh.musicstreaming.playlistCore.service.MediaService;

/**
 * A PlaylistManager that extends the {@link ListPlaylistManager} for use with the
 * {@link MediaService} which extends {@link com.devbrackets.android.playlistcore.service.BasePlaylistService}.
 */
public class PlaylistManager extends ListPlaylistManager<MediaItem> {


    @NonNull
    @Override
    protected Application getApplication() {
        return App.getApplication();
    }

    @NonNull
    @Override
    protected Class<? extends Service> getMediaServiceClass() {
        return MediaService.class;
    }

}

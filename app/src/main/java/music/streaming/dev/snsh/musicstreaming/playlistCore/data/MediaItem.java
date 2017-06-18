package music.streaming.dev.snsh.musicstreaming.playlistCore.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.devbrackets.android.playlistcore.manager.IPlaylistItem;
import com.orhanobut.hawk.Hawk;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;

/**
 * A custom {@link IPlaylistItem}
 * to hold the information pertaining to the audio and video items
 */
public class
MediaItem implements IPlaylistItem {

    @NonNull
    private String songId;
    @NonNull
    private String songName;
    @NonNull
    private String mediaUrl;
    @NonNull
    private String songLowPath;
    @Nullable
    private String albumImage;
    @Nullable
    private String albumName;
    @Nullable
    private String artistName;
    @Nullable
    private String lyric;
    boolean isAudio;

    public MediaItem(@NonNull String songId, @NonNull String songName, @NonNull String mediaUrl, String albumImage, String albumName, String artistName, String lyric, boolean isAudio) {
        this.songId = songId;
        this.songName = songName;
        this.mediaUrl = mediaUrl;
        this.albumImage = albumImage;
        this.albumName = albumName;
        this.artistName = artistName;
        this.lyric = lyric;
        this.isAudio = isAudio;
    }

    public MediaItem(
            @NonNull String songId, @NonNull String songName,
            String artistName, String albumName,
            @NonNull String mediaUrl, @NonNull String songLowPath,
            String albumImage
    ) {
        this.songId = songId;
        this.songName = songName;
        this.mediaUrl = mediaUrl;
        this.songLowPath = songLowPath;
        this.albumImage = albumImage;
        this.albumName = albumName;
        this.artistName = artistName;
        this.isAudio = true;
    }

    /*offline only one media url*/
    public MediaItem(
            @NonNull String songId, @NonNull String songName,
            String artistName, String albumName,
            @NonNull String mediaUrl, @NonNull String songLowPath,
            String albumImage,
            String lyric
    ) {
        this.songId = songId;
        this.songName = songName;
        this.mediaUrl = mediaUrl;
        this.albumImage = albumImage;
        this.albumName = albumName;
        this.songLowPath = songLowPath;
        this.artistName = artistName;
        this.lyric = lyric;
        this.isAudio = true;
    }

    @NonNull
    public String getSongId() {
        return songId;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public long getPlaylistId() {
        return 0;
    }

    @Override
    public int getMediaType() {
        return isAudio ? PlaylistManager.AUDIO : PlaylistManager.VIDEO;
    }

    @Override
    public String getMediaUrl() {

        if (getAlbumImage().startsWith("/storage/")) {
            return mediaUrl;
        }

        Hawk.init(App.getApplication()).build();
        boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
        /*if (isLowQuality)
            return songLowPath;
        else
            return mediaUrl;*/
        return isLowQuality ? songLowPath : mediaUrl;
//        return mediaUrl;
    }

    @Override
    public String getDownloadedMediaUri() {
        return null;
    }

    @Override
    public String getThumbnailUrl() {
        return albumImage;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    @Override
    public String getAlbum() {
        return albumName;
    }

    @Override
    public String getArtist() {
        return artistName;
    }

    @Override
    public String getArtworkUrl() {
        return albumImage;
    }

    @Nullable
    @Override
    public String getTitle() {
        return songName;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getLyric() {
        return lyric;
    }

    @NonNull
    public String getSongLowPath() {
        return songLowPath;
    }

    @NonNull
    public String getSongHighPath() {
        return mediaUrl;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "songId='" + songId + '\'' +
                ", songName='" + songName + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", songLowPath='" + songLowPath + '\'' +
                ", albumImage='" + albumImage + '\'' +
                ", albumName='" + albumName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", lyric='" + lyric + '\'' +
                ", isAudio=" + isAudio +
                '}';
    }
}
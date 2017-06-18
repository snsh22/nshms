package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by androiddeveloper on 19-Mar-17.
 */

public class PersonalPlaylistSong {

    @SerializedName("error")
    public boolean error;
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @SerializedName("artist_name")
        public String artist_name;
        @SerializedName("album_name")
        public String album_name;
        @SerializedName("songid")
        public int songid;
        @SerializedName("song_name")
        public String song_name;
        @SerializedName("album_image")
        public String album_image;
        @SerializedName("song_high_path")
        public String song_high_path;
        @SerializedName("song_low_path")
        public String song_low_path;
        @SerializedName("playlistid")
        public int playlistid;
    }
}

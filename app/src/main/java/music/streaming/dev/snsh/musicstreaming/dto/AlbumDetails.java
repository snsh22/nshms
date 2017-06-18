package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by androiddeveloper on 16-Mar-17.
 */

public class AlbumDetails {

    @SerializedName("error")
    public boolean error;
    @SerializedName("status")
    public int status;
    @SerializedName("song_data")
    public List<Song_data> song_data;

    public static class Song_data {
        @SerializedName("id")
        public int id;
        @SerializedName("artist_name")
        public String artist_name;
        @SerializedName("album_id")
        public int album_id;
        @SerializedName("song_name")
        public String song_name;
        @SerializedName("song_high_path")
        public String song_high_path;
    }
}
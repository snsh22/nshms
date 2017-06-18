package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by androiddeveloper on 19-Mar-17.
 */

public class SearchSong {

    @SerializedName("error")
    public boolean error;
    @SerializedName("status")
    public int status;
    @SerializedName("search_data")
    public List<Search_data> search_data;

    public static class Search_data {
        @SerializedName("id")
        public int id;
        @SerializedName("artist_name")
        public String artist_name;
        @SerializedName("album_name")
        public String album_name;
        @SerializedName("song_name")
        public String song_name;
        @SerializedName("song_rating")
        public int song_rating;
        @SerializedName("song_high_path")
        public String song_high_path;
        @SerializedName("song_low_path")
        public String song_low_path;
        @SerializedName("album_image")
        public String album_image;

        public int getId() {
            return id;
        }

        public String getArtist_name() {
            return artist_name;
        }

        public String getAlbum_name() {
            return album_name;
        }

        public String getSong_name() {
            return song_name;
        }

        public int getSong_rating() {
            return song_rating;
        }

        public String getSong_high_path() {
            return song_high_path;
        }

        public String getSong_low_path() {
            return song_low_path;
        }

        public String getAlbum_image() {
            return album_image;
        }
    }

    public boolean isError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public List<Search_data> getSearch_data() {
        return search_data;
    }
}
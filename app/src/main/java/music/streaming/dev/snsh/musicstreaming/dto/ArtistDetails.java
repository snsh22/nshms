package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by androiddeveloper on 16-Mar-17.
 */

public class ArtistDetails {

    @SerializedName("error")
    public boolean error;
    @SerializedName("status")
    public int status;
    @SerializedName("artists_data")
    public Artists_data artists_data;
    @SerializedName("albums_data")
    public List<Albums_data> albums_data;

    public static class Artists_data {
        @SerializedName("id")
        public int id;
        @SerializedName("artist_name")
        public String artist_name;
        @SerializedName("artist_title")
        public String artist_title;
        @SerializedName("artist_info")
        public String artist_info;
        @SerializedName("artist_photo")
        public String artist_photo;
        @SerializedName("created_at")
        public String created_at;
        @SerializedName("updated_at")
        public String updated_at;
    }

    public static class Albums_data {
        @SerializedName("id")
        public int id;
        @SerializedName("album_name")
        public String album_name;
        @SerializedName("album_title")
        public String album_title;
        @SerializedName("album_info")
        public String album_info;
        @SerializedName("album_image")
        public String album_image;
        @SerializedName("year")
        public int year;
        @SerializedName("created_at")
        public String created_at;
        @SerializedName("updated_at")
        public String updated_at;
    }
}
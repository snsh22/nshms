package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by androiddeveloper on 16-Mar-17.
 */

public class DiscoverNewThisWeek {

    @SerializedName("error")
    public boolean error;
    @SerializedName("status")
    public int status;
    @SerializedName("weekdata")
    public Weekdata weekdata;

    public static class Data {
        @SerializedName("song_name")
        public String song_name;
        @SerializedName("album_name")
        public String album_name;
        @SerializedName("artist_name")
        public String artist_name;
        @SerializedName("id")
        public int id;
        @SerializedName("song_high_path")
        public String song_high_path;
        @SerializedName("song_low_path")
        public String song_low_path;
        @SerializedName("album_image")
        public String album_image;
        public String type;

        public Data(String type) {
            this.type = type;
        }
    }

    public static class Weekdata {
        @SerializedName("total")
        public int total;
        @SerializedName("per_page")
        public int per_page;
        @SerializedName("current_page")
        public int current_page;
        @SerializedName("last_page")
        public int last_page;
        @SerializedName("next_page_url")
        public String next_page_url;
        @SerializedName("prev_page_url")
        public String prev_page_url;
        @SerializedName("from")
        public int from;
        @SerializedName("to")
        public int to;
        @SerializedName("data")
        public List<Data> data;
    }
}

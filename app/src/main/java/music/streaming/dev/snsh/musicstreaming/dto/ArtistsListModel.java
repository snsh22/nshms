package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by androiddeveloper on 16-Mar-17.
 */

public class ArtistsListModel {


    @SerializedName("error")
    public boolean error;
    @SerializedName("status")
    public int status;
    @SerializedName("paginate")
    public Paginate paginate;

    public static class Data {
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

        public String type;

        public Data(String type) {
            this.type = type;
        }
    }

    public static class Paginate {
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
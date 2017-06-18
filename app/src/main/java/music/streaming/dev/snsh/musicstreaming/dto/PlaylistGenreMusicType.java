package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by androiddeveloper on 18-Mar-17.
 */

public class PlaylistGenreMusicType {

    @SerializedName("error")
    public boolean error;
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @SerializedName("id")
        public int id;
        @SerializedName("genere")
        public String genere;
        @SerializedName("genere_image")
        public String genere_image;
        @SerializedName("created_at")
        public String created_at;
        @SerializedName("updated_at")
        public String updated_at;
    }
}
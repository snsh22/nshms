package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by androiddeveloper on 19-Mar-17.
 */

public class CustomerPlayListName {


    @SerializedName("error")
    public boolean error;
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @SerializedName("playlistid")
        public int playlistid;
        @SerializedName("playlist_name")
        public String playlist_name;
        @SerializedName("cust_id")
        public int cust_id;
    }
}
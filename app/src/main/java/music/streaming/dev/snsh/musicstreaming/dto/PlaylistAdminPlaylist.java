package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by androiddeveloper on 18-Mar-17.
 */

public class PlaylistAdminPlaylist {

    @SerializedName("error")
    public boolean error;
    @SerializedName("status")
    public int status;
    @SerializedName("admin_playlist_data")
    public List<Admin_playlist_data> admin_playlist_data;

    public static class Admin_playlist_data {
        @SerializedName("id")
        public int id;
        @SerializedName("playlist_name")
        public String playlist_name;
        @SerializedName("playlist_rating")
        public int playlist_rating;
        @SerializedName("playlist_photo")
        public String playlist_photo;
        @SerializedName("created_at")
        public String created_at;
        @SerializedName("updated_at")
        public String updated_at;
    }
}
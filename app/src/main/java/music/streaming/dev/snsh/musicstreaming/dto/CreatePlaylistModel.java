package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by androiddeveloper on 09-Feb-17.
 */

public class CreatePlaylistModel {

    @SerializedName("status")
    public String status;
    @SerializedName("message")
    public String message;
}

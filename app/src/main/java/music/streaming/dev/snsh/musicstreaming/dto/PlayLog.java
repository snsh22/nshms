package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by androiddeveloper on 19-Mar-17.
 */

public class PlayLog {

    @SerializedName("status")
    public String status;

    public String getStatus() {
        return status;
    }
}
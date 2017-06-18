package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by androiddeveloper on 10-Feb-17.
 */

public class FavouriteModel {

    @SerializedName("like")
    @Expose
    private String like;

    @SerializedName("message")
    @Expose
    private String message;

    public String getLike() {
        return like;
    }

    public String getMessage() {
        return message;
    }
}

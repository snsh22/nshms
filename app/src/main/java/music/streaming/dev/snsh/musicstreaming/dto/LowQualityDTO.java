package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by androiddeveloper on 19-Mar-17.
 */
public class LowQualityDTO {

    @SerializedName("error")
    public boolean error;
    @SerializedName("status")
    public int status;
    @SerializedName("path")
    public Path path;

    public static class Path {
        @SerializedName("song_low_path")
        public String song_low_path;

        public String getSong_low_path() {
            return song_low_path;
        }
    }

    public boolean isError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public Path getPath() {
        return path;
    }
}


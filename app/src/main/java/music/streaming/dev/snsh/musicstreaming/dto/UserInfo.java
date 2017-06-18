package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by androiddeveloper on 19-Mar-17.
 */

public class UserInfo {

    @SerializedName("error")
    public boolean error;
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("cust_phno")
        public String cust_phno;
        @SerializedName("name")
        public String name;
        @SerializedName("gender")
        public String gender;
        @SerializedName("st_quality")
        public String st_quality;
        @SerializedName("photo")
        public String photo;
    }
}

package music.streaming.dev.snsh.musicstreaming.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by androiddeveloper on 06-Feb-17.
 */

public class LoginModel {


    @SerializedName("error")
    public String error;
    @SerializedName("code")
    public String code;
    @SerializedName("otp")
    public int otp;

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("custid")
    @Expose
    private String custid;

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public int getOtp() {
        return otp;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getCustid() {
        return custid;
    }
}

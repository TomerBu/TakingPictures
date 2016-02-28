package tomerbu.edu.uploadingphotos.api;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("sucess")
    @Expose
    private Boolean sucess;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("message")
    @Expose
    private String message;

    /**
     *
     * @return
     * The sucess
     */
    public Boolean getSucess() {
        return sucess;
    }

    /**
     *
     * @param sucess
     * The sucess
     */
    public void setSucess(Boolean sucess) {
        this.sucess = sucess;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "sucess=" + sucess +
                ", token='" + token + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    /**
     *
     * @return
     * The token
     */
    public String getToken() {
        return token;
    }

    /**
     *
     * @param token
     * The token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}

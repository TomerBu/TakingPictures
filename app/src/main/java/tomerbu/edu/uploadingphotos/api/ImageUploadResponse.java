package tomerbu.edu.uploadingphotos.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tomerbuzaglo on 22/02/2016.
 */
public class ImageUploadResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("filename")
    @Expose
    private String filename;

    /**
     *
     * @return
     * The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     *
     * @return
     * The filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     *
     * @param filename
     * The filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "ImageUploadResponse{" +
                "success=" + success +
                ", filename='" + filename + '\'' +
                '}';
    }
}
package tomerbu.edu.uploadingphotos.api;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import tomerbu.edu.uploadingphotos.AppManager;


public class ImageUploadAPI {

    private static long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpg");

    //The Apis root address without the node name:
    public static final String API_URL = "https://imagepusher.herokuapp.com/";

    private final Retrofit retrofit;
    private final ImageService mImageService;


    public ImageUploadAPI() {

        retrofit = new Retrofit.Builder().
                baseUrl(API_URL).
                addConverterFactory(GsonConverterFactory.create()).
                client(AppManager.getSharedInstance().getClient()).
                build();

        // Create an instance of our API interface.
        mImageService = retrofit.create(ImageService.class);
    }

    public interface ImageService {


        @Multipart
        @Headers({"Accept: application/json"})
        @POST("upload")
        Call<ImageResposnse> uploadImage(@Part("fileUpload\"; filename=\"picture.jpg\" ") RequestBody file);


    }

    public void uploadImage(String imageUri) {

        File file = new File(imageUri);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JPEG, file);

        Call<ImageResposnse> call = mImageService.uploadImage(requestBody);
        call.enqueue(new Callback<ImageResposnse>() {
            @Override
            public void onResponse(Call<ImageResposnse> call, Response<ImageResposnse> response) {

            }

            @Override
            public void onFailure(Call<ImageResposnse> call, Throwable t) {

            }
        });


    }

}

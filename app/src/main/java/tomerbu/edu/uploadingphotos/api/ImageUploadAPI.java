package tomerbu.edu.uploadingphotos.api;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
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

        //Multipart requests are used when @Multipart is present on the method. Parts are declared using the @Part annotation.
        @Multipart
        @Headers({"Accept: application/json"})
        @POST("/api/upload")
        Call<ImageUploadResponse> uploadImage(@Part("fileUpload\"; filename=\"picture.jpg\" ") RequestBody file, @Part("userId") RequestBody userId, @Part("token") RequestBody token);

        //without token ,rely on interceptors instead.
        @Multipart
        @Headers({"Accept: application/json"})
        @POST("/api/upload")
        Call<ImageUploadResponse> uploadImage(@Part("fileUpload\"; filename=\"picture.jpg\" ") RequestBody file, @Part("userId") RequestBody userId);

        @POST("/login/auth")
        Call<LoginResponse> getToken(@Body LoginBody loginInfo);

//        @PUT("/user/{id}/update")
//        Call<ImageResposnse> updateUser(@Path("id") String id , @Body Item user);

    }

    public void getToken(String userName, String pass, final OnTokenReceivedListener listener) {
        mImageService.getToken(new LoginBody(userName, pass)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse tokenInside = response.body();
                if (tokenInside != null)
                    listener.onTokenReceived(tokenInside);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public LoginResponse getTokenBlocking(String userName, String pass) {
        try {
            Response<LoginResponse> response = mImageService.getToken(new LoginBody(userName, pass)).execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void uploadImage(String imageUri, String token, String userId) {

        File file = new File(imageUri);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JPEG, file);
        RequestBody userIdRequestBody = RequestBody.create(MediaType.parse("plain/text"), userId);
        RequestBody tokendRequestBody = RequestBody.create(MediaType.parse("plain/text"), token);
        Call<ImageUploadResponse> call = mImageService.uploadImage(requestBody, userIdRequestBody, tokendRequestBody);
        call.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                ImageUploadResponse uploadResponse = response.body();
                if (uploadResponse != null)
                    Log.d("TomerBu", uploadResponse.toString());
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void uploadImage(String imageUri, String userId) {

        File file = new File(imageUri);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JPEG, file);
        RequestBody userIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), userId);
        // RequestBody tokendRequestBody = RequestBody.create(MediaType.parse("text/plain"), token);
        Call<ImageUploadResponse> call = mImageService.uploadImage(requestBody, userIdRequestBody);
        call.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                ImageUploadResponse uploadResponse = response.body();
                if (uploadResponse != null)
                    Log.d("TomerBu", uploadResponse.toString());
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}

package tomerbu.edu.uploadingphotos;

import android.app.Application;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import tomerbu.edu.uploadingphotos.api.ImageUploadAPI;
import tomerbu.edu.uploadingphotos.api.LoginResponse;


public class AppManager extends Application {

    private static AppManager sharedInstance;
    private static String token = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedInstance = this;
    }

    //getters
    public static AppManager getSharedInstance() {
        return sharedInstance;
    }

    //public API
    public OkHttpClient getClient() {

        OkHttpClient client = new OkHttpClient.Builder()
                //.addInterceptor(getHttpLoggingInterceptor())
                .addInterceptor(addTokenInterceptor())
                .authenticator(LoginAuthenticator())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        return client;
    }

    /**
     * If a request was unauthorized (OKHttp client tests the headers)
     * The authenticator performs a login and updates the token
     * once it updates the token, the request is dispatched again with the token in the header
     * Also, the token is saved in the token field so in the next requests we don't hit the login api node again
     *
     * @return OKHttp Authenticator
     */
    private Authenticator LoginAuthenticator() {
        return new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                // give up after 3 failures. do this exponentially slower is better.
                // we are in a background thread and we can block it as we like...
                if (responseCount(response) >= 3) {
                    return null; // If we've failed 3 times, give up.
                }

                //Make a blocking call to get a token, this is done on a background thread already.
                ImageUploadAPI api = new ImageUploadAPI();
                //LoginResponse loginResponse = api.getTokenBlocking("<UserName>", "<Password>");
                String appId = getResources().getString(R.string.AppId);
                String appSecret = getResources().getString(R.string.AppSecret);
                LoginResponse loginResponse = api.getTokenBlocking(appId, appSecret);
                token = loginResponse.getToken();

                //run the original request with the token from the authorization node.
                return response.request().newBuilder().header("Authorization", token).build();
            }
        };
    }

    /**
     * A simple method that counts response.priorResponses.
     * //each time a request is retried, the response.priorResponse stack grows.
     * here we count the respnse.priorResponses
     */
    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

    /**
     * A request interceptor that adds the token to every request.
     *
     * @return an OKHttp3 Interceptor to be used by the httpClient
     */
    private Interceptor addTokenInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if (token == null)
                    return chain.proceed(chain.request());
//                Request original = chain.request();

//
//                // Request URL customization: add url parameters:
//                HttpUrl modifiedUrl = original.url().newBuilder()
//                        //Provide your custom parameter here
//                        //.addQueryParameter("token", token)
//                        .build();
//
//                // Request customization: change the request method:
//                String method = original.method();
//
//                // Request customization: add request headers:
//                Request.Builder requestBuilder = original.newBuilder()
//                        .header("Accept", "application/json")
//                        .header("Content-Type", "application/json")
//                        .header("Authorization", token)  // this is the important line
//                        .url(modifiedUrl)
//                        .method(method, original.body());
//
//                Request request = requestBuilder.build();
//                // Response customization: Get a new token:
//                Response response = chain.proceed(request);
//
//                if (response.code() == 401) {
//                    logThisIssue();
//                }
//                return response;
                //a one liner: take the request and add the header.
                return chain.proceed(chain.request().newBuilder().header("Authorization", token).build());
            }
        };
    }

    /**
     * inValuable debugging aid for OKHttp3 work
     *
     * @return an HttpLoggingInterceptor to log all the requests
     */
    @NonNull
    private HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }


}

package tomerbu.edu.uploadingphotos;

import android.app.Application;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public class AppManager extends Application {

    private static AppManager sharedInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedInstance = this;
    }

    //getters
    public static AppManager getSharedInstance() {
        return sharedInstance;
    }

    public OkHttpClient getClient() {


        OkHttpClient client = new OkHttpClient.Builder()
                //.addInterceptor(getHttpLoggingInterceptor())
                .addInterceptor(getAutorizationInterceptor())
                .build();


        return client;
    }

    private Interceptor getAutorizationInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Customize the request
                Request request = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization", "auth-token")
                        .method(original.method(), original.body())
                        .build();

                Response response = chain.proceed(request);

                // Customize or return the response
                return response;
            }
        };
    }

    @NonNull
    private HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

}

package music.streaming.dev.snsh.musicstreaming.rest;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    public static <S> S createService(Class<S> serviceClass) {

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.255.199.194/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient).build();//production*/
        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://139.59.250.169/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient).build();//development*/
        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.100.162:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient).build();//local test*/
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://128.199.148.73/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient).build();//production
        return retrofit.create(serviceClass);

    }
}

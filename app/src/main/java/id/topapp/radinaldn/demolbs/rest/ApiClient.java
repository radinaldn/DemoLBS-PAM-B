package id.topapp.radinaldn.demolbs.rest;

import id.topapp.radinaldn.demolbs.config.ServerConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by radinaldn on 22/10/18.
 */

public class ApiClient {

    private static final String BASE_URL = ServerConfig.API_ENDPOINT;

    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if (retrofit == null ){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}

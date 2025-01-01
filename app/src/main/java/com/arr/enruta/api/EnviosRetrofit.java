package com.arr.enruta.api;

import com.arr.enruta.api.model.EnviosResponse;
import com.squareup.moshi.Moshi;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class EnviosRetrofit {

    private static final Moshi moshi = new Moshi.Builder().build();

    private static final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

    static {
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    // Configurar el cliente OkHttp con timeouts y logging
    private static final OkHttpClient okHttpClient;

    static {
        okHttpClient =
                new OkHttpClient()
                        .newBuilder()
                        .addInterceptor(interceptor)
                        .retryOnConnectionFailure(true)
                        .build();
    }

    private static final Retrofit retrofit =
            new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://www.correos.cu/wp-json/")
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();

    public static EnviosClient auth() {
        return retrofit.create(EnviosClient.class);
    }
}

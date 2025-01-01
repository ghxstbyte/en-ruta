package com.arr.enruta.api;

/**
 * Copyright [2024] [Alessandro Rodríguez]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import androidx.annotation.Keep;
import com.arr.enruta.api.model.EnviosResponse;
import com.squareup.moshi.Moshi;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Keep
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

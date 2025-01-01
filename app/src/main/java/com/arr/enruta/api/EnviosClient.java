package com.arr.enruta.api;

import com.arr.enruta.api.model.EnviosResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface EnviosClient {

    @Headers({"User-Agent: CorreosApi/1.0", "Content-Type: application/json"})
    @GET("correos-api/envios/{codigo}/{anno}/{usuario}/")
    Single<EnviosResponse> getEnvios(
            @Path("codigo") String codigo,
            @Path("anno") String anno,
            @Path("usuario") String usuario);
}

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
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

@Keep
public interface EnviosClient {

    @Headers({"User-Agent: CorreosApi/1.0", "Content-Type: application/json"})
    @GET("correos-api/envios/{codigo}/{anno}/{usuario}/")
    Single<EnviosResponse> getEnvios(
            @Path("codigo") String codigo,
            @Path("anno") String anno,
            @Path("usuario") String usuario);
}

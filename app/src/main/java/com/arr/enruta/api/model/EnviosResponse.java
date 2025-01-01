package com.arr.enruta.api.model;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

import java.util.List;

public class EnviosResponse {

    @Json(name = "p_origen")
    public String origen;

    @Json(name = "p_destino")
    public String destino;

    @Json(name = "timeline")
    public String estado;

    @Json(name = "error")
    public String error;

    @Json(name = "datos")
    public List<Dato> datos;
}

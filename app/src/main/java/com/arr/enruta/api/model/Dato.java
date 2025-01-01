package com.arr.enruta.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class Dato implements Parcelable {

    @Json(name = "oficina_origen")
    public String oficinaOrigen;

    @Json(name = "oficina_destino")
    public String oficinaDestino;

    @Json(name = "estado")
    public String estado;

    @Json(name = "fecha")
    public String fecha;

    @Json(name = "peso")
    public String peso;

    // Constructor vacío opcional
    public Dato() {}

    // Constructor para `Parcel`
    protected Dato(Parcel in) {
        oficinaOrigen = in.readString();
        oficinaDestino = in.readString();
        estado = in.readString();
        fecha = in.readString();
        peso = in.readString();
    }

    public static final Creator<Dato> CREATOR =
            new Creator<Dato>() {
                @Override
                public Dato createFromParcel(Parcel in) {
                    return new Dato(in);
                }

                @Override
                public Dato[] newArray(int size) {
                    return new Dato[size];
                }
            };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(oficinaOrigen);
        dest.writeString(oficinaDestino);
        dest.writeString(estado);
        dest.writeString(fecha);
        dest.writeString(peso);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

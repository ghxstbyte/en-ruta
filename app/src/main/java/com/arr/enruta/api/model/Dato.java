package com.arr.enruta.api.model;

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

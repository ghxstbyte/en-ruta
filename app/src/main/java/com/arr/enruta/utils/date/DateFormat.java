package com.arr.enruta.utils.date;

import android.net.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormat {

    public static String getFechaFormat(String strimg) {
        SimpleDateFormat formatoEntrada =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd MMM.", new Locale("es", "ES"));

        try {
            // Parsear la fecha de entrada
            Date fecha = formatoEntrada.parse(strimg);
            String fechaFormateada = formatoSalida.format(fecha);
            return fechaFormateada;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getHoraFormat(String string) {
        SimpleDateFormat formatoEntrada =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat formatoSalida = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            // Parsear la fecha de entrada
            Date fecha = formatoEntrada.parse(string);
            String fechaFormateada = formatoSalida.format(fecha);
            return fechaFormateada;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

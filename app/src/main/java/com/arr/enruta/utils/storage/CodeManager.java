package com.arr.enruta.utils.storage;

import android.content.Context;
import com.arr.enruta.HistoryActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;

public class CodeManager {

    private static final String FILE_NAME = "codes.json";

    // Método para cargar los códigos guardados
    public static ArrayList<Code> loadCodes(Context context) {
        File file = CodeSave.getDirectory(context);

        if (file != null && file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                Gson gson = new Gson();
                return gson.fromJson(reader, new TypeToken<ArrayList<Code>>() {}.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public static String getInfoJson(Context context, int position, String key) {
        ArrayList<Code> codes = loadCodes(context); // Carga la lista de códigos
        if (position >= 0 && position < codes.size()) { // Verifica si la posición es válida
            Code code = codes.get(position); // Obtiene el objeto en la posición
            Gson gson = new Gson();
            JsonObject jsonObject = gson.toJsonTree(code).getAsJsonObject();
            if (jsonObject.has(key)) { // Verifica si la clave existe
                return jsonObject.get(key).getAsString(); // Devuelve el valor asociado
            }
        }
        return null;
    }

    // Método para guardar la lista de códigos como JSON
    public static void saveCodes(ArrayList<Code> codes, Context context) {
        Gson gson = new Gson();
        String json = gson.toJson(codes); // Convierte la lista a JSON
        CodeSave.saveFile(json, context); // Guarda el JSON en el archivo
    }
}

package com.arr.enruta.utils.storage;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;

public class CodeSave {

    public static void saveFile(String fileString, Context context) {
        try {
            FileOutputStream outputStream = new FileOutputStream(getDirectory(context));
            outputStream.write(fileString.getBytes());
            outputStream.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static File getDirectory(Context context) {
        File file = context.getExternalFilesDir("cache");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return null;
            }
        }
        return new File(file, "codes.json");
    }
}

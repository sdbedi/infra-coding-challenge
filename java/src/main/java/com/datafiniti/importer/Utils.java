package com.datafiniti.importer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Utils {
    private static Gson gson = new Gson();
    private static JsonArray records;
    private static Random rand = new Random();

    /**
     * returns the content of the file at the path provided
     * @param path the path to the file
     * @return the content of the file
     * @throws IOException if no file exists at the path provided or if something goes wrong while reading the file
     */
    private static String readFile(String path) throws IOException {
        FileReader fr = new FileReader(path);
        BufferedReader bufferedReader =  new BufferedReader(fr);
        String line;

        StringBuilder fileContents = new StringBuilder();

        while((line = bufferedReader.readLine()) != null) {
            fileContents.append(line);
        }

        return fileContents.toString();
    }

    private static void loadSampleRecords() throws IOException {
        String serializedRecords = readFile("../records.json");
        records = gson.fromJson(serializedRecords, JsonArray.class);
    }

    public static String createRecord() throws IOException {
        if (records == null) {
            loadSampleRecords();
        }

        Integer randomIdx = rand.nextInt(records.size());
        JsonObject record = records.get(randomIdx).getAsJsonObject();

        return record.toString();
    }
}

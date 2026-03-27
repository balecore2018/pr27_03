package com.example.pr27_03;

import android.os.AsyncTask;
import android.widget.TextView;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class GetAddressByGPS extends AsyncTask<Void, Void, Void> {

    TextView textAddress;
    String coordinates;
    String token = "3bc53995-5ccd-4ff7-9616-435e4925373c";

    private String jsonResponse;
    private AddressResponse response;

    private static final String TAG = "GetAddressByGPS";

    public GetAddressByGPS(String coordinates, TextView textAddress) {
        this.coordinates = coordinates;
        this.textAddress = textAddress;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // Получаем ответ
            jsonResponse = Jsoup.connect(
                            "https://geocode-maps.yandex.ru/v1/?apikey=" + token +
                                    "&geocode=" + coordinates +
                                    "&format=json&results=1")
                    .ignoreContentType(true)
                    .timeout(10000)
                    .execute()
                    .body();

            // 🔍 ОТЛАДКА: Логируем сырой ответ
            Log.d(TAG, "Raw JSON response: " + jsonResponse);

            // Парсим через Gson
            Gson gson = new GsonBuilder().create();
            response = gson.fromJson(jsonResponse, AddressResponse.class);

        } catch (IOException e) {
            Log.e(TAG, "Network error: " + e.getMessage(), e);
            jsonResponse = null;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        // 🔴 Безопасная навигация с проверками на каждом уровне
        try {
            if (jsonResponse == null) {
                textAddress.setText("❌ Ошибка сети");
                return;
            }

            if (response == null) {
                Log.e(TAG, "Parsing failed - response is null");
                textAddress.setText("❌ Ошибка парсинга");
                return;
            }

            // Проверяем каждый уровень вложенности
            if (response.response == null) {
                textAddress.setText("❌ Нет данных в ответе");
                return;
            }

            if (response.response.GeoObjectCollection == null) {
                textAddress.setText("❌ Нет GeoObjectCollection");
                return;
            }

            if (response.response.GeoObjectCollection.featureMember == null ||
                    response.response.GeoObjectCollection.featureMember.isEmpty()) {
                textAddress.setText("📍 Адрес не найден");
                return;
            }

            var firstMember = response.response.GeoObjectCollection.featureMember.get(0);
            if (firstMember == null || firstMember.GeoObject == null) {
                textAddress.setText("❌ Нет данных об объекте");
                return;
            }

            var metaData = firstMember.GeoObject.metaDataProperty;
            if (metaData == null || metaData.GeocoderMetaData == null) {
                textAddress.setText("❌ Нет метаданных");
                return;
            }

            String address = metaData.GeocoderMetaData.text;
            if (address != null && !address.isEmpty()) {
                textAddress.setText(address);
            } else {
                textAddress.setText("⚠️ Пустой адрес");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onPostExecute: " + e.getMessage(), e);
            textAddress.setText("❌ Ошибка: " + e.getMessage());
        }
    }
}
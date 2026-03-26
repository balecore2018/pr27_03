package com.example.pr27_03;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;

import java.io.IOException;

public class GetAddressByGPS extends AsyncTask<Void, Void, Void> {
    TextView textAddress;
    String coordinats;
    String token = "d129d4b2-4ab3-4fce-bccc-aa45cc87a457";
    AddressResponse Response = null;

    public GetAddressByGPS(String coordinats, TextView TextAddress) {
        this.coordinats = coordinats;
        textAddress = TextAddress;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            Document document = (Document) Jsoup.connect("https://geocode-maps.yandex.ru/1.x/?apikey=" + token + "&format=json&geocode=" + coordinats + "&results=1")
                    .ignoreContentType(true)
                    .get();

            GsonBuilder builder = new GsonBuilder();
            Response = builder.create().fromJson(document.getTextContent(), AddressResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void voids) {
        textAddress.setText(Response.response.GeoObjectCollection.featureMember.get(0).GeoObject.metaDataProperty.GeocoderMetaData.text);
    }

}

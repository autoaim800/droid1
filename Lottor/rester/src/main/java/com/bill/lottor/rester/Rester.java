package com.bill.lottor.rester;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Rester {
    private static Rester ourInstance = new Rester();

    private final OkHttpClient client;

    public static Rester getInstance() {
        return ourInstance;
    }

    private Rester() {
        client = new OkHttpClient();
    }

    public void listCompany() {

        Request request = new Request.Builder()
                .url("https://api.tatts.com/sales/vmax/web/data/lotto/companies")
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "f4fc5f74-0e6f-b3e1-0575-5b04a91d044b")
                .build();

        try {
            Response response = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }
}

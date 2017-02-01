package eu.epitech.fernan_s.msa_m.yourimage;

import okhttp3.OkHttpClient;

/**
 * Created by mathe on 01/02/2017.
 */

public class SHttpClient {
    private static SHttpClient ourInstance = new SHttpClient();

    private static OkHttpClient client;

    public static SHttpClient getInstance() {
        return ourInstance;
    }

    private SHttpClient() {
        client = new OkHttpClient();
    }

    public OkHttpClient getClient(){
        return client;
    }
}

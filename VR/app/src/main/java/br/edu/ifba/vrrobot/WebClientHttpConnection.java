package br.edu.ifba.vrrobot;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Léo on 28/01/2018.
 */
public class WebClientHttpConnection {

    public static final int DID_START = 0;
    public static final int DID_ERROR = 1;
    public static final int DID_SUCCEED = 2;

    private static final int GET = 0;
    private static final int POST = 1;
    private static final int PUT = 2;
    private static final int DELETE = 3;
    private static  final int STATUS = 4;
    private String URL ="";
    private int metodo = -1;
    private String params = null;

    public WebClientHttpConnection(String URL, int metodo, String params) {
        this.URL = URL;
        this.metodo = metodo;
        this.params = params;
    }
    public WebClientHttpConnection() {

    }

    public String get(String url) throws IllegalStateException, IOException {
        return executeHTTPConnection(GET, url, null);
    }

    public String post(String url, String data) throws IllegalStateException, IOException {
        return executeHTTPConnection(POST, url, data);
    }

    public String put(String url, String data) throws IllegalStateException, IOException {
        return executeHTTPConnection(PUT, url, data);
    }

    public String delete(String url) throws IllegalStateException, IOException {
        return executeHTTPConnection(DELETE, url, null);
    }
    public String status(String url) throws IllegalStateException, IOException {
        return executeHTTPConnection(STATUS, url, null);
    }


    private String executeHTTPConnection(int method, String url, String data) throws IllegalStateException, IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 25000);
        HttpResponse response = null;
        StatusLine statusLine;
        switch (method) {
            case GET:
                HttpGet httpGet = new HttpGet(url);
                response = httpClient.execute(httpGet);
                break;
            case POST:
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new StringEntity(data));
                response = httpClient.execute(httpPost);
                break;
            case PUT:
                HttpPut httpPut = new HttpPut(url);
                httpPut.setEntity(new StringEntity(data));
                response = httpClient.execute(httpPut);
                break;
            case DELETE:
                response = httpClient.execute(new HttpDelete(url));
                break;
            case STATUS:
                HttpGet status = new HttpGet(url);
                response = httpClient.execute(status);
                return Integer.toString(response.getStatusLine().getStatusCode());

            default:
                throw new IllegalArgumentException("Unknown Request.");
        }


        return processResponse(response.getEntity());

    }

    private String processResponse(HttpEntity entity) throws IllegalStateException,IOException {
        String jsonText = EntityUtils.toString(entity, HTTP.UTF_8);
        return jsonText;
    }
}

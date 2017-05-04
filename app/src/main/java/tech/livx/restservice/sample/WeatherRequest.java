package tech.livx.restservice.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.MediaType;
import tech.livx.restservice.requests.RestRequest;

/**
 * Description of class
 * <p/>
 * Bugs: none known
 *
 * @author Mitch, LivX : livx.tech
 * @version 1.0
 * @date 2017/04/18
 */
public class WeatherRequest extends RestRequest {

    public WeatherRequest() {

    }

    @Override
    protected MediaType getMediaType(Context context) {
        return null;
    }

    @Override
    protected String getURL(Context context) {
        return "https://httpbin.org/get";
    }

    @Override
    protected Map<String, String> getHeaders(Context context) {
        return null;
    }

    @Override
    protected byte[] getBody(Context context) {
        return null;
    }

    @Override
    protected void onComplete(Context context, InputStream response) throws IOException {
        SharedPreferences.Editor sharedPreferencesEditor = context.getSharedPreferences("weather", Context.MODE_PRIVATE).edit();

        sharedPreferencesEditor.putString("result", getStringFromInputStream(response));
        sharedPreferencesEditor.commit();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i("YAY","FINISHED");
    }

    @Override
    protected void onError(Context context) {

    }

    @Override
    protected String getMethod(Context context) {
        return METHOD_GET;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}

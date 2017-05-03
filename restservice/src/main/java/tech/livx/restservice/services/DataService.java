package tech.livx.restservice.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import tech.livx.restservice.requests.RequestCallback;
import tech.livx.restservice.requests.RestRequest;

/**
 * Data Service used to manage api calls independently from activities
 * <p/>
 * Bugs: none known
 *
 * @author Mitch, LivX : livx.tech
 * @version 1.0
 * @date 2017/04/13
 */
public class DataService extends Service {
    public static final String DATA_PREF = "data_prefs";

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private List<DataServiceListener> listeners = new ArrayList<>();
    private Map<String, List<Long>> pendingRequests = new HashMap<>();


    public class LocalBinder extends Binder {
        public DataService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DataService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void removeListener(DataServiceListener listener) {
        listeners.remove(listener);
    }

    public void addListener(DataServiceListener serviceListener) {
        listeners.add(serviceListener);
    }

    public boolean isPending(long requestId) {
        for(List<Long> requests : pendingRequests.values())
            if(requests.contains(requestId))
                return true;

        return false;
    }

    public int checkResult(String requestType) {
        int code = getSharedPreferences(DATA_PREF, MODE_PRIVATE ).getInt(requestType, 0);
        getSharedPreferences(DATA_PREF, MODE_PRIVATE).edit().remove(requestType).apply();
        return code;
    }

    public long doRequest(RestRequest request, boolean duplicate)  {

        String type = request.getClass().getName();

        List<Long> requestIds;

        if(pendingRequests.containsKey(type)) {
            if(!duplicate && !pendingRequests.get(type).isEmpty())
                return pendingRequests.get(type).get(0);

            requestIds = pendingRequests.get(type);
        } else {
            requestIds = new ArrayList<>();
            pendingRequests.put(type, requestIds);
        }

        long requestId =  generateRequestId();

        requestIds.add(requestId);

        RequestCallback callback = new RequestCallback() {
            @Override
            public void onComplete(long requestId, int code, RestRequest request) {

                for (DataServiceListener listener : listeners) {
                    listener.onRequestComplete(requestId,code, request.getClass().getName());
                }

                onRequestFinish(requestId , code, request.getClass().getName());
            }

            @Override
            public void onFailed(long requestId, RestRequest request) {
                for(DataServiceListener listener : listeners) {
                    listener.onRequestError(requestId, request.getClass().getName());
                }

                onRequestFinish(requestId , -1, request.getClass().getName());
            }

        };

        request.execute(this, requestId, callback);

        return requestId;
    }

    private void onRequestFinish(long requestId , int code, String type) {
        SharedPreferences.Editor sharedPrefs = getSharedPreferences( DATA_PREF , MODE_PRIVATE ).edit();
        sharedPrefs.putInt(type, code);
        sharedPrefs.apply();

        if(pendingRequests.get(type) != null)
            pendingRequests.get(type).remove(requestId);

        if(pendingRequests.get(type) != null && pendingRequests.get(type).isEmpty())
            pendingRequests.remove(type);

        if(pendingRequests.isEmpty()) {
            Log.d("DATA_SERVICE", "Attempt to stop");
            stopSelf();
        }
    }

    private long generateRequestId() {
        return UUID.randomUUID().getLeastSignificantBits();
    }

    public interface DataServiceListener {
        void onRequestComplete(long requestId, int code, String requestType);
        void onRequestError(long requestId, String requestType);
    }
}

package tech.livx.restservice.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.ConcurrentHashMap;

import tech.livx.restservice.requests.RestRequest;
import tech.livx.restservice.services.DataService;

/**
 * Base Activity for any Activity using the RestService
 * Bugs: none known
 *
 * @author Mitch, LivX : livx.tech
 * @version 0.1.0
 */
public abstract class BaseRestAppCompatActivity extends AppCompatActivity implements ServiceConnection, DataService.DataServiceListener {

    private boolean isBound = false;
    private DataService dataService;
    private ConcurrentHashMap<String, Long> currentRequests;
    private Runnable runWhenServiceReady;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            currentRequests = (ConcurrentHashMap<String,Long>)savedInstanceState.getSerializable("currentRequests");
        } else {

            currentRequests = new ConcurrentHashMap<>();
        }

        Intent intent = new Intent(this, DataService.class);
        startService(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("currentRequests", currentRequests);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        DataService.LocalBinder binder = (DataService.LocalBinder) service;
        dataService = binder.getService();
        dataService.addListener(this);

        //Lets go through current requests
        for(String key : currentRequests.keySet()) {

            //Request is still busy
            if(currentRequests.get(key) != 0L) {
                if (dataService.isPending(currentRequests.get(key))) {
                    onLoading();
                } else {
                    int code = dataService.checkResult(key);
                    if (code == -1) {
                        onRequestError(currentRequests.get(key), key);
                    } else if (code > 0) {
                        onRequestComplete(currentRequests.get(key), code, key);
                    }
                }
            }
        }

        isBound = true;

        if(runWhenServiceReady != null) {
            runWhenServiceReady.run();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBound = false;
    }

    protected void doRequest(final RestRequest request, final boolean duplicate) {

        //Make sure the service is started
        Intent intent = new Intent(this, DataService.class);
        startService(intent);

        if (dataService != null && isBound) {

            runWhenServiceReady = null;

            onLoading();
            long requestId = dataService.doRequest(request, duplicate);


            currentRequests.put(request.getClass().getName(), requestId);
            return;
        }


        //Service isnt ready. Lets re run when it is
        runWhenServiceReady = new Runnable() {
            @Override
            public void run() {
                doRequest(request, duplicate);
            }
        };
    }

    @Override
    public void onRequestComplete(long requestId, int code, String type) {
        currentRequests.remove(type);
        onRequestComplete(type, code);
    }

    @Override
    public void onRequestError(long requestId, String type) {
        currentRequests.remove(type);
        onRequestError(type);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound) {
            dataService.removeListener(this);
            unbindService(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, DataService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBound) {
            if(!dataService.anyPending()) {
                Intent intent = new Intent(this, DataService.class);
                startService(intent);
            }
        }
    }

    public abstract void onRequestComplete(String type, int code);
    public abstract void onRequestError(String type);
    public abstract void onLoading();

}
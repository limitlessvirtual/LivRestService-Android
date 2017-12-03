package tech.livx.restservice.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.View;

import tech.livx.restservice.activities.BaseRestAppCompatActivity;

/**
 * Created by dante on 2017/12/02.
 */

public class SideActivity extends BaseRestAppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_side);

        findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onRequestComplete(String type, int code) {

    }

    @Override
    public void onRequestError(String type) {

    }

    @Override
    public void onLoading() {

    }
}

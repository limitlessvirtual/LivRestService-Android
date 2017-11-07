package tech.livx.restservice.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import tech.livx.restservice.activities.BaseRestAppCompatActivity;
import tech.livx.restservice.services.DataService;

/**
 * Description of class
 * <p>
 * Bugs: none known
 *
 * @author Mitch, LivX : livx.tech
 * @version 1.0
 * @date 2017/04/18
 */
public class MainActivity extends BaseRestAppCompatActivity {

    private ProgressBar progressBar;
    private Button requestButton;
    private TextView resultTextView;
    private DataService dataService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.progressBar = (ProgressBar) findViewById(R.id.progress);
        this.requestButton = (Button) findViewById(R.id.button_request);
        this.resultTextView = (TextView) findViewById(R.id.text_result);

        progressBar.setVisibility(View.INVISIBLE);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRequest(new WeatherRequest(), false);
                resultTextView.setText("");
            }
        });

        this.sharedPreferences = getSharedPreferences("weather", MODE_PRIVATE);
    }

    @Override
    public void onRequestComplete(String type, int code) {
        progressBar.setVisibility(View.INVISIBLE);

        if(code == 200) {
            resultTextView.setText(sharedPreferences.getString("result", "Could not retrieve data"));
        } else {
            resultTextView.setText("Error");
        }
    }

    @Override
    public void onRequestError(String type) {
        progressBar.setVisibility(View.INVISIBLE);
        resultTextView.setText("Error");
    }

    @Override
    public void onLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }
}

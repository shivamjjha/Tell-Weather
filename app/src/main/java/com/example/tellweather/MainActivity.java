package com.example.tellweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Button tellButton;
    static TextView temperatureTextView, mainTextView, descriptionTextView;
    EditText cityNameEditText;
    static String add;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tellButton = findViewById(R.id.tellButton);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        mainTextView = findViewById(R.id.mainTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        cityNameEditText = findViewById(R.id.cityNameEditText);

        temperatureTextView.setText("");
        mainTextView.setText("");
        cityNameEditText.setText("");
        descriptionTextView.setText("");
    }

    public void tell(View view) {
        if (isNetworkAvailable()){
            add = cityNameEditText.getText().toString();

            temperatureTextView.setText("");
            mainTextView.setText("");
            descriptionTextView.setText("");

            String result = null;

            if (!add.equals("")) {
                TellWeather task = new TellWeather();

                try {
                    result = task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + add + "&appid=b5b1de59f48fba474e6b48b1a9225500").get();

            /*catch (FileNotFoundException e){
                e.printStackTrace();
                Log.i("Error Occurred!", "Error in executing task!");
                Toast.makeText(this, "City Not Found!, Enter Again", Toast.LENGTH_SHORT).show();
            } catch(Exception e) {

            }*/
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    // MainActivity.descriptionTextView.setText(R.string.no_internet);
                }
            } else {
                Toast.makeText(this, "Enter City Name First!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Check Your Internet Connection Please!!", Toast.LENGTH_LONG).show();
        }
    }

    static private class TellWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String connectionResult = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int intData = inputStreamReader.read();

                while (intData != -1) {
                    connectionResult += (char) intData;
                    intData = inputStreamReader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
                 // connectionResult = null;
                return null;
            }
            Log.i("JSON", connectionResult);
            return connectionResult;
        }

        @Override
        protected void onPostExecute(String returnedResult) {
            if(!MainActivity.add.equals("")) {
                super.onPostExecute(returnedResult);

                try {
                    JSONObject jsonObject = new JSONObject(returnedResult);
                    String weatherInfo = jsonObject.getString("weather");
                    Log.i("Weather", weatherInfo);
                    JSONArray jsonArray = new JSONArray(weatherInfo);
                    JSONObject main = new JSONObject(jsonObject.getString("main"));

                    Log.i("Main", weatherInfo);

                    // MainActivity.setTextViews((String) jsonArray2.get(0), (String) jsonArray.get(1), (String) jsonArray.get(2));

                        MainActivity.temperatureTextView.setText(String.format("%.5s %s", (Double.parseDouble(main.getString("temp")) - 273.15), "\u2103"));
                        MainActivity.mainTextView.setText(jsonArray.getJSONObject(0).getString("main"));
                        MainActivity.descriptionTextView.setText(jsonArray.getJSONObject(0).getString("description"));
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.temperatureTextView.setText(R.string.city_not_found);
                    MainActivity.descriptionTextView.setText(R.string.try_again);
                    //MainActivity.mainTextView.setText(R.string.no_internet);
                }
            }
        }
    }
}


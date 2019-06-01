package com.geoape.backgroundlocationexample;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CallAPI extends AsyncTask<String, String, String> {


    public CallAPI() {

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void onPostExecute(String result) {

        Toast.makeText(MainActivity.mainContext, result, Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(String... params) {
        String result="";

//        String myurldata = "store_data=1&event=Android&uuid=999&path=[[1,2],[3,4]]";
//        String urlString = "http://dam.dotgiscorp.com/tests/test01.php";

        String urlString = params[0];
        String myurldata = params[1];


        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.getOutputStream().write(myurldata.getBytes());

            int response = connection.getResponseCode();

            Log.d("RESPUESTA CODE ", "" + response);

            if (response == HttpURLConnection.HTTP_OK) {
                String line;
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                result = builder.toString();

                Log.d("RESPUESTA POST ", "" + result);

                result = "POST OK";

            }else{

                result = "POST ERROR";

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}

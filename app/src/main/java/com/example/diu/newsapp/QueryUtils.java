package com.example.diu.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Query the USGS dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        //force the background thread sleep for 2 seconds
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<News> news = extractFeatureFromJsonForInfo(jsonResponse);
        Log.v(LOG_TAG,"fetchEarthquakeData");

        // Return the list of {@link Earthquake}s
        return news;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String StringUrl) {
        URL url=null;
        try {
            url=new URL(StringUrl);
        }
        catch (MalformedURLException e){
            Log.e(LOG_TAG,"Error with creating url",e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url)throws IOException {
        String jsonRespons="";
        if(url==null){
            return jsonRespons;
        }
        HttpURLConnection httpURLConnection=null;
        InputStream inputStream=null;
        try{
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setReadTimeout(10000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if(httpURLConnection.getResponseCode()==200){
                inputStream=httpURLConnection.getInputStream();
                jsonRespons=readFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG, "Error response code: " + httpURLConnection.getResponseCode());
            }
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }
        finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonRespons;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream)throws IOException {
        StringBuilder output=new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();

    }

    /**
     * Return an {@link News} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */

    public static List<News> extractFeatureFromJsonForInfo(String newsJSON){
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)){
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        List<News> newses= new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONObject response = baseJsonResponse.getJSONObject("response");
            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            JSONArray newsArray = response.getJSONArray("results");

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentNews = newsArray.getJSONObject(i);

                // For a given earthquake, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that earthquake.
                String newsTitle=currentNews.getString("webTitle");
                String newsSectionName=currentNews.getString("sectionName");
                String publishDate=currentNews.getString("webPublicationDate");
                String newsUrl=currentNews.getString("webUrl");
                // Create a new {@link Earthquake} object with the magnitude, location, time,
                // and url from the JSON response.
                News news = new News(newsTitle, newsSectionName, publishDate, newsUrl);

                // Add the new {@link Earthquake} to the list of earthquakes.
                newses.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return newses;
    }
}

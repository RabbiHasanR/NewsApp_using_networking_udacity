package com.example.diu.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    public static final String LOG_TAG = NewsActivity.class.getName();

    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter newsAdapter;
    private TextView mEmptyStateTextView;
    private ProgressBar mLoadingSpinner;
    private ListView newsListView;

    /** URL for earthquake data from the NEWS dataset */
    private static final String NEWS_REQUEST_URL =
            "https://content.guardianapis.com/search?q=debates&api-key=test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        findView();
        checkNetworkConnectivity();
        updateUi();
    }

    public void findView(){
        newsListView = (ListView) findViewById(R.id.listView);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mLoadingSpinner=(ProgressBar)findViewById(R.id.loading_spinner);
    }

    /**
     * Update the UI with the given earthquake information.
     */
    public void updateUi(){
        // Find a reference to the {@link ListView} in the layout
        newsListView.setEmptyView(mEmptyStateTextView);
        // Create a new adapter that takes an empty list of earthquakes as input
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(newsAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                News currentNews = newsAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getNews_url());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


    }


    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG,"onCreateLoader");
        return new NewsLoader(this,NEWS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        //set progress bar visibility gone when background loading finish
        mLoadingSpinner.setVisibility(View.GONE);
        // Set empty state text to display "No news found."
        mEmptyStateTextView.setText(R.string.no_news);
        // Clear the adapter of previous earthquake data
        newsAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            newsAdapter.addAll(news);
            Log.v(LOG_TAG,"onLoadFinished");
        }

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        newsAdapter.clear();
        Log.v(LOG_TAG,"onLoadReset");
    }

    public void checkNetworkConnectivity(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        //boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        if(isConnected ){
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            Log.v(LOG_TAG,"initLoader");
        }
        else{
            mEmptyStateTextView.setText(R.string.no_internet);
            mLoadingSpinner.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }
}

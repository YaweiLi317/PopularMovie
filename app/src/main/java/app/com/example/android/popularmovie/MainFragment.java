package app.com.example.android.popularmovie;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private ImageAdapter mImageAdatper;
    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mImageAdatper = new ImageAdapter(getActivity());
        GridView gridView = (GridView)rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(mImageAdatper);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "" + i,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }
    //start a asynctask to get data
    public void updatMovie() {
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute("popular");
    }

    @Override
    public void onStart() {
        super.onStart();
        updatMovie();
    }

    class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            
            try {
                final String APPID_PARAM = "api_key";
                Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendEncodedPath(params[0])
                        .appendQueryParameter(APPID_PARAM, "Your API_KEY")
                        .build();
                URL url = new URL(buildUri.toString());
                Log.v(LOG_TAG, "Built URI " + buildUri.toString());

                //open connection
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Jason Data " + buffer.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    }catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovidDataFromJson(movieJsonStr);
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        //get movie info from jason
        private String[] getMovidDataFromJson(String movieJasonStr)
                throws JSONException {
            final String MOVIE_RESULTS = "results";
            JSONObject movieJason = new JSONObject(movieJasonStr);
            JSONArray movieArray = movieJason.getJSONArray(MOVIE_RESULTS);
            String[] imagePath = new String[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.optJSONObject(i);
                imagePath[i] = IMAGE_BASE_URL + movie.optString("poster_path");
            }
            return imagePath;
        }


        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mImageAdatper.setUrls(result);
            }
        }
    }

}


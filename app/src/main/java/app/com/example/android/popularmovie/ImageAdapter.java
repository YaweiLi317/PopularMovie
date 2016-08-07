package app.com.example.android.popularmovie;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by iamle on 8/5/2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mUrls;
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        if (mUrls == null || mUrls.length == 0) {
            return 0;
        }
        return mUrls.length;
    }

    public Object getItem(int position) {
        if (mUrls == null || mUrls.length == 0) {
            return null;
        } else if (position < mUrls.length) {
            return mUrls[position];
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }
    public void setUrls(String[] urls) {
        mUrls = urls;
    }
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView)convertView;
        if (imageView == null) {
            imageView = new ImageView(mContext);
        }
        imageView.setAdjustViewBounds(true);
        Picasso.with(mContext).load(mUrls[position]).into(imageView);
        Log.v("URL:",mUrls[position]);
        return imageView;
    }
}

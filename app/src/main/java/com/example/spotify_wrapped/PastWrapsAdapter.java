package com.example.spotify_wrapped;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.spotify_wrapped.Wrap;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PastWrapsAdapter extends BaseAdapter{
    private Activity context;
    private FragmentManager fragmentManager;
    private LinkedHashMap<String, Wrap> wraps;
    LayoutInflater inflater;


    public PastWrapsAdapter(Activity context, LinkedHashMap<String, Wrap> wraps, FragmentManager fragmentManager) {
        this.context = context;
        this.wraps = wraps;
        inflater = (LayoutInflater.from(context));
        this.fragmentManager = fragmentManager;

    }

    @Override
    public int getCount() {
        return wraps.size();
    }

    @Override
    public Object getItem(int position) {
        return wraps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardView pastWrap;
        pastWrap = (CardView) inflater.inflate(R.layout.wrap_thumbnail, parent, false);

        new DownloadImageFromInternet((ImageView) pastWrap.findViewById(R.id.top_artist_img), context).execute(wraps.get(String.valueOf(position)).getTopArtists().get("1").getImage());
        pastWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WrapFragment clickedWrap = new WrapFragment(wraps.get(String.valueOf(position)));
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, clickedWrap);
                fragmentTransaction.commit();
            }
        });


        return pastWrap;
    }
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        Activity context;

        public DownloadImageFromInternet(ImageView imageView, Activity context) {
            this.imageView = imageView;
            this.context = context;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

}

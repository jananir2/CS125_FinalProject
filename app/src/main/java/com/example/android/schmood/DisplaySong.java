package com.example.android.schmood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DisplaySong extends AppCompatActivity {
    private static final String TAG = "SongsList";
    JsonArray dataSongs;

    ArrayList<String> metadataSongs = new ArrayList<String>();
    String songsQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_song);
        Intent intent = getIntent();
        Button back = findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DisplaySong.this, MainActivity.class);
                startActivity(intent);
            }
        });



        songsQuery = "http://musicovery.com/api/V6/playlist.php?&fct=getfrommood"
                + "&trackarousal="  + intent.getStringExtra("intFeel")
                + "&trackvalence=" + intent.getStringExtra("intEnergy")
                + "&listenercountry=us";
        getSongs(songsQuery);



//        Log.i(TAG, dataSongs.toString());

    }

    private void getSongs(String songsQuery) {
        Log.i(TAG, "Gettings songs");
        final RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest basicInfoReq = new StringRequest(Request.Method.GET, songsQuery,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Res rec'd");
                        JsonObject res = new Gson().fromJson(response, JsonObject.class);
                        Log.i(TAG, res.toString());
                        dataSongs = res.get("tracks").getAsJsonObject().get("track").getAsJsonArray();
                        for (int i = 0; i < dataSongs.size(); i++) {
                            final JsonElement song = dataSongs.get(i);
                            String artistFormatted = song.getAsJsonObject().get("artist_display_name").getAsString().replace(" ", "+");
                            String titleFormatted = song.getAsJsonObject().get("title").getAsString().replace(" ", "+");
                            final String artist = song.getAsJsonObject().get("artist_display_name").getAsString();
                            final String title = song.getAsJsonObject().get("title").getAsString();
                            final String metadataQuery = "http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=9fb556f623b6c3895a0980a1b743b66c" +
                                    "&artist=" + artistFormatted +
                                    "&track=" + titleFormatted +
                                    "&format=json";
                            Log.i(TAG, metadataQuery);

                            StringRequest artLinksReq = new StringRequest(Request.Method.GET, metadataQuery,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            final JsonObject resJson = new Gson().fromJson(response, JsonObject.class);
                                            Log.i(TAG, resJson.toString());


                                            //ADD TO PRENT
                                            LinearLayout parent = findViewById(R.id.listSongs);
                                            View songChunk = getLayoutInflater().inflate(R.layout.chunk_info, parent, false);

                                            Button btnListen = songChunk.findViewById(R.id.btnListen);


                                            btnListen.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    if (resJson.get("track") != null) {
                                                        Intent browserIntent = new Intent(
                                                                Intent.ACTION_VIEW,
                                                                Uri.parse(resJson.get("track").getAsJsonObject().get("url").getAsString()));
                                                        startActivity(browserIntent);
                                                    } else {
                                                        Intent browserIntent = new Intent(
                                                                Intent.ACTION_VIEW,
                                                                Uri.parse("https://www.last.fm"));
                                                        startActivity(browserIntent);
                                                    }
                                                }
                                            });

                                            ImageView imgAlbumArt = songChunk.findViewById(R.id.imgAlbum);

                                            if (resJson.get("track") != null
                                            && resJson.get("track").getAsJsonObject().get("album") != null
                                            && resJson.get("track").getAsJsonObject().get("album").getAsJsonObject()
                                                    .get("image") != null
                                            && resJson.get("track").getAsJsonObject().get("album").getAsJsonObject()
                                                    .get("image").getAsJsonArray().get(2) != null
                                            && !(resJson.get("track").getAsJsonObject().get("album").getAsJsonObject()
                                                    .get("image").getAsJsonArray().get(2).getAsJsonObject().get("#text").getAsString().equals(""))) {
                                                Log.i(TAG, "IMAGE LINK: " +
                                                        resJson.get("track").getAsJsonObject()
                                                        .get("album").getAsJsonObject()
                                                        .get("image").getAsJsonArray().get(2).getAsJsonObject().get("#text").getAsString());
                                                String imgLink = resJson.get("track").getAsJsonObject()
                                                        .get("album").getAsJsonObject()
                                                        .get("image").getAsJsonArray().get(2).getAsJsonObject().get("#text").getAsString();
                                                Picasso.get().load(imgLink).into(imgAlbumArt);
                                            }


                                            TextView txtSongTitle = songChunk.findViewById(R.id.txtTitle);
                                            Log.i(TAG, "title is: " + title);
                                            txtSongTitle.setText(title);

                                            TextView txtArtistName = songChunk.findViewById(R.id.txtArtist);

                                            Log.i(TAG, "artist is: " + artist);
                                            txtArtistName.setText(artist);

//                                                    Picasso.get().load(imgLink).into(imgAlbumArt);

                                            parent.addView(songChunk);
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i(TAG, "RUHROH");
                                    Log.i(TAG, "Uhh so this shit goofed: " + error.toString());
                                }
                            });
                            queue.add(artLinksReq);
//                            LinearLayout parent = findViewById(R.id.listSongs);
//                            parent.removeAllViews();
//                            for (int i = 0; i < dataSongs.size(); i++) {
//                                View songChunk = getLayoutInflater().inflate(R.layout.chunk_info, parent, false);
//
//                                TextView txtSongTitle = songChunk.findViewById(R.id.txtTitle);
//                                Log.i(TAG, "title is: " + song.getAsJsonObject().get("title").getAsString());
//                                txtSongTitle.setText(song.getAsJsonObject().get("title").getAsString());
//
//                                TextView txtArtistName = songChunk.findViewById(R.id.txtArtist);
//
//                                Log.i(TAG, "title is: " + song.getAsJsonObject().get("artist_display_name").getAsString());
//                                txtArtistName.setText(song.getAsJsonObject().get("artist_display_name").getAsString());
//
//                                ImageView imgAlbumArt = songChunk.findViewById(R.id.imgAlbum);
//                                Picasso.get().load(metadataSongs.get(i)).into(imgAlbumArt);
//
//                                parent.addView(songChunk);
//                            }
                        }

                        Log.v(TAG, dataSongs.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "RUHROH");
                Log.i(TAG, "Uhh so this shit goofed: " + error.toString());
            }
        });
        queue.add(basicInfoReq);



        Log.i(TAG, metadataSongs.toString());
        Log.i(TAG, "Got here??");

// Add the request to the RequestQueue.
    }
}
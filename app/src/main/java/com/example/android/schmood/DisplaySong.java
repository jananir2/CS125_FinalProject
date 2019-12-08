package com.example.android.schmood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DisplaySong extends AppCompatActivity {
    private static final String TAG = "SongsList";
    JsonArray dataSongs;
    String query;
    LinearLayout parent;

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


        query = "http://musicovery.com/api/V6/playlist.php?&fct=getfrommood"
                + "&trackarousal="  + intent.getStringExtra("intFeel")
                + "&trackvalence=" + intent.getStringExtra("intEnergy")
                + "&listenercountry=us";
        Log.i(TAG, query);
        getSongs(query);


//        Log.i(TAG, dataSongs.toString());

    }

    private void getSongs(String query) {
        Log.i(TAG, "Gettings songs");
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Res rec'd");
                        JsonObject res = new Gson().fromJson(response, JsonObject.class);
                        Log.i(TAG, res.toString());
                        dataSongs = res.get("tracks").getAsJsonObject().get("track").getAsJsonArray();
                        Log.v(TAG, dataSongs.toString());
                        parent = findViewById(R.id.listSongs);
                        for (JsonElement song : dataSongs) {
                            View songChunk = getLayoutInflater().inflate(R.layout.chunk_info, parent, false);

                            TextView txtSongTitle = songChunk.findViewById(R.id.txtTitle);
                            Log.i(TAG, "title is: " + song.getAsJsonObject().get("title").getAsString());
                            txtSongTitle.setText(song.getAsJsonObject().get("title").getAsString());

                            TextView txtArtistName = songChunk.findViewById(R.id.txtArtist);

                            Log.i(TAG, "title is: " + song.getAsJsonObject().get("artist_display_name").getAsString());
                            txtArtistName.setText(song.getAsJsonObject().get("artist_display_name").getAsString());

                            parent.addView(songChunk);
                            Log.i(TAG, "Added chonk: " + songChunk);
                            Log.i(TAG, "Views: " + parent.getChildCount());
                            Log.i(TAG, "Check check: " + parent.getChildAt(parent.getChildCount() - 1).findViewById(R.id.txtArtist));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "RUHROH");
                Log.i(TAG, "Uhh so this shit goofed: " + error.toString());
            }
        });
        Log.i(TAG, "Got here??");

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
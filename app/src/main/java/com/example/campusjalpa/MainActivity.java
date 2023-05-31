package com.example.campusjalpa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> items;
    private SwipeRefreshLayout refreshLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_layout);

        // Configuración de la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        // Configuración del SwipeRefreshLayout
        refreshLayout = findViewById(R.id.refreshL);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Finalizar la actividad y abrir la actividad principal
                finish();
                launchMain();
            }
        });

        // Configuración del RecyclerView y el adaptador
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
        itemAdapter = new ItemAdapter(items, this);
        recyclerView.setAdapter(itemAdapter);

        // Realizar la solicitud para obtener datos
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://campusjalpa2023.000webhostapp.com/conn.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Procesar los datos JSON y agregar los elementos a la lista
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String title = jsonObject.getString("imagen_nom");
                                String description = jsonObject.getString("imagen_info");
                                String imageUrl = jsonObject.getString("imagen_url");

                                Item item = new Item(title, description, imageUrl);
                                items.add(item);
                            }
                            // Notificar al adaptador que los datos han cambiado
                            itemAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Mandar mensajes de error
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("MiApp", "" + error.getMessage());
                    }
                });

        queue.add(jsonArrayRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú de la barra de la aplicación
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar las opcines del menu
        finish();
        Intent myIntent = new Intent(this, UserSettings.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void launchMain() {
        // Abrir la actividad principal
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

    public void launchImageUpload(View view) {
        // Abrir la actividad de subir imagenes
        Intent myIntent = new Intent(view.getContext(), UploadActivity.class);
        startActivityForResult(myIntent, 0);
    }
}
package com.example.campusjalpa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSettings extends Activity {
    SharedPreferences sp;
    private RequestQueue rQueue;
    private EditText nom;
    private EditText con;

    Button mDialogButton;
    TextView okay_text, cancel_text;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        nom = findViewById(R.id.txtNomUs);
        con = findViewById(R.id.txtPassUs);
    }

    // Método para cerrar sesión
    public void logOut(View view) {
        SharedPreferences.Editor preferencesEditor = sp.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();
        finish();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Método para eliminar un usuario
    public void deleteUser() {
        rQueue = Volley.newRequestQueue(this);
        int usBorrar = sp.getInt("id", 0);

        String url = "https://campusjalpa2023.000webhostapp.com/eliminar.php?id_usuario=" + usBorrar;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(UserSettings.this, "La cuenta se ha eliminado!", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor preferencesEditor = sp.edit();
                                preferencesEditor.clear();
                                preferencesEditor.apply();
                                finish();
                                launchLogin();
                            } else {
                                Toast.makeText(UserSettings.this, "La cuenta no se ha podido eliminar", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        rQueue.add(request);
    }

    // Método para abrir la actividad de inicio de sesión
    public void launchLogin() {
        finish();
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
    }

    // Método para actualizar el nombre de usuario
    public void updateName(View view) {
        if (nom.toString().isEmpty() || nom.toString().length() < 3) {
            Toast.makeText(UserSettings.this, "Nombre de usuario invalido", Toast.LENGTH_SHORT).show();
        } else {
            rQueue = Volley.newRequestQueue(this);

            int idUsuarioToUpdate = sp.getInt("id", 0);
            String newNomUsuario = nom.getText().toString();

            String url = "https://campusjalpa2023.000webhostapp.com/actualizar.php?id_usuario=" + idUsuarioToUpdate
                    + "&nom_usuario=" + newNomUsuario;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                if (success) {
                                    Toast.makeText(UserSettings.this, "Se ha actualizado el nombre!", Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor preferencesEditor = sp.edit();
                                    preferencesEditor.clear();
                                    preferencesEditor.apply();
                                    finish();
                                    launchLogin();
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });

            rQueue.add(request);
        }
    }

    // Método para actualizar la contraseña
    public void updatePass(View view) {
        if (con.toString().isEmpty() || con.toString().length() < 8) {
            Toast.makeText(UserSettings.this, "Contraseña invalida", Toast.LENGTH_SHORT).show();
        } else {
            rQueue = Volley.newRequestQueue(this);

            int idUsuarioToUpdate = sp.getInt("id", 0);
            String newConUsuario = con.getText().toString();

            String url = "https://campusjalpa2023.000webhostapp.com/actcon.php?id_usuario=" + idUsuarioToUpdate
                    + "&con_usuario=" + newConUsuario;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                if (success) {
                                    Toast.makeText(UserSettings.this, "Se ha actualizado la contraseña!", Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor preferencesEditor = sp.edit();
                                    preferencesEditor.clear();
                                    preferencesEditor.apply();
                                    finish();
                                    launchLogin();
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });

            rQueue.add(request);
        }
    }

    // Mostrar el diálogo de confirmación antes de eliminar la cuenta
    public void deleteDialog(View view) {
        mDialogButton = findViewById(R.id.eliminarBtn);
        Dialog dialog = new Dialog(UserSettings.this);

        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        okay_text = dialog.findViewById(R.id.okay_text);
        cancel_text = dialog.findViewById(R.id.cancel_text);

        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteUser();
            }
        });

        cancel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(UserSettings.this, "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}



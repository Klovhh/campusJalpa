package com.example.campusjalpa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private RequestQueue rQueue;
    private EditText nomUs;
    private EditText conUs;
    private String nomUsuario;
    private String conUsuario;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Referencias de los elementos de la interfaz
        nomUs = findViewById(R.id.nombreUsuarioTxf);
        conUs = findViewById(R.id.contrasenaTxf);

        // Obtener instancia de SharedPreferences para almacenar datos de usuario
        sp = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        // Verificar si ya se ha iniciado sesión anteriormente
        if (sp.contains("id")) {
            Toast.makeText(LoginActivity.this, "Bienvenido " + (sp.getString("name", "")), Toast.LENGTH_LONG).show();
            launchMain();
        }
    }

    private void loginUsuario() {
        // Obtener el nombre de usuario y contraseña que se ingresen
        nomUsuario = nomUs.getText().toString();
        conUsuario = conUs.getText().toString();
        rQueue = Volley.newRequestQueue(this);

        String url = "https://campusjalpa2023.000webhostapp.com/us.php"; //?nom_usuario=" + nomUsuario + "&con_usuario=" + conUsuario;

        // Crear una solicitud para obtener un JSONArray desde la URL
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject usuarioObject = response.getJSONObject(i);
                                String nomUsuarioResponse = usuarioObject.getString("nom_usuario");
                                String conUsuarioResponse = usuarioObject.getString("con_usuario");
                                int idUsuarioResponse = usuarioObject.getInt("id_usuario");

                                // Verificar si el nombre de usuario y contraseña coinciden con los datos obtenidos
                                if (nomUsuario.equals(nomUsuarioResponse) && conUsuario.equals(conUsuarioResponse)) {
                                    // Almacenar los datos de usuario en SharedPreferences
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("name", nomUsuario);
                                    editor.putString("pass", conUsuario);
                                    editor.putInt("id", idUsuarioResponse);
                                    editor.putInt("key", 1);
                                    editor.commit();

                                    // Mostrar mensaje de bienvenida
                                    Toast.makeText(LoginActivity.this, "Bienvenido " + nomUsuario, Toast.LENGTH_LONG).show();
                                    launchMain();
                                    return;
                                }
                            }

                            // Mostrar mensaje de error si no se encontró el usuario o la contraseña es incorrecta
                            Toast.makeText(LoginActivity.this, "Hay algún problema, intenta otra vez o regístrate", Toast.LENGTH_LONG).show();
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

        // Agregar la solicitud a la cola
        rQueue.add(request);
    }

    public void launchUserRegister(View view) {
        // Abrir la actividad de registro de usuario
        Intent myIntent = new Intent(view.getContext(), RegisterActivity.class);
        startActivityForResult(myIntent, 0);
    }

    public void loginUser(View view) {
        // Iniciar sesión de usuario
        loginUsuario();
    }

    public void launchMain() {
        // Finalizar la actividad y abrir la actividad principal
        finish();
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
    }
}
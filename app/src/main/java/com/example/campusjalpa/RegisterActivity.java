package com.example.campusjalpa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends Activity {
    private static final String SERVER_URL = "https://campusjalpa2023.000webhostapp.com/registrar.php";

    private EditText user;
    private EditText num;
    private EditText pass;
    private EditText pass2;

    private String FinalUser;
    private String FinalNum;
    private String FinalPass;

    private Integer Correct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Referenciar los elementos de la interfaz
        user = findViewById(R.id.nombreUsuarioTxf);
        num = findViewById(R.id.numeroTxf);
        pass = findViewById(R.id.contrasenaTxf);
        pass2 = findViewById(R.id.contrasenaTxf3);
    }

    public void RegUserForLogin(View view) {
        Correct = 0;

        // Validar los datos ingresados del usuario
        if (user.length() <= 2) {
            Toast.makeText(RegisterActivity.this, "El nombre debe tener al menos 3 caracteres", Toast.LENGTH_LONG).show();
            Correct = 0;
        } else {
            FinalUser = user.getText().toString();
            Correct = Correct + 1;
            if (num.length() < 10) {
                Toast.makeText(RegisterActivity.this, "El número telefónico debe tener al menos 10 dígitos", Toast.LENGTH_LONG).show();
                Correct = 0;
            } else {
                FinalNum = num.getText().toString();
                Correct = Correct + 1;
                if (pass.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_LONG).show();
                    Correct = 0;
                } else {
                    if (pass.getText().toString().equals(pass2.getText().toString())) {
                        FinalPass = pass2.getText().toString();
                        Correct = Correct + 1;
                        if (Correct == 3) {
                            // Subir los datos y abrir la actividad de inicio de sesión
                            uploadData(FinalUser, FinalNum, FinalPass);
                            finish();
                            Intent i = new Intent(this, LoginActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Falta completar un campo", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    public void uploadData(String data1, String data2, String data3) {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Crear la cadena de datos a enviar
            String postData = "data1=" + URLEncoder.encode(data1, "UTF-8") + "&"
                    + "data2=" + URLEncoder.encode(data2, "UTF-8") + "&"
                    + "data3=" + URLEncoder.encode(data3, "UTF-8");

            // Subir los datos
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(postData);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();

            writer.close();
            reader.close();

            if (response.equals("SUCCESS")) {
                Toast.makeText(RegisterActivity.this, "¡Usuario registrado exitosamente! Por favor inicia sesion", Toast.LENGTH_LONG).show();
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
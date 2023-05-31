package com.example.campusjalpa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class UploadActivity extends Activity {
    //Configuración de FTP y el servidor
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
    private static final int CAMERA_REQUEST = 1888;
    private static final String SERVER = "files.000webhost.com";
    private static final String USERNAME = "campusjalpa2023";
    private static final String PASSWORD = "NgY?PV_WAH3S+3g";
    private static final String REMOTE_PATH = "/public_html/images/";
    private static final String SERVER_URL = "https://campusjalpa2023.000webhostapp.com/subir.php";

    // Elementos de la interfaz de usuario
    private ImageView imageView;
    private Button captureButton;
    private Button uploadButton;
    private Bitmap bitmap;

    private TextView name;
    private EditText info;

    // Variables para almacenar los datos ingresados por el usuario
    private String nombreTxt;
    private String infoTxt;
    private String nombreFoto;
    private String nombreUser;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Obtener referencias a los elementos de la interfaz
        imageView = findViewById(R.id.imageView);
        captureButton = findViewById(R.id.captureButton);
        uploadButton = findViewById(R.id.uploadButton);
        name = findViewById(R.id.userLbl);
        info = findViewById(R.id.editTextTextMultiLine);

        // Obtener el nombre de usuario almacenado en SharedPreferences
        SharedPreferences sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        nombreUser = sp.getString("name", "");
        name.setText(nombreUser);

        // Abrir la cámara para tomar una foto
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

        // Configurar el botón de carga
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bitmap != null) {
                    uploadButton.setEnabled(false);
                    uploadBitmap(bitmap);
                    nombreTxt = nombreUser;
                    infoTxt = info.getText().toString();
                    Integer idUser = sp.getInt("id", 0);
                    uploadData(nombreTxt, infoTxt, nombreFoto, idUser);
                } else {
                    Toast.makeText(UploadActivity.this, "Por favor toma una foto primero!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método que se llama cuando se obtiene una respuesta de la cámara
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }

    // Método para cargar una imagen al servidor FTP
    private void uploadBitmap(Bitmap bitmap) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(SERVER);
            ftpClient.login(USERNAME, PASSWORD);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                Log.e("FTP", "Servidor negó la conexión.");
                return;
            }

            // Generar un nombre de archivo aleatorio
            String filename = getRandomString();
            filename = filename + ".jpeg";
            nombreFoto = "https://campusjalpa2023.000webhostapp.com/images/" + filename;

            // Convertir el bitmap en una entrada de flujo de datos
            InputStream inputStream = convertBitmapToInputStream(bitmap);
            ftpClient.storeFile(REMOTE_PATH + filename, inputStream);
            inputStream.close();

            ftpClient.logout();
            ftpClient.disconnect();
            Toast.makeText(UploadActivity.this, "¡La foto se ha subido!", Toast.LENGTH_SHORT).show();
            finish();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } catch (SocketException e) {
            Log.e("FTP", "SocketException:" + e.getMessage());
            e.printStackTrace();
        } catch (UnknownHostException e) {
            Log.e("FTP", "UnknownHostException:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("FTP", "IOException:" + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para cargar datos adicionales al servidor
    public void uploadData(String data1, String data2, String data3, Integer data4) {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Codificar los datos en el cuerpo de la solicitud
            String postData = "data1=" + URLEncoder.encode(data1, "UTF-8") + "&"
                    + "data2=" + URLEncoder.encode(data2, "UTF-8") + "&"
                    + "data3=" + URLEncoder.encode(data3, "UTF-8") + "&"
                    + "data4=" + URLEncoder.encode(String.valueOf(data4), "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(postData);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();

            writer.close();
            reader.close();

            if (response.equals("SUCCESS")) {
                // La carga de datos fue exitosa
            } else {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para convertir un bitmap en una entrada de flujo de datos
    private InputStream convertBitmapToInputStream(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return new ByteArrayInputStream(byteArray);
    }

    // Método para generar una cadena de caracteres aleatoria
    private static String getRandomString() {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(9);
        for (int i = 0; i < 9; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}

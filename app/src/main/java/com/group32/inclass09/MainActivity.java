package com.group32.inclass09;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private String token = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //signup();
            //login();
            //getImageFromGallery();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button buttonSubmit = (Button) findViewById(R.id.buttonLogin);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.editName)).getText().toString();
                String password = ((EditText) findViewById(R.id.editPassword)).getText().toString();

                try {
                    login(username,password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button buttonSignup = (Button) findViewById(R.id.buttonSignup);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });




    }

        public void login(String username, String password) throws Exception {
            RequestBody requestBody = new FormBody.Builder()
                    .add("email", username)
                    .add("password", password)
                    .build();

            Request request = new Request.Builder()
                    .url("http://ec2-54-166-14-133.compute-1.amazonaws.com/api/login")
                    .addHeader("Content-Type","application/x-www-form-urlencoded")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {

                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response.body().string());
                        if (jsonResponse.getString("status").equals("ok")) {
                            token = jsonResponse.getString("token");
                            String loggedUserName = jsonResponse.getString("userFname") + " " + jsonResponse.getString("userLname");
                            Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                            intent.putExtra("username",loggedUserName);
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", token);
                            editor.commit();
                            startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }


    public String getFile() throws Exception {
        Request request = new Request.Builder()
                .url("http://ec2-54-166-14-133.compute-1.amazonaws.com/api/file/sgSDTag")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response.body().string());

                    if (jsonResponse.getString("status").equals("ok")) {
                        token = jsonResponse.getString("token");
                        MainActivity.this.token = token;
                        Log.d("test",token);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }





            }
        });

        return null;
    }



    public void getMessages()throws Exception {
        Request request = new Request.Builder()
                .url("http://ec2-54-166-14-133.compute-1.amazonaws.com/api/messages")
                .addHeader("Authorization","BEARER " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {

                if(response.isSuccessful())
                {
                    Log.d("messages",response.toString());
                }

            }
        });
    }

    public void PostMessage() throws Exception {
        RequestBody requestBody = new FormBody.Builder()
                .add("Type", "TEXT")
                .add("Comment", "Hey")
                .add("FileThumbnailId", "")
                .build();
        Request request = new Request.Builder()
                .url("http://ec2-54-166-14-133.compute-1.amazonaws.com/api/message/add")
                .addHeader("Authorization", "BEARER " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    Log.d("post message", response.toString());
                } else {
                    Log.d("post message", response.toString());
                }

            }
        });
    }




    private String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

}

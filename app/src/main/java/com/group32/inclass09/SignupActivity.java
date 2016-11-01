package com.group32.inclass09;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private String token = "test";

    private EditText firstNameText, lastNameText, emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstNameText = (EditText) findViewById(R.id.editFirstName);
        lastNameText = (EditText) findViewById(R.id.editLastName);
        emailText = (EditText) findViewById(R.id.editEmail);
        passwordText = (EditText) findViewById(R.id.editPassword);


        Button buttonSignup = (Button) findViewById(R.id.buttonSignup);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String firstName =firstNameText.getText().toString();
                    String lastName = lastNameText.getText().toString();
                    String email = emailText.getText().toString();
                    String password = passwordText.getText().toString();

                    signup(firstName,lastName,email,password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void signup(String firstName, String lastName,String email,String password) throws Exception {

        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("fname", firstName)
                .add("lname", lastName)
                .build();

        Request request = new Request.Builder()
                .url("http://ec2-54-166-14-133.compute-1.amazonaws.com/api/signup")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonResponse = new JSONObject(response.body().string());

                    if (jsonResponse.getString("status").equals("ok")) {
                        token = jsonResponse.getString("token");
                        String loggedUserName = jsonResponse.getString("userFname") + " " + jsonResponse.getString("userLname");
                        SignupActivity.this.token = token;
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token",token);
                        editor.commit();

                        //Toast.makeText(SignupActivity.this,"User Created",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this,ChatActivity.class);
                        intent.putExtra("username",loggedUserName);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}

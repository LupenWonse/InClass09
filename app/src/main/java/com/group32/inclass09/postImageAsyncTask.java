package com.group32.inclass09;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by ahmet on 31/10/2016.
 */

public class postImageAsyncTask extends AsyncTask<String, Void, String> {
    private final OkHttpClient client = new OkHttpClient();
    private String token;
    private postMessageAsyncTask.IPostMessage iPostMessage;

    public postImageAsyncTask(postMessageAsyncTask.IPostMessage iPostMessage) {
        this.iPostMessage = iPostMessage;
    }

    @Override
    protected String doInBackground(String... params) {
        File file = new File(params[0]);
        token = params[1];

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("newFile", "newFile",
                            RequestBody.create(MediaType.parse("file"), file))
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization","BEARER " + token)
                    .addHeader("Content-Type","application/x-www-form-urlencoded")
                    .url("http://ec2-54-166-14-133.compute-1.amazonaws.com/api/file/add")
                    .post(requestBody)
                    .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            JSONObject responseJson = new JSONObject(response.body().string());
            return responseJson.getJSONObject("file").getString("Id");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        new postMessageAsyncTask(iPostMessage).execute("",token,"IMAGE",s);
    }
}

package com.group32.inclass09;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ahmet on 31/10/2016.
 */

public class postMessageAsyncTask extends AsyncTask<String,Void,Void> {

    private final OkHttpClient client = new OkHttpClient();
    private String token;
    private String message;
    private IPostMessage iPostMessage;

    public postMessageAsyncTask(IPostMessage iPostMessage) {
        this.iPostMessage = iPostMessage;
    }

    @Override
    protected Void doInBackground(String... params) {
        message = params[0];
        token = params[1];
        String messageType = params[2];
        String imageId = params[3];

        RequestBody requestBody = new FormBody.Builder()
                .add("Type", messageType)
                .add("Comment", message)
                .add("FileThumbnailId", imageId)
                .build();
        Request request = new Request.Builder()
                .url("http://ec2-54-166-14-133.compute-1.amazonaws.com/api/message/add")
                .addHeader("Authorization", "BEARER " + token)
                .post(requestBody)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        iPostMessage.messagePosted();
    }

    public interface IPostMessage{
        void messagePosted();
    }
}

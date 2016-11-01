package com.group32.inclass09;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class getMessagesAsyncTask extends AsyncTask<String,Void,ArrayList<Message>> {
    private final OkHttpClient client = new OkHttpClient();
    private String token;
    private ArrayList<Message> messageArrayList;
    private IMessage iMessage;

    public getMessagesAsyncTask(IMessage iMessage) {
        this.iMessage = iMessage;
    }

    @Override
    protected ArrayList<Message> doInBackground(String... params) {
        messageArrayList = new ArrayList<>();
        token = params[0];

        Request request = new Request.Builder()
                .url("http://ec2-54-166-14-133.compute-1.amazonaws.com/api/messages")
                .addHeader("Authorization", "BEARER " + token)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.isSuccessful()) {
            ArrayList<Message> messageList = new ArrayList<>();
            try {
                JSONObject rootJsonObject = new JSONObject(response.body().string());
                JSONArray messagesJsonArray = rootJsonObject.getJSONArray("messages");

                for (int i = 0; i < messagesJsonArray.length(); i++) {
                    JSONObject message = messagesJsonArray.getJSONObject(i);
                    String firstName = message.getString("UserFname");
                    String lastName = message.getString("UserLname");
                    int id = message.getInt("Id");
                    String comment = message.getString("Comment");
                    String image = message.getString("FileThumbnailId");
                    String type = message.getString("Type");
                    String created = message.getString("CreatedAt");

                    messageList.add(new Message(firstName, lastName, id, comment, image, created, type));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            messageArrayList.clear();
            messageArrayList.addAll(messageList);

        }
        return messageArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Message> messages) {
        iMessage.messageUpdate(messages);
    }

    public interface IMessage{
        void messageUpdate(ArrayList<Message> messages);
    }
}

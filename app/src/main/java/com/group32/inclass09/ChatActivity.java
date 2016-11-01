package com.group32.inclass09;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity implements getMessagesAsyncTask.IMessage, postMessageAsyncTask.IPostMessage{

    private final OkHttpClient client = new OkHttpClient();
    private String token = "test";

    private ListView listView;
    private MessageListAdapter listAdapter;
    private ArrayList<Message> messageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = sharedPreferences.getString("token",null);

        messageArrayList = new ArrayList<Message>();

        listView = (ListView) findViewById(R.id.listChat);
        listAdapter = new MessageListAdapter(this,messageArrayList,R.layout.list_view_row);
        listView.setAdapter(listAdapter);

        new getMessagesAsyncTask(this).execute(token);

        TextView loggedUser = (TextView) findViewById(R.id.textLoggedUser);
        ImageButton buttonPostImage = (ImageButton) findViewById(R.id.buttonGallery);
        ImageButton buttonPostText = (ImageButton) findViewById(R.id.buttonSend);
        final EditText editMessage = (EditText) findViewById(R.id.editMessage);

        buttonPostText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = editMessage.getText().toString();
                new postMessageAsyncTask(ChatActivity.this).execute(comment,token,"TEXT","");
            }
        });

        buttonPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });


        if (getIntent().getExtras() != null) {
            String loggedUserName = getIntent().getExtras().getString("username");
            loggedUser.setText(loggedUserName);

        }

        ((ImageButton) findViewById(R.id.buttonLogout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.commit();
                finish();
            }
        });

    }

    public void PostMessage(String comment) throws Exception {
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

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");


    @Override
    public void messagePosted() {
        new getMessagesAsyncTask(this).execute(token);
    }

    @Override
    public void messageUpdate(ArrayList<Message> messages) {
        messageArrayList.clear();
        messageArrayList.addAll(messages);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        String uriRealPath = getRealPathFromURI(this,uri);
        new postImageAsyncTask(this).execute(uriRealPath,token);
    }
}

package com.group32.inclass09;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MessageListAdapter extends ArrayAdapter<Message> implements Handler.Callback{




    private Context mContext;
    private List<Message> messageList;
    private int rowResId;
    private SharedPreferences sharedPreferences;
    private String token;
    private OkHttpClient client;
    private Handler handler;

    static class ViewHolder implements Serializable{
         ImageView imageView;
         TextView textMessage;
         TextView textWriter;
         TextView textTime;
    }

    public MessageListAdapter(Context mContext, List<Message> messageList, int rowResId) {
        super(mContext, rowResId, messageList);
        this.mContext = mContext;
        this.messageList = messageList;
        this.rowResId = rowResId;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.token = sharedPreferences.getString("token","");
        this.handler = new android.os.Handler(this);
        client = new OkHttpClient();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.rowResId,parent,false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageMessage);
            viewHolder.textMessage = (TextView) convertView.findViewById(R.id.textLoggedUser);
            viewHolder.textWriter= (TextView) convertView.findViewById(R.id.textMessageWriter);
            viewHolder.textTime = (TextView) convertView.findViewById(R.id.textMessageTime);
            convertView.setTag(viewHolder);
        }

        final ViewHolder currentViewHolder = (ViewHolder) convertView.getTag();
        Message currentMessage = messageList.get(position);

        if (currentMessage.getmessageType().equals("IMAGE")) {
            String url = "http://ec2-54-166-14-133.compute-1.amazonaws.com/api/file/"+currentMessage.getImage();
            Request request = new Request.Builder()
                    .header("Authorization", "BEARER " + token)
                    .url(url)
                    .build();


            Response response = null;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final Bitmap image = BitmapFactory.decodeStream(response.body().byteStream());
                    android.os.Message message = new android.os.Message();
                    message.what = position;
                    Bundle data = new Bundle();
                    data.putParcelable("image",image);
                    data.putSerializable("viewHolder",currentViewHolder);
                    message.setData(data);
                    handler.sendMessage(message);
                }
            });

            currentViewHolder.imageView.setVisibility(View.VISIBLE);
            currentViewHolder.textMessage.setVisibility(View.GONE);
        } else {
            currentViewHolder.textMessage.setText(currentMessage.getMessage());
            currentViewHolder.imageView.setVisibility(View.GONE);
            currentViewHolder.textMessage.setVisibility(View.VISIBLE);
        }
        currentViewHolder.textWriter.setText(currentMessage.getFullName());
        currentViewHolder.textTime.setText(currentMessage.getPrettyTime());

        return convertView;

    }

    @Override
    public boolean handleMessage(android.os.Message msg) {
        Bitmap image = (Bitmap) msg.getData().getParcelable("image");
        ViewHolder viewHolder = (ViewHolder) msg.getData().getSerializable("viewHolder");

        viewHolder.imageView.setImageBitmap(image);
        return true;
    }

}

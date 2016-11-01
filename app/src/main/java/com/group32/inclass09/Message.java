package com.group32.inclass09;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ahmet on 31/10/2016.
 */

public class Message {

    private String firstName;
    private String lastName;
    private int id;
    private String message;
    private String image;
    private String created;
    private String messageType;

    public Message(String firstName, String lastName, int id, String message, String image, String created, String type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.message = message;
        this.image = image;
        this.created = created;
        this.messageType = type;
    }

    public String getmessageType() {
        return messageType;
    }

    public void setmessageType(String type) {
        this.messageType = type;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPrettyTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(0);
        try {
            date = dateFormat.parse(created);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PrettyTime p = new PrettyTime();
        return p.format(date);
    }

}

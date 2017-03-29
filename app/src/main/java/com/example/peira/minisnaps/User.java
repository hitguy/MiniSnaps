package com.example.peira.minisnaps;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by peira on 28-Feb-17.
 */
@IgnoreExtraProperties
public class User {

    public String name;

    public User()
    {

    }

    public User(String name)
    {
        this.name = name;
    }
}

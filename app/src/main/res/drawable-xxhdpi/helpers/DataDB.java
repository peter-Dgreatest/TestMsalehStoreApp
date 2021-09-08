package com.itcrusaders.msaleh.helpers;

import android.content.Context;

import java.io.IOException;

public class DataDB {
    private static final String TAG = "DataDB";
    App appState;
    App app;

    DatabaseHandler myDBconnection;

    public DatabaseHandler myConnection(Context context) {
        myDBconnection = new DatabaseHandler(context, "msalah.db");
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (myDBconnection.checkDataBase()) {
            myDBconnection.openDataBase();
        }
        return myDBconnection;
    }
}


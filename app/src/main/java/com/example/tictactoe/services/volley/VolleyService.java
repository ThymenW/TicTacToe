package com.example.tictactoe.services.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.tictactoe.services.volley.requests.IRequest;

public class VolleyService {
    private static VolleyService instance;
    private Context context;
    private RequestQueue requestQueue;

    private VolleyService(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(this.context);
    }

    public static VolleyService getInstance(Context context) {
        if (instance == null)
            instance = new VolleyService(context);
        return instance;
    }

    public void doRequest(IRequest request) {
        JsonRequest jsonRequest = request.doRequest();
        if (jsonRequest == null) {
            return;
        }
        requestQueue.add(jsonRequest);
    }
}

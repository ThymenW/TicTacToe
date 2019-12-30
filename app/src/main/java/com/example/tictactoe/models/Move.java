package com.example.tictactoe.models;

import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class Move {
    private int x, y;
    private int player;

    public Move(int x, int y, int player) {
        this.x = x;
        this.y = y;
        this.player = player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPlayer() {
        return player;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("x", this.x);
        object.put("y", this.y);
        object.put("player", this.player);
        return object;
    }

    @Nullable
    public String toJSONString() {
        try {
            return this.toJSON().toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Move fromJSON(String data) throws JSONException {
        JSONObject object = new JSONObject(data);
        return new Move(object.getInt("x"), object.getInt("y"), object.getInt("player"));
    }
}

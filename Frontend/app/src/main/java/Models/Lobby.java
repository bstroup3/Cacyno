package Models;


import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import interfaces.ILobby;
import interfaces.IUser;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * this class is the lobby instance for when the user joins a game
 */
@Setter
@Getter
public class Lobby {
    @SerializedName("id")
    String id = "";
    @SerializedName("lobbyname")
    String lobbyname = "";
    @SerializedName("active")
    boolean active;

    LobbyOperations ops;

    /**
     * constructs a new lobby with new operations for this lobby
     */
    public Lobby(){
        ops = new LobbyOperations();
    }

    /**
     * this method gives whether the lobby is currently active or not
     * @return true or false depending on if the lobby is active
     */
    public boolean getActive(){
        return active;
    }

    /**
     * this method sets the lobby to be active or not active
     * @param active true if game is created, false if game ended
     */
    public void setActive(boolean active){
        this.active = active;
    }

    /**
     * this method takes the JSONArray and uses Gson to append all lobbies instances from JSONArray and adds to a
     * list of lobby objects
     * @param response the response from the endpoint called
     * @return a list of current lobby objects
     */
    private ArrayList<Lobby> JSONtolist(JSONArray response){
        Type lobbylist = new TypeToken<ArrayList<Lobby>>(){}.getType();
        Exclude ex = new Exclude();
        Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(ex).addSerializationExclusionStrategy(ex).create();
        return gson.fromJson(response.toString(), lobbylist);
    }

    /**
     * this method is called when the user opens "lobby selector" screen to view lobbies to join. Makes a volley
     * request to endpoint to grab JSONArray of lobby instances
     * @param con the current Android context of this view
     * @param list the list that will contain all lobby objects.
     * @param callback this is the interface that is used to handle if the lobbies are adding to list successfully or
     *                 an error occured
     */
    public void calltoServer(Context con, ArrayList<Lobby> list, ILobby callback){
        String url = "http://coms-309-046.cs.iastate.edu:8080/lobby";
        String urllocal = "http://localhost:8080/lobby";

        RequestQueue requestQueue = Volley.newRequestQueue(con);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            /**
             * this method is called when the call to endpoint is successful
             * @param response the response from endpoint
             */
            @Override
            public void onResponse(JSONArray response) {
                list.addAll(JSONtolist(response));
                callback.onSuccess();
                System.out.println(list.size());
            }
        }, new Response.ErrorListener() {
            /**
             * this method is called when the call to the endpoint is not successful and error has occurred
             * @param error the Volley error that has occurred
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR GETTING LOBBIES", error.toString());
            }
        });
        requestQueue.add(request);
    }
    static class Exclude implements ExclusionStrategy {

        @Override
        public boolean shouldSkipClass(Class<?> arg0) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            SerializedName ns = field.getAnnotation(SerializedName.class);
            if(ns != null)
                return false;
            return true;
        }
    }

    /**
     * this method is called when the JSONObject needs to be transferred to a lobby object.
     * @param response the JSONObject from the endpoint called
     */
    private void JSONtoLobby(JSONObject response){
        ops.JSONtoLobby(response, this);
    }

    /**
     * this method is called when needed to get a lobby from all lobby instances with id
     * @param con the current Android context of this view
     * @param id the unqiue id of lobby
     */
    public void calltoServer(Context con, String id){
        String url = "http://coms-309-046.cs.iastate.edu:8080/lobby/"+id;
        String urllocal = "http://localhost:8080/lobby/"+id;

        RequestQueue requestQueue = Volley.newRequestQueue(con);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            /**
             * this method is called when the call to endpoint is successful
             * @param response the response from endpoint
             */
            @Override
            public void onResponse(JSONObject response) {
                JSONtoLobby(response);
            }
        }, new Response.ErrorListener() {
            /**
             * this method is called when the call to the endpoint is not successful and error has occurred
             * @param error the Volley error that has occurred
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR GETTING LOBBY", error.toString());
            }
        });
        requestQueue.add(request);
    }
    /**
     * this method is called when the lobby object needs to translate into a JSONObject to append to endpoint
     * @return a JSONObject that contains this lobby object instance
     */
    private JSONObject LobbyToJSON(){
        return ops.lobbyToJSON(this);
    }

    /**
     * this method is called when needing to update this lobby instance in the database
     * @param con the current Android context of this view
     */
    public void updateLobby(Context con){
        String url = "http://coms-309-046.cs.iastate.edu:8080/lobby";
        String urllocal = "http://localhost:8080/lobby";
        RequestQueue requestQueue = Volley.newRequestQueue(con);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, LobbyToJSON(), new Response.Listener<JSONObject>() {
            /**
             * this method is called when the call/append to endpoint is successful
             * @param response the response from endpoint
             */
            @Override
            public void onResponse(JSONObject response) {}
        }, new Response.ErrorListener() {
            /**
             * this method is called when the call to the endpoint is not successful and error has occurred
             * @param error the Volley error that has occurred
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR sending lobbyTOdb", error.toString());
            }
        });
        requestQueue.add(request);
    }

    /**
     * this method is called when a new game is created and the lobby needs to be added to the database
     * @param con the current Android context of this view
     */
    public void newLobby(Context con, ILobby callback){
        String url = "http://coms-309-046.cs.iastate.edu:8080/lobby";
        String urllocal = "http://localhost:8080/lobby";
        RequestQueue requestQueue = Volley.newRequestQueue(con);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, LobbyToJSON(), new Response.Listener<JSONObject>() {
            /**
             * this method is called when the call/append to database is successful
             * @param response the response from endpoint
             */
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess();
            }
        }, new Response.ErrorListener() {
            /**
             * this method is called when the call to the endpoint is not successful and error has occurred
             * @param error the Volley error that has occurred
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR sending lobbyTOdb", error.toString());
            }
        });
        requestQueue.add(request);
    }





}

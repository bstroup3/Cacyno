package Models;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * this class is used for a lobby to transfer data to and from db
 */
public class LobbyOperations {
    /**
     * Constructor for new lobby operations for a lobby
     */
    public LobbyOperations(){}

    /**
     * this method handles a JSONObject and attempts to append all data from JSONObject to a lobby object
     * @param response the lobby JSONObject from endpoint
     * @param lobby the lobby object that will contain info from JSONObject
     */
    public void JSONtoLobby(JSONObject response, Lobby lobby){
        try {
            lobby.setId(response.getString("id"));
            lobby.setActive(Boolean.parseBoolean(response.getString("active")));
            lobby.setLobbyname(response.getString("lobbyname"));
        }catch (JSONException e){
            Log.e("ERROR: JSON->Lobby", e.toString());
        }

    }

    /**
     * this method handles when a lobby object needs to be translated into a JSONObject
     * @param lobby the lobby object that will be translated
     * @return a JSONObject that contains info of lobby object
     */
    public JSONObject lobbyToJSON(Lobby lobby){
        JSONObject postData = new JSONObject();
        try {
            postData.put("id", lobby.getId());
            postData.put("active", lobby.getActive());
            postData.put("lobbyname", lobby.getLobbyname());
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return postData;
    }

}

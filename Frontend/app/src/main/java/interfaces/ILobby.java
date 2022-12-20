package interfaces;

/**
 * The interface used for making calls to the Lobby table
 */
public interface ILobby {
    /**
     * when the lobbies are being appending to a list, this method will handle when the lobbies are
     * grabbed successfully
     *
     * @return an int that indicates successful
     */
    int onSuccess();

    /**
     * when the volley request to get all lobbies fails, this method will handle the error that has occurred
     *
     * @return an int that indicates an error
     */
    int onError();
}

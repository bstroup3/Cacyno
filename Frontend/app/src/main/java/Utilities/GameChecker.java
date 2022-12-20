package Utilities;

import Models.User;
import android.widget.TextView;

/**
 * This class is for operations to check a game parameters
 */
public class GameChecker {
    /**
     * this method is used to check all cases of input for a lobby name when creating a new game.
     * @param name the text that was inputted
     * @param nameView the text view that is used for lobby name
     * @return true if lobby name passes cases, false if lobby name is in a wrong format
     */
    public boolean checkLobbyName(String name, TextView nameView){
        if(name.isEmpty()){
            nameView.setError("please provide lobby name");
            nameView.requestFocus();
            return false;
        }
        else if(name.length() > 12){
            nameView.setError("please stay within max length");
            nameView.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * this method is used to check all cases of input for a amount of money wanting to bet when creating a new game.
     * @param money the amount of money that was inputted in string format
     * @param moneyView the text view that is used for inputting the amount of money
     * @param user the current user that is tring to create a game
     * @return true if amount of money passes all cases, false if amount money is in wrong format or user
     * does not have enough funds to bet inputted amount
     */
    public boolean checkMoneyAmount(String money, TextView moneyView, User user){
        if(money.isEmpty())
        {
            moneyView.setError("please supply amount");
            moneyView.requestFocus();
            return false;
        }
        else if(money.matches(".*[a-z].*")){
            moneyView.setError("no letters please");
            moneyView.requestFocus();
            return false;
        }
        else if (money.charAt(0) == '0'){
            moneyView.setError("please supply real amount");
            moneyView.requestFocus();
            return false;
        }
        else if(Integer.parseInt(money) > user.getMoney()){
            moneyView.setError(user.getUsername() + " too broke");
            moneyView.requestFocus();
            return false;
        }
        return true;
    }
}

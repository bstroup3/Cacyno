package Utilities;

import android.content.Context;
import android.widget.EditText;

/**
 * Holds the method that checks if the username and password are valid for account creation
 */
public class AccountCreation {
    AccountChecker accountChecker = new AccountChecker();

    /**
     * @param username the username to check
     * @param password the password to check
     * @param email    the field the username was typed in
     * @param passwd   the field the password was typed in
     * @param context  the context of the app
     * @return whether the username and password are valid
     */
    public boolean createAccount(String username, String password, EditText email, EditText passwd, Context context) {
        if (accountChecker.checkEmail(username, email) && accountChecker.checkPassword(password, passwd)) {
            return true;
        } else {
            return false;
        }
    }
}

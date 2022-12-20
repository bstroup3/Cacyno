package Utilities;

import android.widget.EditText;

/**
 * Holds the methods used to check if the user tries to put in an empty email or password
 */
public class AccountChecker {
    /**
     * checks to see if the email field is empty
     *
     * @param email   the proposed email
     * @param emailId the field the email was typed in
     * @return whether the email is valid
     */
    public boolean checkEmail(String email, EditText emailId) {
        if (email.isEmpty()) {
            emailId.setError("Provide your Email first!");
            emailId.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * checks to see if the password field is empty
     *
     * @param password the proposed password
     * @param passwd   the field the password was typed in
     * @return whether the password is valid
     */
    public boolean checkPassword(String password, EditText passwd) {
        if (password.isEmpty()) {
            passwd.setError("Set your password");
            passwd.requestFocus();
            return false;
        }
        return true;
    }
}

package ch.ethz.inf.vs.a4.funwithflags;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    // UI references.
    private EditText mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUserNameView = (EditText) findViewById(R.id.user_name_text_view);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    LogInOrRegister();
                    return true;
                }
                return false;
            }
        });

        //Designate functionality to button
        Button mUserNameSignInButton = (Button) findViewById(R.id.user_name_sign_in_button);
        mUserNameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInOrRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to log in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void LogInOrRegister() { // Signing up: not yet clear if it is a registration or a login
        final String username =  mUserNameView.getText().toString().trim();
        final String password = mPasswordView.getText().toString().trim();

        //Format checks
        boolean validationError = true;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if(username.isEmpty()){
            validationErrorMessage.append(getString(R.string.error_email_required));
        }
        else if(password.isEmpty()){
            validationErrorMessage.append(getString(R.string.error_password_required));
        }
        else if(password.length() < 4){
            validationErrorMessage.append(getString(R.string.error_short_password));
        }
        else{
            validationError = false;
        }
        if (validationError) {
            Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
            return;
        }


        showProgress(true);

        //Check if username already exists. If yes -> login, else -> register
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.setLimit(1); //if found, return this username
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {

                    showProgress(false);

                    if (objects.size() == 0) {

                        register(username, password);

                    } else {
                        //username aready exists!
                        logIn(username, password);
                    }
                } else {
                    // could not check if username already exists
                    loginNotSuccessful(e.getLocalizedMessage());
                }
            }
        });

    }


    private void register(final String username, final String password){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.login_first_time));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setMessage(R.string.provide_email);
        builder.setView(input);
        //Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString();
                //format check
                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email cannot be empty", Toast.LENGTH_LONG).show();
                } else if (!isEmailValid(email)) {
                    Toast.makeText(LoginActivity.this, "Please provide a valid email!", Toast.LENGTH_LONG).show();
                } else {

                    signUp(username, password, email);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void signUp(String username, String password, String email) {

        ParseUser user = new ParseUser();
        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(username);
        // Call the Parse LogInOrRegister method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                showProgress(false);

                if (e == null) {
                    // The query was successful.
                    loginSuccessful();
                } else {
                    loginNotSuccessful(e.getLocalizedMessage());
                }
            }
        });

    }


    private void logIn(String username, String password){
        showProgress(true);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {

                showProgress(false);

                if (e == null) {
                    // Hooray! Let them use the app now.
                    loginSuccessful();
                } else {
                    loginNotSuccessful(e.getLocalizedMessage());
                }

            }
        });

    }

    private void loginNotSuccessful(String error) {

        //TODO: Provide useful messages to the user, maybe change String error to int ErrorCode

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Resources res = getResources();
        builder.setTitle(res.getString(R.string.login_not_successful));

        builder.setMessage(R.string.try_again_later + "\nError message:\n" + error);
        //Set up the buttons
        builder.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
    private void loginSuccessful(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        Toast.makeText(LoginActivity.this, getString(R.string.welcome) + " " + currentUser.getUsername() + "!", Toast.LENGTH_LONG).show();
        this.finish(); //not sure if this works. Maybe we should rather start a other activity
    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}


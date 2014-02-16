package com.iutdijon.androiut2.iut.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.global.AndroIUTApplication;
import com.iutdijon.androiut2.iut.data.Credential;
import com.iutdijon.androiut2.iut.data.account.UserAccount;
import com.iutdijon.androiut2.iut.services.IUTLoginServiceCall;
import com.iutdijon.androiut2.util.AndroIUTLogger;
import com.iutdijon.androiut2.util.IOUtils;
import com.iutdijon.androiut2.util.PreferencesManager;
import com.iutdijon.androiut2.util.UIUtil;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	public static final String EXTRA_EMAIL = "Login";

	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private IUTLoginServiceCall mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mLogin;
	private String mPassword;
	// UI references.
	private EditText mLoginView;
	private EditText mPasswordView;
	private TextView mLoginStatusMessageView;
	private CheckBox mRemberPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_login);
		PreferencesManager.getInstance().init(getApplicationContext());
		
		//AndroIUTLogger logger = new AndroIUTLogger(getApplicationContext());
		new AndroIUTLogger(getApplicationContext());
				
		if(!IOUtils.isOnline(this)){
			UIUtil.showMessage(this, R.string.must_have_network_connection);
		}
		
		mRemberPassword = (CheckBox) findViewById(R.id.remember_password_opt);
		mRemberPassword.setChecked(PreferencesManager.getInstance().getBoolean(PreferencesManager.REMEMBER_ME_CHECKBOX_VAL));
		
		// Set up the login form.
		mLoginView = (EditText) findViewById(R.id.login);
				
		mLogin = PreferencesManager.getInstance().getString(PreferencesManager.REMEMBER_ME_OPTION);
		mLoginView.setText(mLogin);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mPassword = PreferencesManager.getInstance().getString(PreferencesManager.REMEMBER_ME_PASSWORD);
		mPasswordView.setText(mPassword);
		
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					attemptLogin();
				}
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		if(!IOUtils.isOnline(this)){
			UIUtil.showMessage(this, R.string.must_have_network_connection);
			return;
		}
		
		// Reset errors.
		mLoginView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mLogin = mLoginView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mLogin)) {
			mLoginView.setError(getString(R.string.error_field_required));
			focusView = mLoginView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			
			new IUTLoginServiceCall(this){
				@Override
				protected void onPostRun(UserAccount token) {
					if(token != null){
						AndroIUTApplication.getInstance().setUser(token);
						if(mRemberPassword.isChecked()){
							PreferencesManager.getInstance().setString(PreferencesManager.REMEMBER_ME_PASSWORD, (token.getPassword()));
						}else{
							PreferencesManager.getInstance().setString(PreferencesManager.REMEMBER_ME_PASSWORD, "");
						}
						PreferencesManager.getInstance().setBoolean(PreferencesManager.REMEMBER_ME_CHECKBOX_VAL, mRemberPassword.isChecked());
						Intent home_intent = new Intent(LoginActivity.this, HomeActivity.class);
						startActivity(home_intent);
					}else{
						UIUtil.showMessage(LoginActivity.this, getString(R.string.invalid_credential));
					}
				}
			}.execute(new Credential(mLogin, mPassword));
		}
	}
}

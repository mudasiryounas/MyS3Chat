package com.mys3soft.mys3chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.IFireBaseAPI;
import com.mys3soft.mys3chat.Services.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ActivityLogin extends AppCompatActivity {

    DataContext db = new DataContext(this, null, null, 1);


    EditText et_Email, et_Password;
    Button btn_Login;
    public static final String ENDPOINT = "https://mys3chat.firebaseio.com";
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //region UI Controls
        et_Email = (EditText) findViewById(R.id.et_Email);
        et_Password = (EditText) findViewById(R.id.et_Password);
        //endregion
    }


    //region Login
    public void btnLoginClick(View view) {
        if (et_Email.getText().toString().equals("")) {
            et_Email.setError("Email cannot be empty");

        } else if (et_Password.getText().toString().equals("")) {
            et_Password.setError("Password cannot be empty");
// com.squareup.retrofit2:converter-gson
        } else {
            pd = new ProgressDialog(this);
            pd.setMessage("Loading...");
            pd.show();
            LoginTask t = new LoginTask();
            t.execute();
        }

    }
    //endregion

    public class LoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

            IFireBaseAPI api = retrofit.create(IFireBaseAPI.class);
            Call<String> call = api.getAllUsersAsJsonString();
            try {
                return call.execute().body();
            } catch (IOException e) {
                pd.hide();
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonList) {

            String email = Tools.encodeString(et_Email.getText().toString());
            String pass = et_Password.getText().toString();

            try {
                JSONObject obj = new JSONObject(jsonList);

                if (obj.has(email)) {
                    JSONObject userObj = obj.getJSONObject(email);
                    if (userObj.getString("Password").equals(pass)) {
                        pd.hide();
                        boolean resp = db.saveUserInLocalDB(email, userObj.getString("FirstName"), userObj.getString("LastName"));
                        if (resp) {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser",0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("Email",email);
                            editor.commit();
                            finish();
                        } else {
                            Toast.makeText(ActivityLogin.this, "Something went wrong please try again", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        pd.hide();
                        Toast.makeText(ActivityLogin.this, "Incorecct email or password", Toast.LENGTH_LONG).show();

                    }
                } else {
                    pd.hide();
                    Toast.makeText(ActivityLogin.this, "Incorecct email or password", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                pd.hide();
                e.printStackTrace();
            }

        }

    }


    //region SignUp
    public void btnSignUpClick(View view) {

        startActivity(new Intent(this, ActivityRegister.class));
    }
    //endregion


    @Override
    public void onBackPressed() {

    }
}

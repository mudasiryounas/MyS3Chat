package com.mys3soft.mys3chat;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mys3soft.mys3chat.Models.StaticInfo;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.IFireBaseAPI;
import com.mys3soft.mys3chat.Services.LocalUserService;
import com.mys3soft.mys3chat.Services.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;

public class ActivityRegister extends AppCompatActivity {


    EditText et_Email, et_Password, et_FirstName, et_LastName;
    Button btn_Register;
    ProgressDialog pd;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Firebase.setAndroidContext(this);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");

        et_Email = (EditText) findViewById(R.id.et_Email_Rigister);
        et_Password = (EditText) findViewById(R.id.et_Password_Rigister);
        et_FirstName = (EditText) findViewById(R.id.et_FirstName_Rigister);
        et_LastName = (EditText) findViewById(R.id.et_LastName_Rigister);

    }

    public void btn_RegClick(View view) {
        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        } else if (et_FirstName.getText().toString().equals("")) {
            et_FirstName.setError("Enter Firstname");
        } else if (et_LastName.getText().toString().equals("")) {
            et_LastName.setError("Enter Lastname");
        } else if (et_Email.getText().toString().equals("") || !Tools.isValidEmail(et_Email.getText().toString())) {
            et_Email.setError("Enter Valid Email");
        } else if (et_Password.getText().toString().equals("")) {
            et_Password.setError("Enter Password");
        } else {
            email = Tools.encodeString(et_Email.getText().toString());
            RegisterUserTask t = new RegisterUserTask();
            t.execute();
        }
    }

    public class RegisterUserTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            IFireBaseAPI api = Tools.makeRetroFitApi();
            Call<String> call = api.getSingleUserByEmail(StaticInfo.UsersURL + "/" + email + ".json");
            try {
                return call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                pd.hide();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            try {
                if (jsonString.trim().equals("null")) {
                    Firebase firebase = new Firebase(StaticInfo.UsersURL);
                    firebase.child(email).child("FirstName").setValue(et_FirstName.getText().toString());
                    firebase.child(email).child("LastName").setValue(et_LastName.getText().toString());
                    firebase.child(email).child("Email").setValue(email);
                    firebase.child(email).child("Password").setValue(et_Password.getText().toString());
                    DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
                    Date date = new Date();
                    firebase.child(email).child("Status").setValue(dateFormat.format(date));
                    Toast.makeText(getApplicationContext(), "Signup Success", Toast.LENGTH_SHORT).show();
                    pd.hide();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                    pd.hide();
                }
            } catch (Exception e) {
                pd.hide();
                e.printStackTrace();
            }
        }
    }


}

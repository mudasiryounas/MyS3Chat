package com.mys3soft.mys3chat;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.mys3soft.mys3chat.Services.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityRegister extends AppCompatActivity {


    EditText et_Email, et_Password, et_FirstName, et_LastName;
    Button btn_Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Firebase.setAndroidContext(this);

        et_Email = (EditText) findViewById(R.id.et_Email_Rigister);
        et_Password = (EditText) findViewById(R.id.et_Password_Rigister);
        et_FirstName = (EditText) findViewById(R.id.et_FirstName_Rigister);
        et_LastName = (EditText) findViewById(R.id.et_LastName_Rigister);

    }

    public void btn_RegClick(View view) {

        if (et_FirstName.getText().toString().equals("")) {
            et_FirstName.setError("Enter Firstname");
        } else if (et_LastName.getText().toString().equals("")) {
            et_LastName.setError("Enter Lastname");
        } else if (et_Email.getText().toString().equals("") || !Tools.isValidEmail(et_Email.getText().toString()) ) {
            et_Email.setError("Enter Valid Email");
        }
        else if (et_Password.getText().toString().equals("")) {
            et_Password.setError("Enter Password");
        } else {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Loading...");
            pd.show();
            String email = Tools.encodeString(et_Email.getText().toString());
            Firebase firebase = new Firebase("https://mys3chat.firebaseio.com/users");
            // check if user already exists
            firebase.child(email).child("FirstName").setValue(et_FirstName.getText().toString());
            firebase.child(email).child("LastName").setValue(et_LastName.getText().toString());
            firebase.child(email).child("Email").setValue(email);
            firebase.child(email).child("Password").setValue(et_Password.getText().toString());
            DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
            Date date = new Date();
            firebase.child(email).child("Status").setValue( dateFormat.format(date));
            Toast.makeText(this, "Signup Success", Toast.LENGTH_SHORT).show();
            pd.hide();
            finish();

        }

    }
}

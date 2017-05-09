package info.loginp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.app.Activity;

    import android.app.ProgressDialog;
    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
    import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;
    import butterknife.InjectView;

    public class Signup extends AppCompatActivity implements OnItemSelectedListener {
        private static final String TAG = "SignupActivity";
        private FirebaseAuth mauth;
        private DatabaseReference mdatabase;
        private ProgressDialog mpprogress;
        String item,phone;
        FirebaseDatabase db= FirebaseDatabase.getInstance();

        @InjectView(R.id.input_name) EditText _nameText;
        @InjectView(R.id.input_email) EditText _emailText;
        @InjectView(R.id.input_password) EditText _passwordText;
        @InjectView(R.id.btn_signup) Button _signupButton;
        @InjectView(R.id.link_login) TextView _loginLink;



        @Override
        public void onCreate(Bundle savedInstanceState) {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);
            Bundle b= getIntent().getExtras();
            phone=b.getString("phone");
            mauth= FirebaseAuth.getInstance();
            mpprogress = new ProgressDialog(this,R.style.ProgressBar);
            mdatabase= db.getReference().child("Users");
            ButterKnife.inject(this);
            Spinner spinner = (Spinner) findViewById(R.id.city_spinner);
            spinner.setOnItemSelectedListener(this);

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.india_cities, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            spinner.setAdapter(adapter);


            _signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signup();
                }
            });

            _loginLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Finish the registration screen and return to the Login activity
                    finish();
                }
            });

        }
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // On selecting a spinner item
             item = parent.getItemAtPosition(position).toString();

            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }



        public void signup() {
            Log.d(TAG, "Signup");

            if (!validate()) {
                onSignupFailed();
                return;
            }

            //_signupButton.setEnabled(false);



            final String name = _nameText.getText().toString();
            String email = _emailText.getText().toString();
            String password = _passwordText.getText().toString();

            // TODO: Implement your own signup logic here.
            if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password))
            {
                mpprogress.setMessage("Signing up..");
                mpprogress.show();
                mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mauth.getCurrentUser().getUid();
                            String mail= mauth.getCurrentUser().getEmail();
                            //mdatabase.child(uid);
                           // DatabaseReference db2 = mdatabase.child(item);
                            DatabaseReference db1 = mdatabase.child(uid);
                            db1.child("name").setValue(name);
                            db1.child("city").setValue(item);
                            db1.child("image").setValue("default");
                            db1.child("address").setValue("Press edit to enter your address");
                            db1.child("phone").setValue(phone);
                            db1.child("email").setValue(mail);
                            //Firebase fuid=mroot.child(uid);
                            //fuid.child("name").setValue(sname);
                            //fuid.child("image").setValue("Default");
                            mpprogress.dismiss();
                            Toast.makeText(getBaseContext(), "SignUp successful", Toast.LENGTH_LONG).show();
                            Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(i);

                        }
                    }
                });
            }


        }


        public void onSignupSuccess() {
            _signupButton.setEnabled(true);
            setResult(RESULT_OK, null);
            finish();
        }

        public void onSignupFailed() {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

            _signupButton.setEnabled(true);
        }

        public boolean validate() {
            boolean valid = true;

            String name = _nameText.getText().toString();
            String email = _emailText.getText().toString();
            String password = _passwordText.getText().toString();

            if (name.isEmpty() || name.length() < 3) {
                _nameText.setError("at least 3 characters");
                valid = false;
            } else {
                _nameText.setError(null);
            }

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _emailText.setError("enter a valid email address");
                valid = false;
            } else {
                _emailText.setError(null);
            }

            if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
                _passwordText.setError("between 6 and 10 alphanumeric characters");
                valid = false;
            } else {
                _passwordText.setError(null);
            }

            return valid;
        }
    }

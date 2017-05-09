package info.loginp;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Editprofile extends AppCompatActivity {

    FirebaseAuth mauth;
    FirebaseDatabase mdatabase;
    DatabaseReference mref;
    EditText tname, tphone,taddress,tmail;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        tname=(EditText)findViewById(R.id.ename);
        tphone=(EditText)findViewById(R.id.ephone);
        tmail=(EditText)findViewById(R.id.email);
        taddress=(EditText)findViewById(R.id.eaddress);
        save=(Button)findViewById(R.id.btnsave);
        mauth=FirebaseAuth.getInstance();
        mdatabase=FirebaseDatabase.getInstance();
        String uid= mauth.getCurrentUser().getUid();
        final String mail =mauth.getCurrentUser().getEmail();
        mref=mdatabase.getReference().child("Users").child(uid);
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name=dataSnapshot.child("name").getValue(String.class);
                Log.v("name",name);
                String phone=dataSnapshot.child("phone").getValue(String.class);
                Log.v("name",phone);
                String image=dataSnapshot.child("image").getValue(String.class);
                Log.v("name",image);
                String address= dataSnapshot.child("address").getValue(String.class);
                Log.v("name",address);
                tname.setText(name);
                tphone.setText(phone);
                taddress.setText(address);
                tmail.setText(mail);
                tmail.setEnabled(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mref.child("name").setValue(tname.getText().toString());
                mref.child("phone").setValue(tphone.getText().toString());
                mref.child("address").setValue(taddress.getText().toString());
                Intent intent = new Intent(getApplicationContext(),Login_success.class);
                intent.putExtra("tag", "edit");
                startActivity(intent);

            }
        });

    }
}

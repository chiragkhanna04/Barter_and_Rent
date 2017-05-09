package info.loginp;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.mail.Session;

import static android.R.attr.finishOnTaskLaunch;
import static android.R.attr.phoneNumber;
import static android.R.id.message;
import static info.loginp.R.id.email;

public class Selectedpost extends AppCompatActivity {
    FirebaseDatabase mdatabase;
    DatabaseReference mref;
    ImageButton right;
    FirebaseAuth mauth;
    String loggedinusermail;
    private Session session;
    ProgressDialog mp;
    String chatWith,user1,user2;
    String uid,mail,uname,uphone,ucity,umail,subject="Barter & Rent Notification..!",mailmessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectedpost);
        mauth=FirebaseAuth.getInstance();
       mp= new ProgressDialog(this);
        mp.setMessage("Loading..!");
        mp.setCanceledOnTouchOutside(false);
        mp.show();

        OneSignal.startInit(this).init();
        loggedinusermail=mauth.getCurrentUser().getEmail().toString();
        OneSignal.sendTag("userid",loggedinusermail);

        Bundle b = getIntent().getExtras();
        String rval = b.getString("rval");
        String key = b.getString("key");
        String city= b.getString("city");
        final TextView ttitle,tdesc;
        ttitle=(TextView)findViewById(R.id.texts1);
        tdesc=(TextView)findViewById(R.id.texts2);
        right=(ImageButton)findViewById(R.id.imageButton5);
        final ImageButton imgbtn=  (ImageButton)findViewById(R.id.images1);
        Log.v("key",key);
        mdatabase= FirebaseDatabase.getInstance();
        mref=mdatabase.getReference().child("Post").child(rval).child(city).child(key);
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title= dataSnapshot.child("title").getValue(String.class);
                String desc=dataSnapshot.child("desc").getValue(String.class);
                String img= dataSnapshot.child("image").getValue(String.class);
                uid=dataSnapshot.child("uid").getValue(String.class);
                chatWith=dataSnapshot.child("uid").getValue(String.class);
                getmail(uid);
                ttitle.setText(title);
                tdesc.setText(desc);
                Picasso.with(getApplicationContext()).load(img).into(imgbtn);
                mp.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                sendNotification();
                sendEmail(umail,subject,mailmessage);
                Bundle b= new Bundle();




                b.putString("chatWith",chatWith);
                b.putString("user1",user1);
                b.putString("user2",user2);
                Toast.makeText(getBaseContext(), "Now you can chat the client whose product you chose..!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), chat.class);
                i.putExtras(b);
                startActivity(i);



            }
        });}
    void getmail(String uid)
    {
        DatabaseReference mgetmail=mdatabase.getReference().child("Users").child(uid);
        mgetmail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 umail=dataSnapshot.child("email").getValue(String.class);
                 //uname=dataSnapshot.child("name").getValue(String.class);
                 //uphone=dataSnapshot.child("phone").getValue(String.class);
                 //ucity=dataSnapshot.child("city").getValue(String.class);
                //mailmessage="We have found someone who is interested in your product that you put on our app. Details given below\n Name:"+uname+"\n Phone:"+uphone+"\n Email:"+umail+"\n City:"+ucity+"\nContact him for further details about the proposals.\n\n Thanks and keep using Barter & Rent";

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        String cuser=mauth.getCurrentUser().getUid();
        DatabaseReference mgetdata= mdatabase.getReference().child("Users").child(cuser);
        mgetdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cname= dataSnapshot.child("name").getValue(String.class);
                String cphone= dataSnapshot.child("phone").getValue(String.class);
                String cmail= dataSnapshot.child("email").getValue(String.class);
                String ccity= dataSnapshot.child("city").getValue(String.class);
                mailmessage="We have found someone who is interested in your product that you put on our app. Details given below\n Name:"+cname+"\n Phone:"+cphone+"\n Email:"+cmail+"\n City:"+ccity+"\nContact him for further details about the proposals.\n\n Thanks and keep using Barter & Rent";
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendEmail(String umail,String subject,String mailmessage) {
        //Getting content for email

        //Creating SendMail object
        SendMail sm = new SendMail(this,umail,subject,mailmessage);

        //Executing sendmail to send email
        sm.execute();
    }
     private void sendNotification()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email=umail;


                    //This is a Simple Logic to Send Notification different Device Programmatically....


                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MmU2MmQzNGYtZjIzMS00Y2MwLWE2YjgtNTMxOWQ3YWYxYzFj");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"6f7d291b-bbea-465a-9b42-2e917f444db9\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"userid\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"We have found a user who is interested in your product.Please check your mail for his contact details.\"}"
                                + "}";

                        //OneSignal.syncHashedEmail(send_email);
                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }
}




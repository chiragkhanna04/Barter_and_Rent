package info.loginp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;



public class Chat_yo extends AppCompatActivity {
    private FirebaseDatabase mdatabase, mdb1;
    private DatabaseReference mref, mref1;
    private FirebaseAuth mauth;
    private ListView lv;
    String uid, user1, user2, chatWith;
    int flag=0;
    ProgressDialog mprogress;
    ArrayList<String> keyList = new ArrayList<>();
    ArrayList<String> keyList2 = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_yo);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,keyList);
        lv = (ListView) findViewById(R.id.listview);

        mdatabase = FirebaseDatabase.getInstance();

        mdb1 = FirebaseDatabase.getInstance();
        mauth = FirebaseAuth.getInstance();
        uid = mauth.getCurrentUser().getUid();

        mref = mdatabase.getReference().child("messages");

        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.v("!_@@_Key::>", child.getKey());
                    String users = child.getKey().toString();
                    String[] parts = users.split("_", 2);
                    user1 = parts[0];
                    chatWith = parts[1];
                    Log.v("user1", user1);
                    //Log.v("user2",user2);
                    if (user1.equals(uid)) {

                        Log.v("userIn",user1);
                        Log.v("chatwith",chatWith);
                        keyList2.add(chatWith);

                        Log.v("keylist",keyList.toString());
                        //arrayAdapter.notifyDataSetChanged();
                    }
                }
                onStart();
                //arrayAdapter.notifyDataSetChanged();
                // mAdapter.notifyDataStateChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mref1 = mdatabase.getReference().child("Users");
        mref1.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                for(int i=0; i < keyList2.size();i++) {
                    chatWith=keyList2.get(i).toString();
                    Log.v("chatwithIn", chatWith);
                    user1 = dataSnapshot.child(uid).child("name").getValue(String.class);
                    Log.v("user1", user1);
                    //Log.v("chatwith",chatWith);

                    user2 = dataSnapshot.child(chatWith).child("name").getValue(String.class);
                    Log.v("user2", user2);
                    keyList.add(user2);
                    arrayAdapter.notifyDataSetChanged();


                }

                onStart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });










    }
    @Override
    public void onStart()
    {
        super.onStart();
        /*Log.v("mref",mref.toString());
        if(flag==0) {
            mprogress.setMessage("Loading...!");
            mprogress.show();
            flag=1;
        }*/

        Log.v("keylistdd",keyList.toString());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,keyList);
        lv.setAdapter(arrayAdapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle b = new Bundle();
                user1=uid;
                user2=lv.getItemAtPosition(i).toString();
                chatWith= keyList2.get(i).toString();
                Log.v("keylist2",chatWith);
                b.putString("chatWith",chatWith);
                b.putString("user1",user1);
                b.putString("user2",user2);
                Intent it = new Intent(getApplicationContext(), chat.class);
                it.putExtras(b);
                startActivity(it);
            }
        });
    }



}

package layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import info.loginp.Post;
import info.loginp.R;
import info.loginp.Selectedpost;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mpostlist;
    private FirebaseDatabase mdatabase,mdb1;
    public DatabaseReference mref;
    private FirebaseAuth mauth;
    private RadioGroup rg;
    private RadioButton rButton;
    String uid,city,rval="Barter";

    ProgressDialog mprogress;
    Spinner s;
    static Context mcontext;
    static String postkey;
    FirebaseRecyclerAdapter<Post,PostViewHolder> firebaseRecyclerAdapter;
    int flag=0,cityflag=0;

    private OnFragmentInteractionListener mListener;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        s=(Spinner)getActivity().findViewById(R.id.spinner2);
        s.setEnabled(true);
        s.setVisibility(View.VISIBLE);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // item = parent.getItemAtPosition(position).toString();
                city=parent.getItemAtPosition(position).toString();
                mref=mdb1.getReference().child("Post").child(rval).child(parent.getItemAtPosition(position).toString());
                Log.v("mc",""+mref);
                firebaseRecyclerAdapter.notifyDataSetChanged();
                onStart();
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View mview= inflater.inflate(R.layout.fragment_home, container, false);
        rg = (RadioGroup)mview.findViewById(R.id.radGroup);
        mcontext= mview.getContext();
        mdatabase= FirebaseDatabase.getInstance();
        mdb1=FirebaseDatabase.getInstance();
        mauth=FirebaseAuth.getInstance();
        uid= mauth.getCurrentUser().getUid();
        DatabaseReference cityref = mdatabase.getReference().child("Users").child(uid);
        mprogress= new ProgressDialog(getActivity(),R.style.ProgressBar);


        rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(rg.getCheckedRadioButtonId());
                rval=rb.getText().toString();
                Log.v("rval",rval);
                mref=mdb1.getReference().child("Post").child(rval).child(city);
                onStart();
            }
        });





        cityref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                city= dataSnapshot.child("city").getValue(String.class);
                Log.v("tag",city);
                mref=mdb1.getReference().child("Post").child(rval).child(city);//get into city
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                        R.array.india_cities, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                s.setAdapter(adapter);
                int pos=adapter.getPosition(city);
                s.setSelection(pos);
                mprogress.dismiss();
                onStart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Log.v("rval",rval.toString());

        mref=mdb1.getReference().child("Post").child(rval).child("null");//get into city
        mpostlist=(RecyclerView)mview.findViewById(R.id.postlist);

        mpostlist.setHasFixedSize(true);
        mpostlist.setLayoutManager(new LinearLayoutManager(getContext()));

        return  mview;
    }
    @Override
    public void onStart() {


        super.onStart();
        Log.v("mref",mref.toString());
        if(flag==0) {
            mprogress.setMessage("Loading...!");
            mprogress.show();
            flag=1;
        }
        firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class,R.layout.post_row,PostViewHolder.class,mref
        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, final int position) {

                //final String postcity=getRef(position).getParent().getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getActivity(),model.getImage());
                TextView tv = (TextView)viewHolder.mview.findViewById(R.id.texts1);
                TextView tv1=(TextView)viewHolder.mview.findViewById(R.id.text2);
                ImageView im= (ImageView) viewHolder.mview.findViewById(R.id.images1);
                tv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postkey= getRef(position).getKey();
                        Bundle b= new Bundle();
                        Log.v("Hello",city+position);
                        b.putString("rval",rval);
                        b.putString("city",city);
                        b.putString("key",postkey);
                        Intent i = new Intent(getActivity(), Selectedpost.class);
                        i.putExtras(b);
                        startActivity(i);

                    }
                });
                im.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postkey= getRef(position).getKey();

                        Bundle b= new Bundle();
                        Log.v("Hello",city+position);
                        b.putString("rval",rval);
                        b.putString("city",city);
                        b.putString("key",postkey);
                        Intent i = new Intent(getActivity(), Selectedpost.class);
                        i.putExtras(b);
                        startActivity(i);

                    }
                });
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        postkey= getRef(position).getKey();

                        Bundle b= new Bundle();
                        Log.v("Hello",city+position);
                        b.putString("rval",rval);
                        b.putString("city",city);
                        b.putString("key",postkey);
                        Intent i = new Intent(getActivity(), Selectedpost.class);
                        i.putExtras(b);
                        startActivity(i);

                    }
                });
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postkey= getRef(position).getKey();

                        Bundle b= new Bundle();
                        Log.v("Hello",city+position);
                        b.putString("rval",rval);
                        b.putString("city",city);
                        b.putString("key",postkey);
                        Intent i = new Intent(getActivity(), Selectedpost.class);
                        i.putExtras(b);
                        startActivity(i);
                    }
                });
            }
        };
        mpostlist.setAdapter(firebaseRecyclerAdapter);


    }
    public  static  class PostViewHolder extends RecyclerView.ViewHolder{
        View mview;
        TextView ptitle,pdesc;
        ImageView img;

        public PostViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
            ptitle= (TextView)mview.findViewById(R.id.texts1);
            pdesc= (TextView)mview.findViewById(R.id.text2);
            img= (ImageView) mview.findViewById(R.id.images1);
         /*   ptitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle b= new Bundle();
                    b.putString("key",postkey);
                    Intent i = new Intent(mcontext, Selectedpost.class);
                    i.putExtras(b);
                    mcontext.startActivity(i);
                }
            });
            pdesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent i = new Intent(getActivity(), Selectedpost.class);
                    //startActivity(i);
                    Bundle b= new Bundle();
                    b.putString("key",postkey);
                    Intent i = new Intent(mcontext, Selectedpost.class);
                    i.putExtras(b);
                    mcontext.startActivity(i);
                }
            });
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   Intent i = new Intent(getActivity(), Selectedpost.class);
                    // startActivity(i);
                    Bundle b= new Bundle();
                    b.putString("key",postkey);
                    Intent i = new Intent(mcontext, Selectedpost.class);
                    i.putExtras(b);
                    mcontext.startActivity(i);
                }
            });*/
        }

        public void setTitle(String title)
        {
            // TextView ptitle= (TextView)mview.findViewById(R.id.text1);
            ptitle.setText(title);


        }
        public void setDesc(String desc)
        {
            //TextView pdesc= (TextView)mview.findViewById(R.id.text2);
            pdesc.setText(desc);
        }
        public void setImage(Context ctx,String image)
        {
            //ImageView img= (ImageView) mview.findViewById(R.id.image1);
            Picasso.with(ctx).load(image).into(img);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

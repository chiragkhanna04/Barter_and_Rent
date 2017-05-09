package layout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import info.loginp.Editprofile;
import info.loginp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Button btn;
    private FirebaseAuth mauth;
    private FirebaseDatabase mdatabase;
    private DatabaseReference mref;
    private TextView tname,tmail,tphone,taddress;
    private ImageView dp;
    String email;
    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Spinner s= (Spinner)getActivity().findViewById(R.id.spinner2);
        s.setEnabled(false);
        s.setVisibility(View.GONE);
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
        View mview= inflater.inflate(R.layout.fragment_profile, container, false);
        tname=(TextView)mview.findViewById(R.id.textView);
        tmail=(TextView)mview.findViewById(R.id.tmail);
        tphone=(TextView)mview.findViewById(R.id.tphone);
        taddress=(TextView)mview.findViewById(R.id.taddress);
        dp=(ImageView)mview.findViewById(R.id.profile_pic);
        mauth=FirebaseAuth.getInstance();
        String uid= mauth.getCurrentUser().getUid();
        email= mauth.getCurrentUser().getEmail();

        mdatabase=FirebaseDatabase.getInstance();
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
                tmail.setText(email);
                tphone.setText(phone);
                taddress.setText(address);
                if (image=="default")
                {
                    Drawable res =getResources().getDrawable(R.drawable.defaultphoto);
                    dp.setImageDrawable(res);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btn = (Button)mview.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity(), Editprofile.class);
                startActivity(i);
            }
        });
        return mview;
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

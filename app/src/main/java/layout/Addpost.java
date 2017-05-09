package layout;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import info.loginp.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Addpost.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Addpost#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Addpost extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ImageButton mSelectImage;
    private EditText mtitle,mdesc;
    private Button msubmit;
    private RadioGroup rgrp;
    private RadioButton rButton;
    private StorageReference mstorage;
    private Uri imageuri;
    private ProgressDialog mprogress;
    private FirebaseAuth mauth;
    private FirebaseDatabase mdatabase;
    private DatabaseReference mref,mdb;
    private  String city,uid,rValue;
    private static final int GALLERY_REQUEST=1;
    public Addpost() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Addpost.
     */
    // TODO: Rename and change types and number of parameters
    public static Addpost newInstance(String param1, String param2) {
        Addpost fragment = new Addpost();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        final View InputFragmentView= inflater.inflate(R.layout.fragment_addpost, container, false);
        mSelectImage= (ImageButton)InputFragmentView.findViewById(R.id.imageButton);
        msubmit=(Button)InputFragmentView.findViewById(R.id.button2);
        mtitle= (EditText)InputFragmentView.findViewById(R.id.editText);
        mdesc=(EditText)InputFragmentView.findViewById(R.id.editText3);
        rgrp=(RadioGroup)InputFragmentView.findViewById(R.id.radioGroup);
        mauth=FirebaseAuth.getInstance();
        mstorage= FirebaseStorage.getInstance().getReference();
        mprogress= new ProgressDialog(getActivity());
        uid= mauth.getCurrentUser().getUid();

        mdatabase= FirebaseDatabase.getInstance();

        mref=mdatabase.getReference().child("Users").child(uid);
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                city= dataSnapshot.child("city").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getgallery=new Intent(Intent.ACTION_GET_CONTENT);
                getgallery.setType("image/*");


                startActivityForResult(getgallery,GALLERY_REQUEST);
                Toast.makeText(getContext(), "Done", Toast.LENGTH_LONG).show();
            }
        });
        msubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rgrp.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                rButton = (RadioButton)InputFragmentView.findViewById(selectedId);
                rValue = rButton.getText().toString();



                mprogress.setMessage("Posting");
                mprogress.show();
                Toast.makeText(getContext(), "Posting...", Toast.LENGTH_LONG).show();
                startPosting();
                Toast.makeText(getContext(), "Posted !!", Toast.LENGTH_LONG).show();
                mtitle.setText("");
                mdesc.setText("");
                mSelectImage.setImageDrawable(null);
                mprogress.dismiss();

            }
        });
        return InputFragmentView;
    }

    private void startPosting() {

        final String title= mtitle.getText().toString().trim();
        final String desc= mdesc.getText().toString().trim();
        if(!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(desc)&& imageuri!=null)
        {
            StorageReference filepath=mstorage.child("Post_images").child(imageuri.getLastPathSegment());
            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadurl=taskSnapshot.getDownloadUrl();
                    mdb=mdatabase.getReference().child("Post").child(rValue).child(city);
                    DatabaseReference newpost= mdb.push();
                    newpost.child("title").setValue(title);
                    newpost.child("desc").setValue(desc);
                    newpost.child("image").setValue(downloadurl.toString());
                    newpost.child("uid").setValue(uid);
                }
            });


        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK)
        {

            imageuri=data.getData();
            //  cropCapturedImage(imageuri);
            mSelectImage.setImageURI(imageuri);
        }


    }

   /* private void cropCapturedImage(Uri imageuri) {
        // call the standard crop action intent
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
// indicate image type and Uri of image
        cropIntent.setDataAndType(imageuri, "image/*");
// set crop properties
cropIntent.putExtra("crop", "true");
// indicate aspect of desired crop
cropIntent.putExtra("aspectX", 1);
cropIntent.putExtra("aspectY", 1);
// indicate output X and Y
cropIntent.putExtra("outputX", 500);
cropIntent.putExtra("outputY", 220);
// retrieve data on return
cropIntent.putExtra("return-data", true);
// start the activity – we handle returning in onActivityResult
startActivityForResult(cropIntent, 2);
    }*/


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

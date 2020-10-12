package com.example.GadProjectMobileHealth29.doctorconsult;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyProfileFrag extends Fragment {

    View view;
    private CircleImageView patient_image;
    private TextView patient_name, email_id, phone_no, patient_gender, patient_address, patient_pin;
    private int id;
    private String stringResponse;
    private ImageButton edit;
    Context context;

    OnEditListener onEditListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_my_profile, container, false);

        return  view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        patient_image = (CircleImageView) view.findViewById(R.id.patient_image);
        patient_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onEditListener != null) {
                    onEditListener.onUpdateProfilePic(view);
                }
            }
        });
        patient_name = (TextView) view.findViewById(R.id.patient_name);
        email_id = (TextView) view.findViewById(R.id.email_id);
        phone_no = (TextView) view.findViewById(R.id.phone_no);
        patient_gender = (TextView) view.findViewById(R.id.patient_gender);
        patient_address = (TextView) view.findViewById(R.id.patient_address);
        patient_pin = (TextView) view.findViewById(R.id.patient_pin);
        edit = (ImageButton) view.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onEditListener != null) {
                    onEditListener.onEdit(stringResponse);
                }
            }
        });
        id = SharedPrefManager.getInstance(getContext()).isLoggedIn();

        getData();



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        onEditListener = (OnEditListener) context;

    }



    public void getData() {
//        Toast.makeText(getContext(), "In getDatas()", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest;
        stringRequest = new StringRequest(Request.Method.POST,
                Constants.PATIENT_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            stringResponse = response;
                            boolean error = jsonObject.getBoolean("error");


//                            Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                            if(!error){
//                                Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
                                String photo = jsonObject.getString("photo");
//                                setData(jsonObject);
                                Toast.makeText(getContext(), photo, Toast.LENGTH_SHORT).show();
                                Glide.with(getContext())
                                        .load(photo)
                                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(patient_image);
                                email_id.setText(jsonObject.getString("email"));
                                patient_name.setText(jsonObject.getString("name"));
                                patient_address.setText(jsonObject.getString("address"));
                                patient_gender.setText(jsonObject.getString("gender"));
                                patient_pin.setText(String.valueOf(jsonObject.getInt("pin")));
                                phone_no.setText(String.valueOf(jsonObject.getInt("phone")));

                            }
                            else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), e.getMessage()+"catch", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    public void setData(JSONObject jsonObject) throws JSONException {
        email_id.setText(jsonObject.getString("email"));
        patient_name.setText(jsonObject.getString("name"));
        patient_address.setText(jsonObject.getString("address"));
        patient_gender.setText(jsonObject.getString("gender"));
        patient_pin.setText(jsonObject.getInt("pin"));
        phone_no.setText(jsonObject.getInt("phone"));
    }

    public void updateProfilePic(Bitmap bitmap){
        patient_image.setImageBitmap(bitmap);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        final String image = Base64.encodeToString(imgByte,Base64.DEFAULT);

        StringRequest stringRequest;
        stringRequest = new StringRequest(Request.Method.POST,
                Constants.PATIENT_PROFILE_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");
                            if(!error){
                                String photo = jsonObject.getString("photo");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                Glide.with(getContext())
                                        .load(photo)
                                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(patient_image);
                            }
                            else {
                                message = jsonObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("update_method", "UPDATE_PROFILE_PIC");
                params.put("id",String.valueOf(id));
                params.put("image", image);
                return params;
            }
        };

        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);



    }


    public  interface OnEditListener {
        void onEdit(String stringResponse);
        void onUpdateProfilePic(View view);
    }


}

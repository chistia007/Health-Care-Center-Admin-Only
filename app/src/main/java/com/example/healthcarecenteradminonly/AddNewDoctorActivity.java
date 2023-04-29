package com.example.healthcarecenteradminonly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.healthcarecenteradminonly.databinding.ActivityAddNewDoctorBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewDoctorActivity extends AppCompatActivity {
    ActivityAddNewDoctorBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddNewDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnDocAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String,String> docotor=new HashMap<>();
                docotor.put("doctor_name",binding.DoctorName.getText().toString());
                docotor.put("hospital_address",binding.hspAddress.getText().toString());
                docotor.put("Experience",binding.exp.getText().toString());
                docotor.put("mobile_number",binding.phoneNumber.getText().toString());
                docotor.put("fees",binding.fees.getText().toString());
                docotor.put("id",binding.docotorId.getText().toString());
                docotor.put("limit",binding.limit.getText().toString());
                docotor.put("title",binding.dctTitle.getText().toString());
                docotor.put("time",binding.apptTime.getText().toString());
                docotor.put("taken_appointment",binding.takenAppt.getText().toString());
                CollectionReference col = db.collection(binding.dctTitle.getText().toString());

                col.document(binding.docotorId.getText().toString()).set(docotor).addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddNewDoctorActivity.this, "success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddNewDoctorActivity.this,DoctorDetailsActivity.class));

                });
            }
        });

    }
}
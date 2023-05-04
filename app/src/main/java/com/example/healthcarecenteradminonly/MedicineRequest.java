package com.example.healthcarecenteradminonly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.healthcarecenteradminonly.databinding.ActivityMedicineRequestBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MedicineRequest extends AppCompatActivity {
    List<String> imageUrls;
    ActivityMedicineRequestBinding binding;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMedicineRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://health-care-center-e51e6.appspot.com/Medicine Requests");

        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int numImages = listResult.getItems().size();
                        imageUrls = new ArrayList<>();

                        for (StorageReference item : listResult.getItems()) {
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    imageUrls.add(imageUrl);

                                    if (imageUrls.size() == numImages) {
                                        adapter = new ArrayAdapter<String>(MedicineRequest.this, R.layout.buy_medicine_request_imageview, R.id.buyMedicineView, imageUrls) {
                                            @Override
                                            public View getView(int position, View convertView, ViewGroup parent) {
                                                if (convertView == null) {
                                                    convertView = getLayoutInflater().inflate(R.layout.buy_medicine_request_imageview, parent, false);
                                                }

                                                ImageView imageView = convertView.findViewById(R.id.buyMedicineView);
                                                String imageUrl = getItem(position);

                                                // Load the image from the URL into the ImageView using Glide
                                                if (imageView != null) {
                                                    Glide.with(MedicineRequest.this)
                                                            .load(imageUrl)
                                                            .into(imageView);
                                                }

                                                return convertView;
                                            }
                                        };

                                        binding.medicineRequests.setAdapter(adapter);
                                    }
                                }
                            });
                        }
                    }
                });




        //view on full screen

        binding.medicineRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imageUrl = imageUrls.get(position);

                Intent intent = new Intent(MedicineRequest.this, FullScreenImageActivity.class);
                intent.putExtra("image_url", imageUrl);
                startActivity(intent);
            }
        });




        // Set up the long press listener for the ListView
        binding.medicineRequests.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder=new AlertDialog.Builder(MedicineRequest.this);
                builder.setTitle("Delete selected prescription");
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                });

                builder.setPositiveButton("Delete this prescription", (dialog, which) -> {
                    // Retrieve the Firebase Cloud Storage URL of the long pressed item
                    String storageUrl = imageUrls.get(position);

                    // Get a reference to the storage file that needs to be deleted
                    StorageReference storageRef = storage.getReferenceFromUrl(storageUrl);

                    // Delete the file from the cloud storage
                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            imageUrls.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MedicineRequest.this, "prescription removed", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(MedicineRequest.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                });
                builder.create().show();


                return true;
            }
        });


    }
}
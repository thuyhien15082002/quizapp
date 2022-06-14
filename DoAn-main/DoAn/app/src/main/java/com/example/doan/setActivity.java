package com.example.doan;

import static com.example.doan.CategoryActivity.catList;
import static com.example.doan.CategoryActivity.selected_cat_index;
import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class setActivity extends AppCompatActivity {
    private RecyclerView setsView;
    private Button btn_addSet;
    public static List<String> setsIDs = new ArrayList<>();
    private FirebaseFirestore firestore;
    private Dialog loadingDialog;
    private SetAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_activity);
        Toolbar toolbar = findViewById(R.id.sets_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SETS");
        loadingDialog = new Dialog(setActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        setsView = findViewById(R.id.set_recyler);
        btn_addSet = findViewById(R.id.add_set);
        firestore = FirebaseFirestore.getInstance();
        btn_addSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewSet();

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setsView.setLayoutManager(layoutManager);
        loadSet();
  }

private void loadSet(){
        setsIDs.clear();
        //loadingDialog.show();
        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        long noOfSets = (long)documentSnapshot.get("SETS");

                        for(int i=1; i <= noOfSets; i++)
                        {
                            setsIDs.add(documentSnapshot.getString("SET" + String.valueOf(i) + "_ID"));
                        }
                        catList.get(selected_cat_index).setSetCounter(documentSnapshot.getString("COUNTER"));
                        catList.get(selected_cat_index).setNoOfSets(String.valueOf(noOfSets));
                        adapter = new SetAdapter(setsIDs);
                        setsView.setAdapter(adapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(setActivity.this, "Load Set fail",
                                Toast.LENGTH_SHORT).show();
                        //loadingDialog.dismiss();

                    }
                });

  }
  private void addNewSet(){
      final String curr_cat_id = catList.get(selected_cat_index).getId();
      final String curr_counter = catList.get(selected_cat_index).getSetCounter();
      Map<String,Object> qData = new ArrayMap<>();
      qData.put("COUNT","0");
      firestore.collection("QUIZ").document(curr_cat_id)
              .collection(curr_counter).document("QUESTIONS_LIST")
              .set(qData)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                      Map<String,Object> catDoc = new ArrayMap<>();
                      catDoc.put("COUNTER", String.valueOf(Integer.valueOf(curr_counter) + 1)  );
                      catDoc.put("SET" + String.valueOf(setsIDs.size() + 1) + "_ID", curr_counter);
                      catDoc.put("SETS", setsIDs.size() + 1);
                      firestore.collection("QUIZ").document(curr_cat_id)
                              .update(catDoc)
                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void unused) {
                                      Toast.makeText(setActivity.this, " Set Added Successfully",Toast.LENGTH_SHORT).show();

                                      setsIDs.add(curr_counter);
                                      catList.get(selected_cat_index).setNoOfSets(String.valueOf(setsIDs.size()));
                                      catList.get(selected_cat_index).setSetCounter(String.valueOf(Integer.valueOf(curr_counter) + 1));

                                      adapter.notifyItemInserted(setsIDs.size());
                                      loadingDialog.dismiss();
                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      Toast.makeText(setActivity.this, "Load Set fail",
                                              Toast.LENGTH_SHORT).show();


                                  }
                              });
                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Toast.makeText(setActivity.this, "Load Set fail",
                              Toast.LENGTH_SHORT).show();

                  }
              });

  }
}



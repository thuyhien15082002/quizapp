package com.example.doan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doan.model.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {
private RecyclerView cat_recycle_view;
private Button btn_addCategory, btn_dialog_addCat;
private EditText catName_addDialog;
private Dialog addCategoryDialog, loadingDialog;
private CategoryAdapter adapter;
public static int selected_cat_index=0;
public static List<CategoryModel> catList = new ArrayList<>();
private FirebaseFirestore firestore;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");
        cat_recycle_view = findViewById(R.id.catGridview);
        btn_addCategory = findViewById(R.id.add_cat);
        loadingDialog = new Dialog(CategoryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //add_categorydialog
        addCategoryDialog = new Dialog(CategoryActivity.this);
        addCategoryDialog.setContentView(R.layout.add_category_dialog);
        addCategoryDialog.setCancelable(true);
        addCategoryDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        btn_dialog_addCat = addCategoryDialog.findViewById(R.id.btn_addDialog);
        catName_addDialog = addCategoryDialog.findViewById(R.id.tv_addDialog);

        firestore = FirebaseFirestore.getInstance();


        btn_addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == catName_addDialog) {
                    catName_addDialog.setText("");
                }
                addCategoryDialog.show();
            }
        });
        btn_dialog_addCat.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                if(catName_addDialog.getText().toString().isEmpty())
               {
                  catName_addDialog.setError("Enter Category Name");
                  return;
               }
                  addNewCategory(catName_addDialog.getText().toString());


            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cat_recycle_view.setLayoutManager(layoutManager);
        loadData();
    }
    public void loadData(){
        catList.clear();
        firestore.collection("QUIZ").document("Categories")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()){
                                long count = (long)doc.get("COUNT");
                                for (int i=1; i<=count; i++ ){
                                    String catName = doc.getString("CAT"+String.valueOf(i) + "_NAME");
                                    String catId = doc.getString("CAT"+String.valueOf(i) + "_ID");

                                    catList.add(new CategoryModel(catId, catName, "0", "1"));
                                }
                                adapter = new CategoryAdapter(catList);
                                cat_recycle_view.setAdapter(adapter);

                            }else{
                                Toast.makeText(CategoryActivity.this, "No Category document exists",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }else {
                            Toast.makeText(CategoryActivity.this, "Load catName fail",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void addNewCategory(final String title){
        addCategoryDialog.dismiss();
        loadingDialog.show();
        final Map<String,Object> catData = new ArrayMap<>();
        catData.put("NAME",title);
        catData.put("SETS",0);
        catData.put("COUNTER","1");
        final String doc_id = firestore.collection("QUIZ").document().getId();
              firestore.collection("QUIZ").document(doc_id)
                .set(catData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Map<String,Object> catDoc = new ArrayMap<>();
                        catDoc.put("CAT" + String.valueOf(catList.size() + 1) + "_NAME",title);
                        catDoc.put("CAT" + String.valueOf(catList.size() + 1) + "_ID",doc_id);
                        catDoc.put("COUNT", catList.size() + 1);

                        firestore.collection("QUIZ").document("Categories")
                                .update(catDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(CategoryActivity.this,"Category added successfully",Toast.LENGTH_SHORT).show();

                                        catList.add(new CategoryModel(doc_id,title,"0","1"));

                                        adapter.notifyItemInserted(catList.size());

                                        loadingDialog.dismiss();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CategoryActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CategoryActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
               });



    }

}




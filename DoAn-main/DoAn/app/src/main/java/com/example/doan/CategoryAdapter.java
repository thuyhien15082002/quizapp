package com.example.doan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.model.CategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategoryModel> catList;

    public CategoryAdapter(List<CategoryModel> catList) {
        this.catList = catList;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout,parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        String title = catList.get(position).getCatName();
        holder.setData(title,position, this);
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView catName;
        private  ImageView btn_del;
        private TextView edit_catName;
        private Button btn_editCatName;
        private Dialog editDialog, loadingDialog;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            catName = itemView.findViewById(R.id.catName);
            btn_del = itemView.findViewById(R.id.btn_delete);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progress_bar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


            editDialog = new Dialog(itemView.getContext());
            editDialog.setContentView(R.layout.edit_category_dialog);
            editDialog.setCancelable(true);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            edit_catName = editDialog.findViewById(R.id.editCatName);
            btn_editCatName = editDialog.findViewById(R.id.btn_edit_cat);
        }

        public void setData(String title, int position, final CategoryAdapter adapter) {
           catName.setText(title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CategoryActivity.selected_cat_index = position;
                    Intent intent = new Intent(itemView.getContext(),setActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    edit_catName.setText(catList.get(position).getCatName());
                    editDialog.show();

                    return false;
                }
            });
            btn_editCatName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(edit_catName.getText().toString().isEmpty())
                    {
                        edit_catName.setError("Enter Category Name");
                        return;
                    }

                    updateCategory(edit_catName.getText().toString(), position, itemView.getContext(), adapter);

                }
            });



           btn_del.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                           .setTitle("DELETE CATEGORY")
                           .setMessage("DO YOU WANT TO DELETE THIS SUBJECT?")
                           .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                       deleteCategory(position, itemView.getContext(),adapter );
                               }
                           }).setNegativeButton("CANCLE", null)
                           .setIcon(android.R.drawable.ic_dialog_alert).show();
                   dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundColor(Color.RED);
                   dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(Color.RED);

                   LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                   params.setMargins(0, 0,50, 0);
                   dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);

               }
           });
        }

        private void deleteCategory(final int id, final Context context, CategoryAdapter adapter ){
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Map<String, Object> catDoc = new ArrayMap<>();
            int idex = 1;
            for(int i = 0; i<catList.size();i++){
                if(i != id){
                    catDoc.put("CAT" +String.valueOf(idex)+"_ID", catList.get(i).getId());
                    catDoc.put("CAT" +String.valueOf(idex)+"_NAME", catList.get(i).getCatName());
                    idex++;

                }
            }
            catDoc.put("COUNT" ,idex - 1);
            firestore.collection("QUIZ").document("Categories")
           .set(catDoc)
           .addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void unused) {
                   Toast.makeText(context, "Category deleted successful", Toast.LENGTH_SHORT).show();
                   CategoryActivity.catList.remove(id);
                  // CategoryActivity.catList.remove(catName);
                   adapter.notifyDataSetChanged();




               }
           })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Category deleted successful", Toast.LENGTH_SHORT).show();

                        }
                    });

        }

private void updateCategory(final String catNewName, final int position, final Context context, final CategoryAdapter adapter)
{
    editDialog.dismiss();

    loadingDialog.show();

    Map<String,Object> catData = new ArrayMap<>();
    catData.put("NAME",catNewName);

    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    firestore.collection("QUIZ").document(catList.get(position).getId())
            .update(catData)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Map<String,Object> catDoc = new ArrayMap<>();
                    catDoc.put("CAT" + String.valueOf(position + 1) + "_NAME",catNewName);

                    firestore.collection("QUIZ").document("Categories")
                            .update(catDoc)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(context,"Category Name Changed Successfully",Toast.LENGTH_SHORT).show();
                                    CategoryActivity.catList.get(position).setCatName(catNewName);
                                    adapter.notifyDataSetChanged();

                                    loadingDialog.dismiss();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                }
                            });

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            });


}
    }
}
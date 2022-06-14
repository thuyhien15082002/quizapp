package com.example.doan;

import static com.example.doan.CategoryActivity.catList;
import static com.example.doan.CategoryActivity.selected_cat_index;
import static com.example.doan.QuestionActivity.quesList;
import static com.example.doan.SetAdapter.selected_set_index;
import static com.example.doan.setActivity.setsIDs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.model.QuestionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private List<QuestionModel> ques_List;
    public QuestionAdapter(List<QuestionModel> ques_list) {
        this.ques_List = ques_list;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout,parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder holder, int pos) {
      //  String title = ques_List.get(position).getQuestion();
        holder.setData(pos, this);
    }

    @Override
    public int getItemCount() {
        return ques_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView btn_del;
        private Dialog loadingDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.catName);
            btn_del = itemView.findViewById(R.id.btn_delete);
            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progress_bar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        }

        public void setData(final int pos, final QuestionAdapter adapter) {
            title.setText("QUESTION " + pos );
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),Question_detail_Activity.class);
                    intent.putExtra("ACTION","EDIT");
                    intent.putExtra("Q_ID", pos);
                    itemView.getContext().startActivity(intent);
                }
            });
//            btn_del.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
//                            .setTitle("Delete Question")
//                            .setMessage("Do you want to delete this Question ?")
//                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    deleteQuestion(pos, itemView.getContext(), adapter);
//                                }
//                            })
//                            .setNegativeButton("Cancel",null)
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();
//
//                    dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundColor(Color.RED);
//                    dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(Color.RED);
//
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    params.setMargins(0,0,50,0);
//                    dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);
//                }
//            });
        }

//        private void deleteQuestion(final int pos, final Context context, final QuestionAdapter adapter){
//            loadingDialog.show();
//            final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//            firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
//                    .collection(setsIDs.get(selected_set_index)).document(quesList.get(pos).getQuesID())
//                    .delete()
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Map<String,Object> quesDoc = new ArrayMap<>();
//                            int index=1;
//                            for(int i=0; i< quesList.size(); i++)
//                            {
//                                if(i != pos)
//                                {
//                                    quesDoc.put("Q" + String.valueOf(index) + "_ID", quesList.get(i).getQuesID());
//                                    index++;
//                                }
//                            }
//                            quesDoc.put("COUNT", String.valueOf(index - 1));
//                            firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
//                                    .collection(setsIDs.get(selected_set_index)).document("QUESTIONS_LIST")
//                                    .set(quesDoc)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            Toast.makeText(context,"Question Deleted Successfully",Toast.LENGTH_SHORT).show();
//                                            quesList.remove(pos);
//                                            adapter.notifyDataSetChanged();
//                                            loadingDialog.dismiss();
//
//
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//
//                                            Toast.makeText(context,"fail",Toast.LENGTH_SHORT).show();
//                                            loadingDialog.dismiss();
//                                        }
//                                    });
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(context,"fail",Toast.LENGTH_SHORT).show();
//                            loadingDialog.dismiss();
//
//                        }
//                    });
//        }

    }
}

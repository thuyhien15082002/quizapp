package com.example.doan;

import static com.example.doan.CategoryActivity.catList;
import static com.example.doan.CategoryActivity.selected_cat_index;
import static com.example.doan.QuestionActivity.quesList;
import static com.example.doan.SetAdapter.selected_set_index;
import static com.example.doan.setActivity.setsIDs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.doan.model.QuestionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Question_detail_Activity extends AppCompatActivity {
    private EditText ques, optionA, optionB, optionC, optionD, answer;
    private Button addQB;
    private String qStr, aStr, bStr, cStr, dStr, ansStr;
    private Dialog loadingDialog;
    private FirebaseFirestore firestore;
    private String action;
    private int qID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
       // Toolbar toolbar = findViewById(R.id.ques_detail_toolbar);
        Toolbar toolbar = findViewById(R.id.ques_detail_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ques = findViewById(R.id.question);
        optionA = findViewById(R.id.OptionA);
        optionB = findViewById(R.id.OptionB);
        optionC = findViewById(R.id.OptionC);
        optionD = findViewById(R.id.OptionD);
        answer = findViewById(R.id.Answer);
        addQB = findViewById(R.id.add_ques);
        loadingDialog = new Dialog(Question_detail_Activity.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        if(action.compareTo("EDIT") == 0)
        {
            qID = getIntent().getIntExtra("Q_ID",0);
            loadData(qID);
            getSupportActionBar().setTitle("Question " + String.valueOf(qID + 1));
            addQB.setText("UPDATE");
        }
        else
        {
            getSupportActionBar().setTitle("Question " + String.valueOf(quesList.size() + 1));
            addQB.setText("ADD");
        }
        addQB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                qStr = ques.getText().toString();
                aStr = optionA.getText().toString();
                bStr = optionB.getText().toString();
                cStr = optionC.getText().toString();
                dStr = optionD.getText().toString();
                ansStr = answer.getText().toString();

                if(qStr.isEmpty()) {
                    ques.setError("Enter Question");
                    return;
                }

                if(aStr.isEmpty()) {
                    optionA.setError("Enter option A");
                    return;
                }

                if(bStr.isEmpty()) {
                    optionB.setError("Enter option B ");
                    return;
                }
                if(cStr.isEmpty()) {
                    optionC.setError("Enter option C");
                    return;
                }
                if(dStr.isEmpty()) {
                    optionD.setError("Enter option D");
                    return;
                }
                if(ansStr.isEmpty()) {
                    answer.setError("Enter correct answer");
                    return;
                }

                if(action.compareTo("EDIT") == 0)
                {
                    editQuestion();
                }
                else {
                    addNewQuestion();
                }

            }
        });
    }


    private void addNewQuestion()
    {
        loadingDialog.show();

        Map<String,Object> quesData = new ArrayMap<>();

        quesData.put("QUESTION",qStr);
        quesData.put("A",aStr);
        quesData.put("B",bStr);
        quesData.put("C",cStr);
        quesData.put("D",dStr);
        quesData.put("ANSWER",ansStr);


        final String doc_id = firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                .collection(setsIDs.get(selected_set_index)).document().getId();

        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                .collection(setsIDs.get(selected_set_index)).document(doc_id)
                .set(quesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Map<String,Object> quesDoc = new ArrayMap<>();
                        quesDoc.put("Q" + String.valueOf(quesList.size() + 1) + "_ID", doc_id);
                        quesDoc.put("COUNT",String.valueOf(quesList.size() + 1));

                        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                                .collection(setsIDs.get(selected_set_index)).document("QUESTIONS_LIST")
                                .update(quesDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Question_detail_Activity.this, " Question Added Successfully", Toast.LENGTH_SHORT).show();

                                        quesList.add(new QuestionModel(
                                                doc_id,
                                                qStr,aStr,bStr,cStr,dStr, Integer.valueOf(ansStr)
                                        ));

                                        loadingDialog.dismiss();
                                        Question_detail_Activity.this.finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Question_detail_Activity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Question_detail_Activity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });


    }
    private void loadData(int id)
    {
        ques.setText(quesList.get(id).getQuestion());
        optionA.setText(quesList.get(id).getOptionA());
        optionB.setText(quesList.get(id).getOptionB());
        optionC.setText(quesList.get(id).getOptionC());
        optionD.setText(quesList.get(id).getOptionD());
        answer.setText(String.valueOf(quesList.get(id).getCorectAns()));
    }
    private void editQuestion()
    {
        loadingDialog.show();

        Map<String,Object> quesData = new ArrayMap<>();
        quesData.put("QUESTION", qStr);
        quesData.put("A",aStr);
        quesData.put("B",bStr);
        quesData.put("C",cStr);
        quesData.put("D",dStr);
        quesData.put("ANSWER",ansStr);


        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                .collection(setsIDs.get(selected_set_index)).document(quesList.get(qID).getQuesID())
                .set(quesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(Question_detail_Activity.this,"Question updated successfully",Toast.LENGTH_SHORT).show();

                        quesList.get(qID).setQuestion(qStr);
                        quesList.get(qID).setOptionA(aStr);
                        quesList.get(qID).setOptionB(bStr);
                        quesList.get(qID).setOptionC(cStr);
                        quesList.get(qID).setOptionD(dStr);
                        quesList.get(qID).setCorectAns(Integer.valueOf(ansStr));

                        loadingDialog.dismiss();
                        Question_detail_Activity.this.finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Question_detail_Activity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });

    }


}



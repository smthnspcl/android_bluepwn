package io.eberlein.insane.bluepwn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import io.paperdb.Paper;

// todo fix duplicating items

public class StagerActivity extends AppCompatActivity {

     @BindView(R.id.name) EditText name;
     @BindView(R.id.save) Button save;
     @BindView(R.id.stages) RecyclerView stages;

     private StageAdapter stageAdapter;

     @OnClick(R.id.add)
     public void addButtonClicked(){
         AlertDialog.Builder b = new AlertDialog.Builder(this);
         LayoutInflater i = this.getLayoutInflater();
         b.setView(i.inflate(R.layout.dialog_stage, null));
         b.setPositiveButton("save", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 AutoCompleteTextView stage = findViewById(R.id.stage);
                 stageAdapter.add(Paper.book("stage").read(stage.getText().toString()));
             }
         });
         b.setNegativeButton("close", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 // todo close dialog
             }
         });
         b.setNeutralButton("new", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 Intent i = new Intent(getApplicationContext(), StageActivity.class); // todo StageActivity
                 i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(i);
             }
         });
         AlertDialog dialog = b.create();
         dialog.show();
     }

     @OnClick(R.id.save)
     public void saveButtonClicked(){
        Stager s = new Stager();
        s.name = name.getText().toString();
        s.lastModified = new Date();
        Paper.book("action").write(s.id, s);
        finish();
     }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stager);
        ButterKnife.bind(this);
        initFromIntent();
        stageAdapter = new StageAdapter();
        stages.setLayoutManager(new LinearLayoutManager(this));
        stages.setAdapter(stageAdapter);
    }

    private void initFromIntent(){ // todo fill stages
         Bundle b = getIntent().getExtras();
         if(b != null){
             String uuid = getIntent().getExtras().getString("uuid");
             Stager s = Paper.book("action").read(uuid, new Stager());
             if(s != null){
                 name.setText(s.name);
             }
         }
    }
}

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
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// todo fix duplicating items

public class StagerActivity extends AppCompatActivity {
     @BindView(R.id.service) EditText service;
     @BindView(R.id.name) EditText name;
     @BindView(R.id.save) Button save;
     @BindView(R.id.recycler) RecyclerView recycler;

     private StageAdapter stageAdapter;
     private Stager stager;
     private Service _service;

     @OnClick(R.id.add)
     public void addButtonClicked(){
         AlertDialog.Builder b = new AlertDialog.Builder(this);
         LayoutInflater i = this.getLayoutInflater();
         b.setView(i.inflate(R.layout.dialog_stage, null));
         b.setPositiveButton("save", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 AutoCompleteTextView stage = findViewById(R.id.stage);
                 stageAdapter.add(Stage.get(stage.getText().toString()));
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
                 Intent i = new Intent(getApplicationContext(), StageActivity.class);
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
        s.uuid = service.getText().toString();
        s.name = name.getText().toString();
        s.lastModified = new Date();
        s.save();
        finish();
     }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stager);
        ButterKnife.bind(this);
        _service = Service.get(getIntent().getStringExtra("service"));
        stageAdapter = new StageAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(stageAdapter);
        stageAdapter.setOnItemClickListener(new StageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getApplicationContext(), StageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("uuid", stageAdapter.get(p).uuid);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        stager = Stager.get(null);
        name.setText(stager.name);
        service.setText(stager.uuid);
        stageAdapter.empty();
        stageAdapter.addAll(Stage.get());
    }
}

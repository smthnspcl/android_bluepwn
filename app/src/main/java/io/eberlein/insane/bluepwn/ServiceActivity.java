package io.eberlein.insane.bluepwn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class ServiceActivity extends AppCompatActivity {
    @BindView(R.id.nameEditText) EditText nameET;
    @BindView(R.id.uuidEditText) EditText uuidET;
    @BindView(R.id.descriptionEditText) EditText descriptionET;
    @BindView(R.id.recycler) RecyclerView recycler;

    @OnClick(R.id.save)
    public void saveBtnClicked(){
        service.name = nameET.getText().toString();
        service.uuid = uuidET.getText().toString();
        service.description = descriptionET.getText().toString();
        service.save();
        finish();
    }

    @OnClick(R.id.add)
    public void addBtnClicked(){
        Intent i = new Intent(this, StagerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("service", service.uuid);
        startActivity(i);
    }

    private Service service;
    private StagerAdapter stagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);
        service = Paper.book("service").read(getIntent().getStringExtra("service"));
        uuidET.setText(service.uuid);
        nameET.setText(service.name);
        descriptionET.setText(service.description);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        stagerAdapter = new StagerAdapter();
        recycler.setAdapter(stagerAdapter);
        stagerAdapter.setOnItemClickListener(new StagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getApplicationContext(), StagerActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id", stagerAdapter.get(p).id);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        stagerAdapter.empty();
        stagerAdapter.addAll(service.getStagers());
    }
}

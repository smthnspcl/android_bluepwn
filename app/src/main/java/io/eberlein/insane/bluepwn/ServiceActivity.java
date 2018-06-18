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
    @BindView(R.id.name) EditText name;
    @BindView(R.id.uuid) EditText uuid;
    @BindView(R.id.description) EditText description;
    @BindView(R.id.recycler) RecyclerView recycler;

    @OnClick(R.id.save)
    public void saveBtnClicked(){
        service.name = name.getText().toString();
        service.uuid = uuid.getText().toString();
        service.description = description.getText().toString();
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
        service = Service.getExistingOrNew(getIntent().getStringExtra("uuid"));
        uuid.setText(service.uuid);
        name.setText(service.name);
        description.setText(service.description);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        stagerAdapter = new StagerAdapter();
        recycler.setAdapter(stagerAdapter);
        stagerAdapter.setOnItemClickListener(new StagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getApplicationContext(), StagerActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("uuid", stagerAdapter.get(p).uuid);
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

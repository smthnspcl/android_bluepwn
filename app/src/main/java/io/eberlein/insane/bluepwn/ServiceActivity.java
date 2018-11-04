package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceActivity extends AppCompatActivity {
    //@BindView(R.id.name) EditText name;
    //@BindView(R.id.uuid) EditText uuid;
    //@BindView(R.id.description) EditText description;
    //@BindView(R.id.recycler) RecyclerView recycler;

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;

    /*
    @OnClick(R.id.save)
    public void saveBtnClicked(){
        //service.name = name.getText().toString();
        //service.uuid = uuid.getText().toString();
        //service.description = description.getText().toString();
        service.save();
        finish();
    }


    @OnClick(R.id.add)
    public void addBtnClicked(){
        Intent i = new Intent(this, StagerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("uuid", service.uuid);
        startActivity(i);
    }

    */

    private Service service;
    private StagerAdapter stagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);
        service = Service.getExistingOrNew(getIntent().getStringExtra("uuid"));
        setTitle("service: " + service.name);

        viewPager.setAdapter(new ServiceFragmentPagerAdapter(getSupportFragmentManager(), service));
        tabLayout.setupWithViewPager(viewPager);

        /*
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
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.onResume(this.getClass());
        //stagerAdapter.empty();
        //stagerAdapter.addAll(service.getStagers());
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.onStart(this.getClass());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.onStop(this.getClass());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.onDestroy(this.getClass());
    }
}

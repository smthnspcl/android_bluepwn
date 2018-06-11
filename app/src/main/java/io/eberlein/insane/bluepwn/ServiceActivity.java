package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class ServiceActivity extends AppCompatActivity {
    @BindView(R.id.nameEditText) EditText nameET;
    @BindView(R.id.uuidEditText) EditText uuidET;
    @BindView(R.id.descriptionEditText) EditText descriptionET;

    @OnClick(R.id.save)
    public void saveBtnClicked(){
        service.name = nameET.getText().toString();
        service.uuid = uuidET.getText().toString();
        service.description = descriptionET.getText().toString();
        service.save();
        finish();
    }

    private Service service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);
        service = Paper.book("service").read(getIntent().getStringExtra("service"));
        uuidET.setText(service.uuid);
        nameET.setText(service.name);
        descriptionET.setText(service.description);
    }
}

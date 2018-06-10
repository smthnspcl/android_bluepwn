package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class UUIDActivity extends AppCompatActivity {
    @BindView(R.id.nameEditText) EditText nameET;
    @BindView(R.id.uuidEditText) EditText uuidET;

    @OnClick(R.id.saveBtn)
    public void saveBtnClicked(){
        service.name = nameET.getText().toString();
        service.uuid = uuidET.getText().toString();
        Paper.book("service").write(service.uuid, service);
        finish();
    }

    private Service service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uuid);
        ButterKnife.bind(this);
        service = Paper.book("parcelUuid").read(getIntent().getStringExtra("service"));
        uuidET.setText(service.uuid);
        nameET.setText(service.name);
    }
}

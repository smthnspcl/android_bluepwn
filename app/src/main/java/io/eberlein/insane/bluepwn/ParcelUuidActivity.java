package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class ParcelUuidActivity extends AppCompatActivity {

    @BindView(R.id.uuidLabel) TextView uuid;
    @BindView(R.id.nameEditText) EditText name;
    @BindView(R.id.descriptionEditText) EditText description;

    @OnClick(R.id.saveBtn)
    public void saveBtnClicked(){
        parcelUuid.name = name.getText().toString();
        parcelUuid.description = description.getText().toString();
        Paper.book("parcelUuid").write(parcelUuid.uuid.toString(), parcelUuid);
    }

    private ParcelUuid parcelUuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uuid);
        ButterKnife.bind(this);
        parcelUuid = Paper.book("parcelUuid").read(getIntent().getStringExtra("id"));
        uuid.setText(parcelUuid.uuid.getUuid().toString());
        name.setText(parcelUuid.name);
        description.setText(parcelUuid.description);
    }
}

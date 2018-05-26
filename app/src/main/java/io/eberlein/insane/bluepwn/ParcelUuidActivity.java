package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ParcelUuidActivity extends AppCompatActivity {

    @BindView(R.id.uuidLabel) TextView uuid;
    @BindView(R.id.nameEditText) EditText name;
    @BindView(R.id.descriptionEditText) EditText description;

    @OnClick(R.id.saveBtn)
    public void saveBtnClicked(){
        parcelUuid.name = name.getText().toString();
        parcelUuid.description = description.getText().toString();
        FlowManager.getDatabase(LocalDatabase.class).executeTransaction(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                parcelUuid.save(databaseWrapper);
            }
        });
    }

    private ParcelUuid parcelUuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uuid);
        ButterKnife.bind(this);
        parcelUuid = SQLite.select().from(ParcelUuid.class).where(ParcelUuid_Table.id.eq(getIntent().getLongExtra("id", -1))).querySingle();
        uuid.setText(parcelUuid.uuid.getUuid().toString());
        name.setText(parcelUuid.name);
        description.setText(parcelUuid.description);
    }
}

package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment{

    @BindView(R.id.serverEditText) EditText server;
    @BindView(R.id.usernameEditText) EditText username;
    @BindView(R.id.passwordEditText) EditText password;

    @OnClick(R.id.saveBtn)
    public void saveBtnClicked(){
        settings.mongoDBSettings = new MongoDBSettings(server.getText().toString(), username.getText().toString(), password.getText().toString());
        FlowManager.getDatabase(LocalDatabase.class).executeTransaction(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                settings.save(databaseWrapper);
            }
        });
        Toast.makeText(getContext(), "settings saved", Toast.LENGTH_SHORT).show();
    }

    private Settings settings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        settings = SQLite.select().from(Settings.class).querySingle();
        if(settings == null) settings = new Settings();
        return v;
    }
}

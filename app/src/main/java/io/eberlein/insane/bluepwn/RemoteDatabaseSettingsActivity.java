package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class RemoteDatabaseSettingsActivity extends AppCompatActivity{

    @BindView(R.id.serverEditText) EditText server;
    @BindView(R.id.usernameEditText) EditText username;
    @BindView(R.id.passwordEditText) EditText password;
    @BindView(R.id.authenticationCheckbox) CheckBox authenticationCheckbox;
    @BindView(R.id.sslCheckbox) CheckBox sslCheckbox;

    private RemoteDBSettings remoteDBSettings;

    private void populateSettings(){
        server.setText(remoteDBSettings.server);
        username.setText(remoteDBSettings.username);
        password.setText(remoteDBSettings.password);
        authenticationCheckbox.setChecked(remoteDBSettings.authentication != null);
        sslCheckbox.setChecked(remoteDBSettings.ssl);
    }

    private void saveSettings(){
        remoteDBSettings.server = server.getText().toString();
        if(!remoteDBSettings.server.endsWith("/")) remoteDBSettings.server += "/";
        remoteDBSettings.authentication = authenticationCheckbox.isChecked();
        remoteDBSettings.username = username.getText().toString();
        remoteDBSettings.password = password.getText().toString();
        remoteDBSettings.ssl = sslCheckbox.isChecked();
        remoteDBSettings.save();
        Toast.makeText(this, "settings saved", Toast.LENGTH_SHORT).show();
    }

    @OnCheckedChanged(R.id.authenticationCheckbox)
    public void authenticationCheckboxChanged(){
        username.setEnabled(authenticationCheckbox.isChecked());
        password.setEnabled(authenticationCheckbox.isChecked());
        remoteDBSettings.authentication = authenticationCheckbox.isChecked();
    }

    @OnCheckedChanged(R.id.sslCheckbox)
    public void sslCheckboxChanged(){
        remoteDBSettings.ssl = sslCheckbox.isChecked();
    }

    @OnClick(R.id.saveBtn)
    public void saveBtnClicked(){
        saveSettings();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_database_settings);
        setTitle("remote database");
        ButterKnife.bind(this);
        remoteDBSettings = new RemoteDBSettings();
        username.setEnabled(false);
        password.setEnabled(false);
        remoteDBSettings = RemoteDBSettings.get();
        if(remoteDBSettings == null) remoteDBSettings = new RemoteDBSettings();
        else populateSettings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveSettings();
    }

}

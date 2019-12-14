package io.eberlein.insane.bluepwn.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.object.RemoteDBSettings;

public class RemoteDatabaseSettingsActivity extends AppCompatActivity{

    @BindView(R.id.serverEditText) EditText server;
    @BindView(R.id.usernameEditText) EditText username;
    @BindView(R.id.passwordEditText) EditText password;
    @BindView(R.id.authenticationCheckbox) CheckBox authenticationCheckbox;
    @BindView(R.id.sslCheckbox) CheckBox sslCheckbox;

    private RemoteDBSettings remoteDBSettings;

    private void populateSettings(){
        server.setText(remoteDBSettings.getServer());
        username.setText(remoteDBSettings.getUsername());
        password.setText(remoteDBSettings.getPassword());
        authenticationCheckbox.setChecked(remoteDBSettings.getAuthentication() != null);
        sslCheckbox.setChecked(remoteDBSettings.getSsl());
    }

    private void saveSettings(){
        remoteDBSettings.setServer(server.getText().toString());
        if (!remoteDBSettings.getServer().endsWith("/"))
            remoteDBSettings.setServer(remoteDBSettings.getServer() + "/");
        remoteDBSettings.setAuthentication(authenticationCheckbox.isChecked());
        remoteDBSettings.setUsername(username.getText().toString());
        remoteDBSettings.setPassword(password.getText().toString());
        remoteDBSettings.setSsl(sslCheckbox.isChecked());
        remoteDBSettings.save();
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
    }

    @OnCheckedChanged(R.id.authenticationCheckbox)
    public void authenticationCheckboxChanged(){
        username.setEnabled(authenticationCheckbox.isChecked());
        password.setEnabled(authenticationCheckbox.isChecked());
        remoteDBSettings.setAuthentication(authenticationCheckbox.isChecked());
    }

    @OnCheckedChanged(R.id.sslCheckbox)
    public void sslCheckboxChanged(){
        remoteDBSettings.setSsl(sslCheckbox.isChecked());
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

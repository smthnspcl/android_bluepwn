package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.paperdb.Paper;

// todo
// layers pls
// drop/burn individual books in own activity
// database settings in own activity
// ui settings in own activity
public class SettingsFragment extends Fragment{

    @BindView(R.id.serverEditText) EditText server;
    @BindView(R.id.usernameEditText) EditText username;
    @BindView(R.id.passwordEditText) EditText password;
    @BindView(R.id.authenticationCheckbox) CheckBox authenticationCheckbox;
    @BindView(R.id.sslCheckbox) CheckBox sslCheckbox;

    @OnCheckedChanged(R.id.authenticationCheckbox)
    public void authenticationCheckboxChanged(){
        username.setEnabled(authenticationCheckbox.isChecked());
        password.setEnabled(authenticationCheckbox.isChecked());
        remoteDBSettings.setAuthentication(authenticationCheckbox.isChecked());
        saveSettings();
    }

    @OnCheckedChanged(R.id.sslCheckbox)
    public void sslCheckboxChanged(){
        remoteDBSettings.setSsl(sslCheckbox.isChecked());
        saveSettings();
    }

    @OnClick(R.id.saveBtn)
    public void saveBtnClicked(){
        saveSettings();
    }

    private RemoteDBSettings remoteDBSettings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        remoteDBSettings = new RemoteDBSettings();
        username.setEnabled(false);
        password.setEnabled(false);
        remoteDBSettings = Paper.book("settings").read("remote");
        if(remoteDBSettings == null) remoteDBSettings = new RemoteDBSettings();
        else populateSettings();
        return v;
    }

    private void populateSettings(){
        server.setText(remoteDBSettings.getServer());
        username.setText(remoteDBSettings.getUsername());
        password.setText(remoteDBSettings.getPassword());
        authenticationCheckbox.setChecked(remoteDBSettings.getAuthentication() != null);
        sslCheckbox.setChecked(remoteDBSettings.getSsl());
    }

    private void saveSettings(){
        remoteDBSettings.setServer(server.getText().toString());
        remoteDBSettings.setAuthentication(authenticationCheckbox.isChecked());
        remoteDBSettings.setUsername(username.getText().toString());
        remoteDBSettings.setPassword(password.getText().toString());
        remoteDBSettings.setSsl(sslCheckbox.isChecked());
        Paper.book("settings").write("remote", remoteDBSettings);
        Toast.makeText(getContext(), "settings saved", Toast.LENGTH_SHORT).show();
    }
}

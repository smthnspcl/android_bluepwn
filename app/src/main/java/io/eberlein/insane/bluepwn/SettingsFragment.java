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
        settings.remoteDBSettings.setAuthentication(authenticationCheckbox.isChecked());
        saveSettings();
    }

    @OnCheckedChanged(R.id.sslCheckbox)
    public void sslCheckboxChanged(){
        settings.remoteDBSettings.setSsl(sslCheckbox.isChecked());
        saveSettings();
    }

    @OnClick(R.id.saveBtn)
    public void saveBtnClicked(){
        saveSettings();
    }

    private Settings settings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        settings = new Settings();
        username.setEnabled(false);
        password.setEnabled(false);
        settings = Paper.book("settings").read("remote");
        if(settings == null) settings = new Settings();
        else populateSettings();
        return v;
    }

    private void populateSettings(){
        server.setText(settings.remoteDBSettings.getServer());
        username.setText(settings.remoteDBSettings.getUsername());
        password.setText(settings.remoteDBSettings.getPassword());
        authenticationCheckbox.setChecked(settings.remoteDBSettings.getAuthentication() != null);
        sslCheckbox.setChecked(settings.remoteDBSettings.getSsl());
    }

    private void saveSettings(){
        settings.remoteDBSettings.setServer(server.getText().toString());
        settings.remoteDBSettings.setAuthentication(authenticationCheckbox.isChecked());
        settings.remoteDBSettings.setUsername(username.getText().toString());
        settings.remoteDBSettings.setPassword(password.getText().toString());
        settings.remoteDBSettings.setSsl(sslCheckbox.isChecked());
        Paper.book("settings").write("remote", settings);
        Toast.makeText(getContext(), "settings saved", Toast.LENGTH_SHORT).show();
    }
}

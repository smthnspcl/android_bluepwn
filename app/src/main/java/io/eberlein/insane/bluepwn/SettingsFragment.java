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
    @BindView(R.id.databaseEditText) EditText database;
    @BindView(R.id.authenticationCheckbox) CheckBox authenticationCheckbox;

    @OnCheckedChanged(R.id.authenticationCheckbox)
    public void authenticationCheckboxChanged(){
        settings.mongoDBSettings.authentication = authenticationCheckbox.isChecked();
    }

    @OnClick(R.id.saveBtn)
    public void saveBtnClicked(){
        settings.mongoDBSettings = new MongoDBSettings(server.getText().toString(), username.getText().toString(), password.getText().toString());
        Paper.book("settings").write("settings", settings);
        Toast.makeText(getContext(), "settings saved", Toast.LENGTH_SHORT).show();
    }

    private Settings settings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        settings = Paper.book("settings").read("settings");
        if(settings == null) settings = new Settings();
        return v;
    }
}

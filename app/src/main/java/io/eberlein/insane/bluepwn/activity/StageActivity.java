package io.eberlein.insane.bluepwn.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.RadioButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.object.Stage;

public class StageActivity extends AppCompatActivity {

    @BindView(R.id.name) EditText name;
    @BindView(R.id.data) EditText data;
    @BindView(R.id.hexRadio) RadioButton hexRadio;
    @BindView(R.id.byteRadio) RadioButton byteRadio;
    @BindView(R.id.textRadio) RadioButton textRadio;

    @OnClick(R.id.save)
    public void onSaveBtnClicked(){
        save();
        finish();
    }

    private void save(){
        stage.setName(name.getText().toString());
        if (hexRadio.isChecked()) stage.setDataType("hex");
        else if (byteRadio.isChecked()) stage.setDataType("byte");
        else if (textRadio.isChecked()) stage.setDataType("text");
        stage.setData(data.getText().toString());
        stage.save();
    }

    Stage stage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);
        ButterKnife.bind(this);
        stage = Stage.get(getIntent().getStringExtra("uuid"));
        setTitle("stage: " + stage.getName());
    }

    private void setDataType(){
        switch (stage.getDataType()) {
            case "hex":
                hexRadio.setChecked(true);
                break;
            case "byte":
                byteRadio.setChecked(true);
                break;
            case "text":
                textRadio.setChecked(true);
                break;
            default:
                byteRadio.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        name.setText(stage.getName());
        data.setText(stage.getData());
        setDataType();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stage.save();
    }
}

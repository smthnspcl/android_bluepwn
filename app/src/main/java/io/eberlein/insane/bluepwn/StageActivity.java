package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.RadioButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        stage.name = name.getText().toString();
        if(hexRadio.isChecked()) stage.dataType = "hex";
        else if(byteRadio.isChecked()) stage.dataType = "byte";
        else if(textRadio.isChecked()) stage.dataType = "text";
        stage.data = data.getText().toString();
        stage.save();
    }

    Stage stage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);
        ButterKnife.bind(this);
        stage = Stage.get(getIntent().getStringExtra("id"));
    }

    private void setDataType(){
        switch (stage.dataType){
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
        name.setText(stage.name);
        data.setText(stage.data);
        setDataType();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stage.save();
    }
}

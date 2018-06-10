package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import io.paperdb.Paper;

// todo fix duplicating items

public class StagerActivity extends AppCompatActivity {

     @BindView(R.id.name) EditText name;
     @BindView(R.id.save) Button save;

     @OnClick(R.id.save)
     public void saveButtonClicked(){
        Stager s = new Stager();
        s.name = name.getText().toString();
        s.lastModified = new Date();
        Paper.book("action").write(s.id, s);
        finish();
     }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stager);
        ButterKnife.bind(this);
        initFromIntent();
    }

    private void initFromIntent(){
         Bundle b = getIntent().getExtras();
         if(b != null){
             String uuid = getIntent().getExtras().getString("uuid");
             Stager s = Paper.book("action").read(uuid, new Stager());
             if(s != null){
                 name.setText(s.name);
             }
         }
    }
}

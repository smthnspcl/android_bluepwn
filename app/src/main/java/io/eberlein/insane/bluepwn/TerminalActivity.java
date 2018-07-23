package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static io.eberlein.insane.bluepwn.Static.TYPE_CLASSIC;
import static io.eberlein.insane.bluepwn.Static.TYPE_DUAL;
import static io.eberlein.insane.bluepwn.Static.TYPE_LE;

public class TerminalActivity extends AppCompatActivity {

    @BindView(R.id.log) EditText log;
    @BindView(R.id.cmd) EditText cmd;

    TerminalSession session;
    Device device;

    @OnClick(R.id.send)
    public void sendClicked(){
        String data = cmd.getText().toString();
        // todo
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        ButterKnife.bind(this);
        device = Device.getExistingOrNew(getIntent().getStringExtra("address"));
        session = TerminalSession.get(getIntent().getStringExtra("session"));
        switch(device.type){
            case TYPE_DUAL:
                break;
            case TYPE_LE:
                break;
            case TYPE_CLASSIC:
                break;
            default:

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.save();
    }
}
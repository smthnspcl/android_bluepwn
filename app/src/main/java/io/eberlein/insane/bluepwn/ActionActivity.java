package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

// todo fix duplicating items

public class ActionActivity extends AppCompatActivity {

    private String[] spinnerArrayItems = {
            "hex", "text"
    };

    private Boolean actionDataHex = true;

     @BindView(R.id.actionName) EditText actionName;
     @BindView(R.id.actionMacPrefix) AutoCompleteTextView actionMacPrefix;
     @BindView(R.id.actionDataTypeSelector) Spinner actionDataTypeSelector;
     @BindView(R.id.actionDataEditText) EditText actionDataEditText;
     @BindView(R.id.saveAction) Button saveAction;

     @OnClick(R.id.saveAction)
     public void saveActionButtonClicked(){
        Action a = new Action();
        a.name = actionName.getText().toString();
        a.macPrefix = actionMacPrefix.getText().toString();
        a.lastModified = new Date();
        a.data = actionDataEditText.getText().toString();
        a.hex = actionDataHex;
        FlowManager.getDatabase(LocalDatabase.class).executeTransaction(new ITransaction() {
             @Override
             public void execute(DatabaseWrapper databaseWrapper) {
                 a.save(databaseWrapper);
             }});
        finish();
     }

     @OnItemSelected(R.id.actionDataTypeSelector)
     public void setActionDataTypeSelectorClicked(){
         actionDataHex = actionDataTypeSelector.getSelectedItem().toString().equals("hex");
     }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        ButterKnife.bind(this);
        initFromIntent();
        actionDataTypeSelector.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, spinnerArrayItems));
        actionMacPrefix.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, SQLite.select().from(OuiEntry.class).queryList()));
    }

    private void initFromIntent(){
         Bundle b = getIntent().getExtras();
         if(b != null){
             Long id = getIntent().getExtras().getLong("id");
             Action a = SQLite.select().from(Action.class).where(Action_Table.id.eq(id)).querySingle();
             if(a != null){
                 actionName.setText(a.name);
                 actionDataTypeSelector.setSelection(a.hex ? 0 : 1);
                 actionMacPrefix.setText(a.macPrefix);
                 actionDataEditText.setText(a.data);
             }
         }
    }

    @OnItemSelected(R.id.actionDataTypeSelector)
    public void onSpinnerItemSelected(int index){
        // todo get string from index and set actionDataHex = true/false
    }
}

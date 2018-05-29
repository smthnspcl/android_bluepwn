package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class SyncFragment extends Fragment {
    @BindView(R.id.ouiSyncStatusLabel) TextView ouiSyncStatusLabel;
    @BindView(R.id.actionsSyncStatusLabel) TextView actionsSyncStatusLabel;
    @BindView(R.id.devicesSyncStatusLabel) TextView devicesSyncStatusLabel;

    @OnClick(R.id.syncBtn)
    public void syncBtnClicked(){

    }

    @OnClick(R.id.checkBtn)
    public void checkBtnClicked(){

    }

    private MongoDBSettings mongoDBSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sync, container, false);
        ButterKnife.bind(this, v);
        mongoDBSettings = Paper.book("mongodb").read("settings");
        return v;
    }

    private void checkActions(){}

    private void checkDevices(){}

    private void checkParcelUuids(){}

    private void checkLocations(){}

    private void checkOui(){}

    private void checkScans(){}
}

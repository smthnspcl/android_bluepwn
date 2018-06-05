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


    @OnClick(R.id.checkSyncBtn)
    public void syncBtnClicked(){
        if(!actionsUpToDate()) updateActions();
        if(!devicesUpToDate()) updateDevices();
        if(!parcelUuidsUptoDate()) updateParcelUuids();
        if(!locationsUpToDate()) updateLocations();
        if(!ouiUpToDate()) updateOui();
        if(!scansUpToDate()) updateScans();
    }

    private RemoteDBSettings remoteDBSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sync, container, false);
        ButterKnife.bind(this, v);
        remoteDBSettings = Paper.book("remote").read("settings");
        return v;
    }

    private boolean actionsUpToDate(){
        return false;
    }

    private boolean updateActions(){
        return false;
    }

    private boolean devicesUpToDate(){
        return false;
    }

    private boolean updateDevices(){return false;}

    private boolean parcelUuidsUptoDate(){
        return false;
    }

    private boolean updateParcelUuids(){return false;}

    private boolean locationsUpToDate(){
        return false;
    }

    private boolean updateLocations(){return false;}

    private boolean ouiUpToDate(){
        return false;
    }

    private boolean updateOui(){return false;}

    private boolean scansUpToDate(){
        return false;
    }

    private boolean updateScans(){return false;}
}

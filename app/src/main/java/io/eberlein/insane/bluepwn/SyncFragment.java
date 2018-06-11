package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class SyncFragment extends Fragment {
    @BindView(R.id.sync) Button sync;
    @BindView(R.id.ouiSyncStatusLabel) TextView ouiSyncStatusLabel;
    @BindView(R.id.devicesSyncStatusLabel) TextView devicesSyncStatusLabel;
    @BindView(R.id.uuidsSyncStatusLabel) TextView uuidsSyncStatusLabel;
    @BindView(R.id.locationsSyncStatusLabel) TextView locationsSyncStatusLabel;
    @BindView(R.id.scansSyncStatusLabel) TextView scansSyncStatusLabel;
    @BindView(R.id.stagersSyncStatusLabel) TextView stagersSyncStatusLabel;
    @BindView(R.id.stagesSyncStatusLabel) TextView stagesSyncStatusLabel;

    @OnClick(R.id.sync)
    public void syncBtnClicked(){

    }

    private RemoteDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sync, container, false);
        ButterKnife.bind(this, v);
        database = new RemoteDatabase(getContext(), Paper.book("settings").read("remote"));
        return v;
    }
}

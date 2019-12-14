package io.eberlein.insane.bluepwn.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.object.Service;

public class ServiceTabInfoFragment extends Fragment {
    @BindView(R.id.name) EditText name;
    @BindView(R.id.uuid) EditText uuid;

    private Service service;

    public static ServiceTabInfoFragment newInstance(int p, Service s){
        ServiceTabInfoFragment sof = new ServiceTabInfoFragment();
        Bundle b = new Bundle();
        b.putString("uuid", s.getUuid());
        sof.setArguments(b);
        return sof;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = Service.get(getArguments().getString("uuid"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_info, container, false);
        ButterKnife.bind(this, v);
        name.setText(service.getName());
        uuid.setText(service.getUuid());
        return v;
    }
}

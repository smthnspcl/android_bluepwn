package io.eberlein.insane.bluepwn.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.object.Service;

public class CharacteristicsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.objectlist_search, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    static CharacteristicsFragment newInstance(int p, Service s){
        CharacteristicsFragment cf = new CharacteristicsFragment();
        Bundle b = new Bundle();
        b.putString("uuid", s.getUuid());
        cf.setArguments(b);
        return cf;
    }
}

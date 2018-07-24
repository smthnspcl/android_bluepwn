package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StagesFragment extends Fragment {

    @BindView(R.id.spinner) Spinner selectionSpinner;
    @BindView(R.id.query) AutoCompleteTextView selectionQuery;
    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.fab) RapidFloatingActionButton fab;

    private StagerAdapter stagerAdapter;
    private ArrayAdapter<String> selectionSpinnerAdapter;

    @OnClick(R.id.search)
    public void searchBtnClicked() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.objectlist_search, container, false);
        ButterKnife.bind(this, v);
        selectionSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, Static.STAGE_KEYS));
        return v;
    }
}

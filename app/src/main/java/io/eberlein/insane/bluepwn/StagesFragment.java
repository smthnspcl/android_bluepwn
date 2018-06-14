package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StagesFragment extends Fragment {

    private static final String[] selectionSpinnerAdapterItems = {
            "id", "name", "hex", "data", "macPrefix"
    };

    @BindView(R.id.spinner) Spinner selectionSpinner;
    @BindView(R.id.query) AutoCompleteTextView selectionQuery;
    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.addRecyclerItem) FloatingActionButton addRecyclerItem;

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

        return v;
    }
}

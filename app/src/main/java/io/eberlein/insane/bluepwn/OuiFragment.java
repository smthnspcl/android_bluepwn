package io.eberlein.insane.bluepwn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OuiFragment extends Fragment implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.query) AutoCompleteTextView filters;
    @BindView(R.id.fabLayout) RapidFloatingActionLayout rfaLayout;
    @BindView(R.id.fab) RapidFloatingActionButton fab;

    private RapidFloatingActionHelper rfabHelper;
    private OuiAdapter ouiAdapter;

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        rfabHelper.toggleContent();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.onCreate(this.getClass());
        ouiAdapter = new OuiAdapter();
        getActivity().setTitle("oui lookup");
        populateRapidFloatingActionButton();
    }

    private void populateRapidFloatingActionButton(){
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getContext());
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("filter")
                .setResId(R.drawable.baseline_filter_list_white_48)
                .setIconNormalColor(0xfc9d19)
                .setIconPressedColor(0x96d824)
                .setWrapper(0));
        items.add(new RFACLabelItem<Integer>()
                .setLabel("add")
                .setResId(R.drawable.ic_add_white_48dp)
                .setIconNormalColor(0xe06711)
                .setIconPressedColor(0xe03a10)
                .setWrapper(1));

        rfaContent.setItems(items);
        rfaContent.setIconShadowRadius(RFABTextUtil.dip2px(getContext(), 5));
        rfaContent.setIconShadowColor(0xff888888);
        rfaContent.setIconShadowDy(RFABTextUtil.dip2px(getContext(), 5));
        rfabHelper = new RapidFloatingActionHelper(getContext(), rfaLayout, fab, rfaContent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.objectlist_search, container, false);
        ButterKnife.bind(this, v);
        spinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, Static.OUI_KEYS));
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(ouiAdapter);
        ouiAdapter.addAll(OuiEntry.get());
        ouiAdapter.setOnItemClickListener(new OuiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getContext(), ScanActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("assignment", ouiAdapter.get(p).assignment);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.onStart(this.getClass());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.onStop(this.getClass());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.onResume(this.getClass());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.onDestroy(this.getClass());
    }
}

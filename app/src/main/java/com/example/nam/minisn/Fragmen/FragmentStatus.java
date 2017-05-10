package com.example.nam.minisn.Fragmen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nam.minisn.Adapter.StatusAdapter;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.ItemListview.Status;
import com.example.nam.minisn.R;

import java.util.ArrayList;

public class FragmentStatus extends Fragment {
    private View rootView;
    private Bundle bundle;
    private static StatusAdapter adapter;
    private static ArrayList<Status> data;
    private ListView lvStatus;
    private EditText inputStatus;
    private TextView btnPost;
    public FragmentStatus() {
    }


    public static FragmentStatus newInstance(Bundle bundle) {
        FragmentStatus fragmen = new FragmentStatus();
        fragmen.setArguments(bundle);
        return fragmen;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_tab_status, container, false);
        bundle = getArguments();

        init();
        return rootView;
    }

    public void init(){
        lvStatus = (ListView)rootView.findViewById(R.id.status_lv);
        btnPost = (TextView)rootView.findViewById(R.id.status_btnPost);
        inputStatus = (EditText)rootView.findViewById(R.id.status_input);

        data = new ArrayList<>();
        adapter = new StatusAdapter(getActivity(),R.layout.item_tab_status,data);
        lvStatus.setAdapter(adapter);

        Friend friend = new Friend("chucuoinb","Lại Văn Nam",1,2);
        Status status1 = new Status(friend,System.currentTimeMillis(),"hihihi",true,5,6);
        Status status2 = new Status(friend,System.currentTimeMillis(),"hihihi",false,5,6);
        data.add(status1);
        data.add(status2);
        adapter.notifyDataSetChanged();
    }


    public static StatusAdapter getAdapter() {
        return adapter;
    }

    public static ArrayList<Status> getData() {
        return data;
    }
}

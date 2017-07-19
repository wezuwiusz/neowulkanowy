package io.github.wulkanowy.activity.dashboard.marks;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.wulkanowy.R;

public class MarksFragment extends Fragment {


    final String lista[] = {
            "Donut",
            "Eclair",
            "Froyo",
            "Gingerbread",
            "Honeycomb",
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow"
    };

    public MarksFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_marks, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(),2);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<String> array = new ArrayList<>(Arrays.asList(lista));
        ImageAdapter adapter = new ImageAdapter(view.getContext(),array);
        recyclerView.setAdapter(adapter);

        return view;
    }

}

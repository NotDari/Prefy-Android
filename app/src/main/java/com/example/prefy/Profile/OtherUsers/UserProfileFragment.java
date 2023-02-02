package com.example.prefy.Profile.OtherUsers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.prefy.Profile.ProfilePostsRec.NewProfilePostsGateway;
import com.example.prefy.Profile.User;
import com.example.prefy.R;

public class UserProfileFragment extends Fragment {
    private String UID;
    private User user;
    private SwipeRefreshLayout refreshLayout;
    private Boolean destroyed;
    private OtherUserDataController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getBundle();
        initRefresh(view);
        getData(view);
        return view;
    }

    private void initRefresh(View view){
        refreshLayout = view.findViewById(R.id.ProfileSwipeRefreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //String uid = FirebaseAuth.getInstance().getUid();
                //getUserInfoExecutor executor = new getUserInfoExecutor(uid, HomeProfileFragment.this::taskDone);
                //executor.initExecutor();
                controller.refreshData();
            }
        });
    }

    private void getData(View view){
        destroyed = false;
        RecyclerView recView = view.findViewById(R.id.ProfileRecView);
        NewProfilePostsGateway gateway;
        gateway = new NewProfilePostsGateway(recView, view, user, false);

        gateway.displayView();
        controller = new OtherUserDataController(view,gateway, view.getContext(),user, getActivity());
        controller.initRetrieveData();

    }

    private void getBundle(){
        this.UID = getArguments().getString("UID");
        this.user = getArguments().getParcelable("user");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


}

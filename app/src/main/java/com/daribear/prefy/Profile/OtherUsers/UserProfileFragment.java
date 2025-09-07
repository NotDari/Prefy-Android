package com.daribear.prefy.Profile.OtherUsers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daribear.prefy.Profile.ProfilePostsRec.ProfilePostsGateway;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;

/**
 * The fragment which represents the profile of a user.
 */
public class UserProfileFragment extends Fragment {
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

    /**
     * Sets a refresh listener which allows for the refreshing of data
     * @param view baseview to use
     */
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

    /**
     * Gets data, which means setting up the recylerview and the userController
     * @param view
     */
    private void getData(View view){
        destroyed = false;
        RecyclerView recView = view.findViewById(R.id.ProfileRecView);
        ProfilePostsGateway gateway;
        gateway = new ProfilePostsGateway(getActivity(),recView, view, user, false);

        gateway.displayView();
        controller = new OtherUserDataController(view,gateway, view.getContext(),user, getActivity());
        controller.initRetrieveData();

    }

    /**
     * Get passed in user details
     */
    private void getBundle(){
        this.user = getArguments().getParcelable("user");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


}

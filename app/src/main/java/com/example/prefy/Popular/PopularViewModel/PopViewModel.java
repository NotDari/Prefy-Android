package com.example.prefy.Popular.PopularViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prefy.Popular.PopularActivity;
import com.example.prefy.Popular.PopularPostSet;

public class PopViewModel extends ViewModel {
    private MutableLiveData<PopularPostSet> popPostSetData;
    private PopularPostsRepository popRepo;
    private Context ApplicationContext;





    public void init(Context applicationContext){
        this.ApplicationContext = applicationContext;
       popRepo = PopularPostsRepository.getInstance(applicationContext);
        if (popRepo.getDataStatus() == 0) {
            this.popPostSetData = popRepo.getIntitialData();
        } else {
            this.popPostSetData = popRepo.getPopularSetMutable();
        }
    }

    public LiveData<PopularPostSet> getPostData(){
        return popPostSetData;
    }

    public PopularPostSet singleDataCheck(){
        if (popRepo == null){
            popRepo = PopularPostsRepository.getInstance(ApplicationContext);
        }
        return popRepo.singleDataCheck();
    }


    public String getDataType(){
        return popRepo.getDataType();
    }

    public LiveData<PopularActivity> getActivity(){
        return popRepo.getActivityCount();
    }



    public void getMoreData(){
        if (popRepo.getDataLoading() == false) {
            popRepo.getMoreData();
        }
    }

}

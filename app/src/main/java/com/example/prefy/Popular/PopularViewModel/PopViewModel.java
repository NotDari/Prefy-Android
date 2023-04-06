package com.example.prefy.Popular.PopularViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prefy.Popular.PopularActivity;
import com.example.prefy.Popular.PopularPostSet;
import com.example.prefy.Popular.PopularViewModel.UpdatedPopularViewModel.PopularPostsRepository2;
import com.example.prefy.customClasses.StandardPost;

public class PopViewModel extends ViewModel {
    private MutableLiveData<PopularModelPackage> popPostSetData;
    private PopularPostsRepository2 popRepo;
    private Context ApplicationContext;





    public void init(Context applicationContext){
        this.ApplicationContext = applicationContext;
       popRepo = PopularPostsRepository2.getInstance(applicationContext);
       this.popPostSetData = popRepo.getPopularModelMutable();
    }

    public LiveData<PopularModelPackage> getPostData(){
        return popPostSetData;
    }

    public PopularModelPackage singleDataCheck(){
        if (popRepo == null){
            popRepo = PopularPostsRepository2.getInstance(ApplicationContext);
        }
        return popRepo.singleDataCheck();
    }



    /**
    public LiveData<PopularActivity> getActivity(){
        return popRepo.getActivityCount();
    }
     */

    public void deleteItem(StandardPost post){
        //popRepo.deleteItem(post);
    }

    public void setAdaptorPosition(Integer position){
        popRepo.setViewPagerPosition(position);
    }

    public void refreshData(){
        popRepo.refreshData();
    }

    public void viewChanged(Integer position){

    }

}

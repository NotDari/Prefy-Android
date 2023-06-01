package com.daribear.prefy.Popular.OldPopularSystem;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daribear.prefy.Popular.PopularViewModel.PopularModelPackage;
import com.daribear.prefy.customClasses.Posts.StandardPost;

public class PopViewModel extends ViewModel {
    private MutableLiveData<PopularModelPackage> popPostSetData;
    private PopularPostsRepository popRepo;
    private Context ApplicationContext;





    public void init(Context applicationContext){
        this.ApplicationContext = applicationContext;
       popRepo = PopularPostsRepository.getInstance(applicationContext);
       this.popPostSetData = popRepo.getPopularModelMutable();
    }

    public LiveData<PopularModelPackage> getPostData(){
        return popPostSetData;
    }

    public PopularModelPackage singleDataCheck(){
        if (popRepo == null){
            popRepo = PopularPostsRepository.getInstance(ApplicationContext);
        }
        return popRepo.singleDataCheck();
    }




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

package com.daribear.prefy.Popular;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daribear.prefy.Popular.PopularViewModel.PopularModelPackage;
import com.daribear.prefy.Profile.User;

import lombok.Getter;

/**
 * The viewmodel which represents the gateway beteeen the ui and the popular posts repository.
 * handles the calling for pagination of popular posts to remove them or get more, for the repository.
 */
public class NewPopularViewModel extends ViewModel {
    private MutableLiveData<PopularModelPackage> popPostSetData;

    @Getter
    private MutableLiveData<PopularActivity> popularActivityMutable;


    private NewPopularRepository popRepo;
    private Context ApplicationContext;





    public void init(Context applicationContext){
        this.ApplicationContext = applicationContext;
        popRepo = NewPopularRepository.getInstance(applicationContext);
        this.popPostSetData = popRepo.getPopularModelMutable();
        this.popularActivityMutable = popRepo.getPopularActivityMutable();
    }

    public PopularModelPackage singleDataCheck(){
        return popPostSetData.getValue();
    }


    public void getMoreData(){
        popRepo.getMoreData();
    }

    public LiveData<PopularModelPackage> getPostData(){
        return popPostSetData;
    }


    public void removeItem(){
        popRepo.removeItem();
    }

    public void resetActivity(){
        popRepo.resetActivity();
    }



    public void itemVote(Long postId, String vote){
        popRepo.itemVote(postId, vote);
    }

    public void userAltered(User user){
        popRepo.userAltered(user);
    }







}

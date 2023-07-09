package com.daribear.prefy.Popular.NewPopularSystem;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daribear.prefy.Popular.PopularActivity;
import com.daribear.prefy.Popular.PopularViewModel.PopularModelPackage;
import com.daribear.prefy.Profile.User;

import lombok.Getter;
import lombok.Setter;

public class NewPopularViewModel extends ViewModel {
    private MutableLiveData<PopularModelPackage> popPostSetData;

    @Getter
    private MutableLiveData<PopularActivity> popularActivityMutable;


    private NewPopularRepository popRepo;
    private Context ApplicationContext;





    public void init(Context applicationContext){
        this.ApplicationContext = applicationContext;
        popRepo = NewPopularRepository.getInstance(applicationContext);
        System.out.println("Sdad popRepo original:" + popRepo);
        this.popPostSetData = popRepo.getPopularModelMutable();
        this.popularActivityMutable = popRepo.getPopularActivityMutable();
    }

    public PopularModelPackage singleDataCheck(){
        return popPostSetData.getValue();
    }

    public void printData(){
        PopularModelPackage popularModelPackage = singleDataCheck();
        for (int i = 0; i< popularModelPackage.getPopularPostSet().getPostList().size(); i++){
            System.out.println("SdadT :" + popularModelPackage.getPopularPostSet().getPostList().get(i).getQuestion());
        }
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

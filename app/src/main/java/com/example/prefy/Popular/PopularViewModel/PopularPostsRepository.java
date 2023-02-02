package com.example.prefy.Popular.PopularViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.prefy.Popular.PopularActivity;
import com.example.prefy.Popular.PopularPost;
import com.example.prefy.Popular.PopularPostSet;
import com.example.prefy.customClasses.StandardPost;

import java.util.ArrayList;

import lombok.Getter;


public class PopularPostsRepository implements RetreivePopularDataInterface, CachePopularDataRetreiverInterface, PopularActivityRetrieverInterface {
    private static PopularPostsRepository instance;
    private MutableLiveData<PopularPostSet> popularSetMutable;
    private MutableLiveData<PopularActivity> popularActivityMutable;
    private PopularPostSet popularPostSet;
    @Getter private Integer dataStatus = 0;
    private Context applicationContext;
    @Getter private Boolean dataLoading;
    @Getter private String dataType;

    public PopularPostsRepository(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static PopularPostsRepository getInstance(Context applicationContext){

        if (instance == null){
            instance = new PopularPostsRepository(applicationContext);
        }
        return instance;
    }

    public static Boolean isInstanceNull(){
        return (instance == null);
    }

    public MutableLiveData<PopularPostSet> getPopularSetMutable() {
        return popularSetMutable;
    }



    public MutableLiveData<PopularPostSet> getIntitialData(){
        if (popularPostSet == null) {
            CachePopularDataExecutor cacheExecutor = new CachePopularDataExecutor(applicationContext, this);
            cacheExecutor.init();
            WebPopularDataExecutor executor = new WebPopularDataExecutor(this::taskComplete, 0, false);
            executor.initExecutor();
            updateActivity();
            dataLoading = true;
            popularSetMutable = new MutableLiveData<>();
            popularSetMutable.setValue(popularPostSet);
            dataStatus = 1;
            dataType = "none";
        }
        return popularSetMutable;
    }




    public void getMoreData(){
        if (popularPostSet.getPostList() != null ){
            if (popularPostSet.getPostList().size() > 0) {
                Double lastCreation = (popularPostSet.getPostList().get(popularPostSet.getPostList().size() - 1).getCreationDate());
                //TODO change pageNumber
                WebPopularDataExecutor executor = new WebPopularDataExecutor(this::taskComplete, 0, true);
                executor.initExecutor();
                updateActivity();
            }
        }

    }

    public PopularPostSet singleDataCheck(){
        return popularSetMutable.getValue();
    }

    private void updateActivity(){
        PopularActivityRetreiver retreiver = new PopularActivityRetreiver(this::taskCompleted, applicationContext);
        retreiver.init();
    }

    public MutableLiveData<PopularActivity> getActivityCount() {
        if (popularActivityMutable == null){
            popularActivityMutable = new MutableLiveData<>();
        }
        return popularActivityMutable;
    }

    public void reset(){
        instance = null;
    }

    @Override
    public void taskComplete(Boolean successful, PopularPostSet newPopularPostSet, Boolean update) {
        dataType = "";

        if (successful){
            System.out.println("Sdad update:" + update + dataStatus);
            if (!update) {
                if (dataStatus < 2) {
                    popularPostSet = newPopularPostSet;
                    popularSetMutable.postValue(popularPostSet);
                    dataType = "noCache";
                } else {
                    dataType = "override";
                    Boolean dataTypeChanged = false;
                    for (int i = 0; i < newPopularPostSet.getPostList().size(); i ++){
                        System.out.println("Sdad popChecker in Repository postSize: " + newPopularPostSet.getPostList().size() + " userlistSize: " + newPopularPostSet.getUserList().size());
                        if (!popularPostSet.getPostList().contains(newPopularPostSet.getPostList().get(i))){
                            if ((popularPostSet.getPostList().size() - 1) > i) {
                                popularPostSet.getPostList().add(i, newPopularPostSet.getPostList().get(i));
                                popularPostSet.getUserList().add(i, newPopularPostSet.getUserList().get(i));
                            } else {
                                popularPostSet.getPostList().add(newPopularPostSet.getPostList().get(i));
                                popularPostSet.getUserList().add(newPopularPostSet.getUserList().get(i));
                            }
                            if (!dataTypeChanged){
                                dataType += " + NewItem";
                                dataTypeChanged = true;
                            }
                        }
                    }
                    Integer countSize;
                    if (newPopularPostSet.getPostList().size() > popularPostSet.getPostList().size()){
                        countSize = popularPostSet.getPostList().size();
                    } else {
                        countSize = newPopularPostSet.getPostList().size();
                    }
                    Boolean dataTypeChangedAgain = false;
                    for (int i = 0; i < countSize; i ++){
                        if (!newPopularPostSet.getPostList().contains(popularPostSet.getPostList().get(i))){
                            popularPostSet.getPostList().remove(i);
                            popularPostSet.getUserList().remove(i);
                            if (!dataTypeChangedAgain){
                                if (dataTypeChanged){
                                    dataType += " + itemRemove";
                                } else {
                                    dataType += " + itemRemove";
                                }
                                dataTypeChangedAgain = true;
                            }
                        }
                    }
                    if (!dataTypeChanged && !dataTypeChangedAgain){
                        dataType += " + NothingChanged";
                    }
                    popularSetMutable.postValue(popularPostSet);
                }
                dataStatus = 3;
            } else {
                Integer countSize;
                if (newPopularPostSet.getPostList().size() > popularPostSet.getPostList().size()){
                    countSize = popularPostSet.getPostList().size();
                } else {
                    countSize = newPopularPostSet.getPostList().size();
                }
                for (int i = 0; i < countSize; i++){
                    System.out.println("Sdad size Checker post: " + newPopularPostSet.getPostList().size() + " user: " + newPopularPostSet.getUserList().size());
                    StandardPost temporaryPost = newPopularPostSet.getPostList().get(i);
                    if (!popularPostSet.getPostList().contains(temporaryPost)) {
                        if (popularPostSet.getPostList().size() > 0){
                            if (temporaryPost.getCreationDate() > popularPostSet.getPostList().get(0).getCreationDate()){
                                popularPostSet.getPostList().add(0,newPopularPostSet.getPostList().get(i));
                                popularPostSet.getUserList().add(0, newPopularPostSet.getUserList().get(i));
                            } else {
                                popularPostSet.getPostList().add(newPopularPostSet.getPostList().get(i));
                                popularPostSet.getUserList().add(newPopularPostSet.getUserList().get(i));
                            }
                        } else {
                            popularPostSet.getPostList().add(newPopularPostSet.getPostList().get(i));
                            popularPostSet.getUserList().add(newPopularPostSet.getUserList().get(i));
                        }

                    }
                }
                dataType = "update";
                popularSetMutable.postValue(popularPostSet);
            }
            dataLoading = false;
        }
    }

    @Override
    public void completed(Boolean successful, PopularPostSet newPopularPostSet) {
        if (dataStatus < 3){
            popularPostSet = newPopularPostSet;
            if (popularSetMutable == null){
                popularSetMutable = new MutableLiveData<>();
            }
            popularSetMutable.postValue(popularPostSet);
            dataStatus = 2;
            dataType = "cache";
        }
    }


    @Override
    public void taskCompleted(Boolean successful, PopularActivity popularActivity) {
        if (successful){

            popularActivityMutable.postValue(popularActivity);
        }
    }
}

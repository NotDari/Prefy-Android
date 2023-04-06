package com.example.prefy.Popular.PopularViewModel.UpdatedPopularViewModel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.prefy.Popular.PopularActivity;
import com.example.prefy.Popular.PopularPost;
import com.example.prefy.Popular.PopularPostSet;
import com.example.prefy.Popular.PopularViewModel.CachePopularDataExecutor;
import com.example.prefy.Popular.PopularViewModel.CachePopularDataRetreiverInterface;
import com.example.prefy.Popular.PopularViewModel.PopularModelPackage;
import com.example.prefy.Popular.PopularViewModel.RetreivePopularDataInterface;
import com.example.prefy.Popular.PopularViewModel.WebPopularDataExecutor;
import com.example.prefy.customClasses.StandardPost;

import lombok.Getter;

public class PopularPostsRepository2 implements CachePopularDataRetreiverInterface, RetreivePopularDataInterface2 {

    private static PopularPostsRepository2 instance;
    @Getter
    private MutableLiveData<PopularModelPackage> popularModelMutable;

    private MutableLiveData<PopularActivity> popularActivityMutable;

    private Boolean dataLoading, activityLoading, dataRefresh, initData = false;
    private Context applicationContext;

    private Integer viewPagerPosition;


    public PopularPostsRepository2(Context applicationContext) {
        this.applicationContext = applicationContext;
        getInitialData();
    }

    public static PopularPostsRepository2 getInstance(Context applicationContext){
        if (instance == null){
            instance = new PopularPostsRepository2(applicationContext);
        }
        return instance;
    }

    public MutableLiveData<PopularModelPackage> getInitialData(){
        if (popularModelMutable == null){
            viewPagerPosition = 0;
            popularModelMutable = new MutableLiveData<>();
            popularActivityMutable = new MutableLiveData<>();
            dataLoading = false;
            CachePopularDataExecutor cacheExecutor = new CachePopularDataExecutor(applicationContext, this);
            cacheExecutor.init();

        }
        return popularModelMutable;
    }

    public void setViewPagerPosition(Integer position){
        this.viewPagerPosition = position;
        checkUpdate();
    }

    private void checkUpdate(){
        if (initData) {
            if (popularModelMutable.getValue().getPopularPostSet().getPostList().size() > 1) {
                if (viewPagerPosition >= popularModelMutable.getValue().getPopularPostSet().getPostList().size() - 2) {
                    updateData();
                }
            }
        }
    }



    @Override
    public void completed(Boolean successful, PopularPostSet popularPostSet) {
        if (successful && popularPostSet.getPostList().size() > 0){
            PopularModelPackage popularModelPackage = new PopularModelPackage();
            popularModelPackage.setPopularPostSet(popularPostSet);
            popularModelPackage.setRetrievalType("Cache");
            popularModelMutable.postValue(popularModelPackage);

        }else {
            PopularModelPackage popularModelPackage = new PopularModelPackage();
            popularModelPackage.setRetrievalType("NoCache");
            popularModelMutable.postValue(popularModelPackage);
        }
        WebPopularDataExecutor2 webPopularDataExecutor = new WebPopularDataExecutor2(this, (double) System.currentTimeMillis(), "Override");
        webPopularDataExecutor.initExecutor();
        dataLoading = true;
    }
    // if (popularPostSet.getPostList().size() > 0){
    //            lastPopularDate = popularPostSet.getPostList().get(popularPostSet.getPostList().size() - 1).getPopularDate();
    //        }

    public void refreshData(){
        WebPopularDataExecutor2 webPopularDataExecutor = new WebPopularDataExecutor2(this,(double) System.currentTimeMillis(), "Refresh");
        System.out.println("Sdad SystemTime:" + System.currentTimeMillis());
        webPopularDataExecutor.initExecutor();
        dataLoading = true;
    }


    public PopularModelPackage singleDataCheck(){
        return popularModelMutable.getValue();
    }

    @Override
    public void taskComplete(Boolean successful, PopularPostSet popularPostSet, String type) {
        if (!initData){
            initData = true;
        }
        switch (type){
            case "Override":
                overrideCache(popularPostSet);
                dataLoading = false;
                break;
            case "Refresh":
                setRefresh(popularPostSet);
                dataLoading = false;
                break;
            case "Update":
                initUpdate(popularPostSet);
                dataLoading = false;
                break;
            default:
                dataLoading = false;
                break;
        }
    }

    public void updateData(){
        Double lastPopularDate = popularModelMutable.getValue().getPopularPostSet().getPostList().get(popularModelMutable.getValue().getPopularPostSet().getPostList().size() - 1).getPopularDate();
        WebPopularDataExecutor2 webPopularDataExecutor = new WebPopularDataExecutor2(this,lastPopularDate, "Update");
        webPopularDataExecutor.initExecutor();
        dataLoading = true;
    }

    public void initUpdate(PopularPostSet popularPostSet){
        PopularModelPackage popularModelPackage = popularModelMutable.getValue();
        if (popularPostSet.getPostList().size() > 0) {
            for (int i = 0; i < popularPostSet.getPostList().size(); i++) {
                popularModelPackage.getPopularPostSet().getPostList().add(popularPostSet.getPostList().get(i));
                popularModelPackage.getPopularPostSet().getUserList().add(popularPostSet.getUserList().get(i));
            }
            System.out.println("Sdad updateSize:" + popularModelPackage.getPopularPostSet().getPostList().size() + " " + popularModelPackage.getPopularPostSet().getUserList().size());
            popularModelPackage.setRetrievalType("Update");
            popularModelMutable.postValue(popularModelPackage);
        }
    }

    private void setRefresh(PopularPostSet popularPostSet){
        PopularModelPackage popularModelPackage = new PopularModelPackage();
        popularModelPackage.setPopularPostSet(popularPostSet);
        popularModelPackage.setRetrievalType("Refresh");
        popularModelMutable.postValue(popularModelPackage);
    }

    private void overrideCache(PopularPostSet popularPostSet){
        PopularModelPackage originalPopularModelPackage = popularModelMutable.getValue();
        PopularPostSet currentSet = originalPopularModelPackage.getPopularPostSet();
        if (!originalPopularModelPackage.getRetrievalType().equals("NoCache")){
            for (int i = 0; i < popularPostSet.getPostList().size(); i ++){
                PopularPost post = popularPostSet.getPostList().get(i);
                if (originalPopularModelPackage.getPopularPostSet().getPostList().contains(post)){
                    break;
                }
                if (currentSet.getPostList().get(currentSet.getPostList().size() - 1).getPopularDate() > post.getPopularDate()){
                    originalPopularModelPackage.getPopularPostSet().getPostList().add(post);
                    break;
                }
                for (int z = 0; z < originalPopularModelPackage.getPopularPostSet().getPostList().size(); z++){
                    if (originalPopularModelPackage.getPopularPostSet().getPostList().get(z).getPopularDate() < post.getPopularDate()){
                        originalPopularModelPackage.getPopularPostSet().getPostList().add(z, post);
                        break;
                    }
                }
            }
        }else{
            PopularModelPackage popularModelPackage = new PopularModelPackage();
            popularModelPackage.setPopularPostSet(popularPostSet);
            popularModelPackage.setRetrievalType("OverrideNoCache");
            popularModelMutable.postValue(popularModelPackage);
        }
    }
}

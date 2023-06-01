package com.daribear.prefy.Popular.NewPopularSystem;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.daribear.prefy.Database.DatabaseHelper;
import com.daribear.prefy.Popular.PopularActivity;
import com.daribear.prefy.Popular.PopularPostSet;
import com.daribear.prefy.Popular.PopularViewModel.PopularActivityRetreiver;
import com.daribear.prefy.Popular.PopularViewModel.PopularActivityRetrieverInterface;
import com.daribear.prefy.Popular.PopularViewModel.PopularModelPackage;
import com.daribear.prefy.Popular.PopularViewModel.RetreivePopularDataInterface;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class NewPopularRepository implements NewCachePopularDataRetreiverInterface, RetreivePopularDataInterface , PopularActivityRetrieverInterface {
    private static NewPopularRepository instance;

    @Getter
    private MutableLiveData<PopularModelPackage> popularModelMutable;

    @Getter
    @Setter
    private MutableLiveData<ArrayList<Long>> postIdVoteList;



    @Getter
    private MutableLiveData<PopularActivity> popularActivityMutable;

    private Context context;

    private Boolean dataLoading;

    public NewPopularRepository(Context appContext) {
        this.context = appContext;
        dataLoading = false;
        getInitData();
    }

    public static NewPopularRepository getInstance(Context appContext){
        if (instance == null){
            instance = new NewPopularRepository(appContext);
        }
        return instance;
    }

    public MutableLiveData<PopularModelPackage> getInitData(){
        if (popularModelMutable == null){
            popularModelMutable = new MutableLiveData<>();
            postIdVoteList = new MutableLiveData<>();
            popularActivityMutable = new MutableLiveData<>();

            postIdVoteList.setValue(new ArrayList<>());
            NewCachePopularDataExecutor cacheExecutor = new NewCachePopularDataExecutor(context, this);
            cacheExecutor.init();
            getPopularActivity();
            dataLoading = true;
        }
        return popularModelMutable;
    }

    public void removeItem(){
        PopularModelPackage popularModelPackage = getPopularModelMutable().getValue();
        postIdVoteList.getValue().add(popularModelPackage.getPopularPostSet().getPostList().get(0).getPostId().longValue());
        popularModelPackage.getPopularPostSet().getPostList().remove(0);
        popularModelPackage.getPopularPostSet().getUserList().remove(0);
        popularModelPackage.setRetrievalType("Delete");
        popularModelMutable.setValue(popularModelPackage);
        if (popularModelPackage.getPopularPostSet().getPostList().size() < 5){
            getMoreData();
        }
    }

    public void getPopularActivity(){
        PopularActivityRetreiver popularActivityRetreiver = new PopularActivityRetreiver(this);
        popularActivityRetreiver.init();
    }

    public void itemVote(Long postId, String vote){

    }

    public void getMoreData(){
        if (!dataLoading){
            ArrayList<Long> avoidList = new ArrayList<>();
            PopularPostSet popularPostSet = popularModelMutable.getValue().getPopularPostSet();
            for (int i = 0; i < popularPostSet.getPostList().size(); i++){
                avoidList.add(popularPostSet.getPostList().get(i).getPostId());
            }
            avoidList.addAll(postIdVoteList.getValue());

            WebDataRetriever webDataRetriever = new WebDataRetriever(this, avoidList, "Add");
            webDataRetriever.initExec();
            getPopularActivity();
            dataLoading = true;
        }
    }

    @Override
    public void completed(Boolean successful, PopularPostSet popularPostSet, ArrayList<Long> avoidList) {
        postIdVoteList.postValue(avoidList);
        ArrayList<Long> ignoreList = new ArrayList<>();
        if (successful) {
            for (int i = 0; i < popularPostSet.getPostList().size(); i++) {
                ignoreList.add(popularPostSet.getPostList().get(i).getPostId());
            }
            ignoreList.addAll(avoidList);
        }
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
        WebDataRetriever webDataRetriever = new WebDataRetriever(this, ignoreList, "Add");
        webDataRetriever.initExec();
        dataLoading = true;
    }

    @Override
    public void taskComplete(Boolean successful, PopularPostSet popularPostSet, String type) {
        dataLoading = false;
        PopularModelPackage popularModelPackage = popularModelMutable.getValue();
        if (popularModelPackage == null){
            popularModelPackage = new PopularModelPackage();
        }
        if (popularModelPackage.getPopularPostSet() == null){
            popularModelPackage.setPopularPostSet(new PopularPostSet());
        }
        if (successful){
            PopularPostSet oldSet = popularModelPackage.getPopularPostSet();
            if (oldSet.getPostList() == null && oldSet.getUserList() == null){
                oldSet.setPostList(new ArrayList<>());
                oldSet.setUserList(new ArrayList<>());
            }
            checkVoteNotSubmitted checkVoteNotSubmitted = new checkVoteNotSubmitted();
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
            for (int i =0; i < popularPostSet.getPostList().size(); i ++){
                Long id = popularPostSet.getPostList().get(i).getPostId();
                if (!checkVoteNotSubmitted.exists(databaseHelper.getReadableDatabase(), id)) {
                    oldSet.getPostList().add(popularPostSet.getPostList().get(i));
                    oldSet.getUserList().add(popularPostSet.getUserList().get(i));
                }else {
                    postIdVoteList.getValue().add(id);
                }
            }
            popularModelPackage.setPopularPostSet(oldSet);
            popularModelPackage.setRetrievalType(type);
            popularModelMutable.postValue(popularModelPackage);
        }
        else {
            if (popularModelPackage.getPopularPostSet().getPostList() == null){
                popularModelPackage.getPopularPostSet().setPostList(new ArrayList<>());
            }
            if (popularModelPackage.getPopularPostSet().getUserList() == null){
                popularModelPackage.getPopularPostSet().setUserList(new ArrayList<>());
            }
            popularModelPackage.setRetrievalType("Failed");
            popularModelMutable.postValue(popularModelPackage);

        }
    }

    public void resetActivity(){
        PopularActivity popularActivity = new PopularActivity();
        popularActivity.setTotalActivities(0);
        popularActivity.setCommentsCount(0);
        popularActivity.setVotesCount(0);
        this.popularActivityMutable.setValue(popularActivity);
    }

    public static Boolean isInstanceNull(){
        return (instance == null);
    }

    public void reset(){
        instance = null;
    }


    @Override
    public void taskCompleted(Boolean successful, PopularActivity popularActivity) {
        if (successful){
            this.popularActivityMutable.postValue(popularActivity);
        }
    }
}

package com.daribear.prefy.Popular;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.daribear.prefy.Database.DatabaseHelper;
import com.daribear.prefy.Popular.PopularViewModel.PopularActivityRetreiver;
import com.daribear.prefy.Popular.PopularViewModel.PopularActivityRetrieverInterface;
import com.daribear.prefy.Popular.PopularViewModel.PopularModelPackage;
import com.daribear.prefy.Popular.PopularViewModel.RetreivePopularDataInterface;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.PopularPost;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * The repository which handles all the data details necessary for the popular pages.
 * It handles:
 * Fetching popular posts from the cache or web
 * Tracking votes and user interactions on posts
 * Maintains live data objects for UI observers
 * Updates popular activity statistics (votes, comments, total activity)
 * Handling addition and removal of posts in the popular feed
 *
 */
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

    /**
     * Sets up the livedata for the popular posts, if not yet created.
     * Gets the initial set of posts and associated details.
     * @return
     */
    public MutableLiveData<PopularModelPackage> getInitData(){
        if (popularModelMutable == null){
            popularModelMutable = new MutableLiveData<>();


            PopularModelPackage popularModelPackage = new PopularModelPackage();
            popularModelPackage.setRetrievalType("Loading");
            popularModelMutable.setValue(popularModelPackage);


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

    /**
     * Removes an item from the popularModelPackage, which contains the data of this repository.
     */
    public void removeItem(){
        PopularModelPackage popularModelPackage = getPopularModelMutable().getValue();
        if (popularModelPackage.getFullPostList().size() > 0) {
            postIdVoteList.getValue().add(popularModelPackage.getFullPostList().get(0).getStandardPost().getPostId());
            popularModelPackage.getFullPostList().get(0);
            popularModelPackage.setRetrievalType("Delete");
            popularModelMutable.setValue(popularModelPackage);
        }
    }

    /**
     * Calls the executor which gets the Popular Activity
     */
    public void getPopularActivity(){
        PopularActivityRetreiver popularActivityRetreiver = new PopularActivityRetreiver(this);
        popularActivityRetreiver.init();
    }

    /**
     * Called when the current user votes on a post.Changes the post within this repository's data to reflect that change
     * @param postId id of post to change
     * @param vote vote the user made.
     */
    public void itemVote(Long postId, String vote){
        if (popularModelMutable.getValue() != null) {
            Boolean changed = false;
            PopularModelPackage popularModelPackage = popularModelMutable.getValue();
            if (popularModelPackage != null) {
                ArrayList<FullPost> fullPostList =  popularModelPackage.getFullPostList();
                for (int i = 0; i < fullPostList.size(); i++) {
                    if (fullPostList.get(i).getStandardPost().getPostId().equals(postId)) {
                        changed = true;
                        fullPostList.get(i).getStandardPost().setCurrentVote(vote);

                    }
                }
                if (changed) {
                    popularModelMutable.setValue(popularModelPackage);
                }
            }
        }
    }

    /**
     * Called when a user is altered in one of the posts, and thus this repository's data has to be changed aswell.
     * @param user the new user, that has been changed.
     */
    public void userAltered(User user){
        if (popularModelMutable.getValue() != null) {
            Boolean changed = false;
            PopularModelPackage popularModelPackage = popularModelMutable.getValue();
            if (popularModelPackage != null) {
                if (popularModelPackage.getFullPostList() != null) {
                    ArrayList<FullPost> fullPostList = popularModelPackage.getFullPostList();
                    for (int i = 0; i < fullPostList.size(); i++) {
                        if (fullPostList.get(i).getUser().getId().equals(user.getId())) {
                            fullPostList.get(i).setUser(user);
                            changed = true;
                        }
                    }
                    if (changed) {
                        popularModelMutable.setValue(popularModelPackage);
                    }
                }
            }
        }
    }

    /**
     * Called when more popular posts are required.
     * Gets more popular posts and adds them to the data list.
     */
    public void getMoreData(){
        if (!dataLoading){
            ArrayList<Long> avoidList = new ArrayList<>();
            ArrayList<FullPost> fullPostList = popularModelMutable.getValue().getFullPostList();
            for (int i = 0; i < fullPostList.size(); i++){
                avoidList.add(fullPostList.get(i).getStandardPost().getPostId());
            }
            if (postIdVoteList.getValue() != null) {
                avoidList.addAll(postIdVoteList.getValue());
            }

            WebDataRetriever webDataRetriever = new WebDataRetriever(this, avoidList, "Add");
            webDataRetriever.initExec();
            getPopularActivity();
            dataLoading = true;
        }
    }

    /**
     * Resets all the activities in this repository
     */
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


    /**
     * Delected for when retrieving popularActivity is complete
     * @param successful whether it succesfully retrieved the data
     * @param popularActivity the popularActivity retrieved
     */
    @Override
    public void taskCompleted(Boolean successful, PopularActivity popularActivity) {
        if (successful){
            this.popularActivityMutable.postValue(popularActivity);
        }
    }

    /**
     * Called when retrieving data is complete.
     * @param successful whether it was successful
     * @param fullPostList the list of posts retrieved
     * @param type the type retrieved
     */
    @Override
    public void taskComplete(Boolean successful, ArrayList<FullPost> fullPostList, String type) {
        dataLoading = false;
        PopularModelPackage popularModelPackage = popularModelMutable.getValue();
        if (successful){
            ArrayList<FullPost> oldList = popularModelPackage.getFullPostList();
            if (oldList == null){
                oldList = new ArrayList<>();
            }
            checkVoteNotSubmitted checkVoteNotSubmitted = new checkVoteNotSubmitted();
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
            for (int i =0; i < fullPostList.size(); i ++){
                Long id = fullPostList.get(i).getStandardPost().getPostId();
                if (!checkVoteNotSubmitted.exists(databaseHelper.getReadableDatabase(), id)) {
                   oldList.add(fullPostList.get(i));
                }else {
                    postIdVoteList.getValue().add(id);
                }
            }
            popularModelPackage.setFullPostList(oldList);
            popularModelPackage.setRetrievalType(type);
            popularModelMutable.postValue(popularModelPackage);
        }
        else {
            if (popularModelPackage.getFullPostList() == null){
                popularModelPackage.setFullPostList(new ArrayList<>());
            }
            popularModelPackage.setRetrievalType("Failed");
            popularModelMutable.postValue(popularModelPackage);

        }
    }

    /**
     * Called when retrieving data is complete.
     * @param successful whether it was successful
     * @param postList list of posts retrieved
     * @param avoidList lists of posts to avoid, as they have already been retrieved/seen
     */
    @Override
    public void completed(Boolean successful, ArrayList<FullPost> postList, ArrayList<Long> avoidList) {
        postIdVoteList.postValue(avoidList);
        ArrayList<Long> ignoreList = new ArrayList<>();
        if (successful) {
            for (int i = 0; i < postList.size(); i++) {
                ignoreList.add(postList.get(i).getStandardPost().getPostId());
            }
            ignoreList.addAll(avoidList);
        }
        if (successful && postList.size() > 0){
            PopularModelPackage popularModelPackage = new PopularModelPackage();
            popularModelPackage.setFullPostList(postList);
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
}
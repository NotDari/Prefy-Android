package com.daribear.prefy.Search;

import androidx.lifecycle.MutableLiveData;

import com.daribear.prefy.Ads.AdTracker;
import com.daribear.prefy.Profile.User;

import java.util.ArrayList;
import java.util.Objects;

public class SearchRepository implements SearchUsersTopDelegate, SearchUsersStringDelegate{
    private static SearchRepository instance;
    private MutableLiveData<ArrayList<User>> searchlistMutable;
    private MutableLiveData<Boolean> internetAvailable;
    private String currentSearch;
    private Integer pageNumber;
    private Boolean dataLoading;


    public SearchRepository(){
        currentSearch = "";
        if (internetAvailable == null){
            internetAvailable = new MutableLiveData<>();
            getData();
        }
    }

    public static SearchRepository getInstance(){
        if (instance == null){
            instance = new SearchRepository();
        }
        return instance;
    }

    public static Boolean isInstanceNull(){
        return (instance == null);
    }

    public MutableLiveData<Boolean> getInternetAvailable() {
        if (internetAvailable == null){
            internetAvailable = new MutableLiveData<>();
        }
        return internetAvailable;
    }

    public MutableLiveData<ArrayList<User>> getSearchlistMutable() {
        if (searchlistMutable == null){
            searchlistMutable = new MutableLiveData<>();
        }
        return searchlistMutable;
    }

    public void getData(){
        pageNumber = 0;
        if (currentSearch.isEmpty()){
            SearchTopUsersRetriever topExecutor = new SearchTopUsersRetriever(pageNumber, 15, this);
            topExecutor.initExecutor();
        } else {
            SearchDataRetriever dataExecutor = new SearchDataRetriever(currentSearch, pageNumber, this, 15);
            dataExecutor.initExecutor();
        }
        dataLoading = true;
    }

    public String getCurrentSearch() {
        return currentSearch;
    }

    public void setCurrentSearch(String currentSearch) {
        pageNumber = 0;
        if (!this.currentSearch.equals(currentSearch)) {
            this.currentSearch = currentSearch;
            getData();
        }
    }

    public void reset(){
        instance = null;
    }

    public void viewScrolled(){
        if (currentSearch != null){
            if (!dataLoading) {
                pageNumber += 1;
                ArrayList<User> searchList = searchlistMutable.getValue();
                if (currentSearch.isEmpty()) {
                    if (!searchList.isEmpty()) {
                        SearchTopUsersRetriever executor = new SearchTopUsersRetriever(pageNumber, 15, this);
                        executor.initExecutor();
                    }
                } else {
                    if (!searchList.isEmpty()) {
                        SearchDataRetriever executor = new SearchDataRetriever(currentSearch, pageNumber, this, 15);
                        executor.initExecutor();
                    }
                }
                dataLoading = true;
            }
        }
    }

    public void userAltered(User user){
        ArrayList<User> userList = searchlistMutable.getValue();
        Boolean changed = false;
        if (userList != null) {
            for (int i = 0; i < userList.size(); i++) {
                if (Objects.equals(userList.get(i).getId(), user.getId())) {
                    changed = true;
                    userList.set(i, user);
                }
            }
            if (changed) {
                searchlistMutable.setValue(userList);
            }
        }
    }

    public void setFollowing(Long userId, Boolean following){
        ArrayList<User> userList = searchlistMutable.getValue();
        Boolean changed = false;
        if (userList != null) {
            for (int i = 0; i < userList.size(); i++) {
                if (Objects.equals(userList.get(i).getId(), userId)) {
                    System.out.println("SdadAPP altered, userId:" + userId + " , following:" + following);
                    changed = true;
                    userList.get(i).setFollowing(following);
                }
            }
            if (changed) {
                searchlistMutable.setValue(userList);
            }
        }

    }


    @Override
    public void stringCompleted(Boolean successful, Boolean update, String text, ArrayList<User> searchUserArrayList) {
        if (successful) {
            internetAvailable.postValue(true);
            if (!update) {
                if (text != null) {
                    if (text.equals(currentSearch)) {
                        searchlistMutable.postValue(searchUserArrayList);
                    }
                }
            } else {
                if (text.equals(currentSearch)) {
                    ArrayList<User> currentArrayList = searchlistMutable.getValue();
                    for (int i = 0; i < searchUserArrayList.size(); i++) {
                        currentArrayList.add(searchUserArrayList.get(i));
                    }
                    searchlistMutable.postValue(currentArrayList);
                    System.out.println("Sdad list size:" + searchlistMutable.getValue().size());
                }
            }
            AdTracker.getInstance().otherViewed();
        } else {
            if (internetAvailable == null){
                internetAvailable = new MutableLiveData<>();
            }
            internetAvailable.postValue(false);
        }
        dataLoading = false;
    }

    @Override
    public void topCompleted(Boolean successful, Boolean update, ArrayList<User> searchUserArrayList) {
        if (successful){
            internetAvailable.postValue(true);
            if (!update) {
                if (currentSearch.isEmpty()) {
                    searchlistMutable.postValue(searchUserArrayList);
                }
            } else {
                if (currentSearch.isEmpty()) {
                    ArrayList<User> currentArrayList = searchlistMutable.getValue();
                    for (int i = 0; i < searchUserArrayList.size(); i++) {

                        currentArrayList.add(searchUserArrayList.get(i));
                    }
                    searchlistMutable.postValue(currentArrayList);
                }
            }
            AdTracker.getInstance().otherViewed();
        } else {
            if (internetAvailable == null){
                internetAvailable = new MutableLiveData<>();
            }
            internetAvailable.postValue(false);
        }
        dataLoading = false;

    }
}

package com.daribear.prefy.Search;

import androidx.lifecycle.MutableLiveData;

import com.daribear.prefy.Profile.User;

import java.util.ArrayList;

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
        }
        getData();
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
        this.currentSearch = currentSearch;
        getData();
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
        } else {
            if (internetAvailable == null){
                internetAvailable = new MutableLiveData<>();
            }
            internetAvailable.postValue(false);
        }
        dataLoading = false;
    }
}

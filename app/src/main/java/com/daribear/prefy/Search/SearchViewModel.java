package com.daribear.prefy.Search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daribear.prefy.Profile.User;

import java.util.ArrayList;

public class SearchViewModel extends ViewModel {
    private SearchRepository searchRepo;
    private MutableLiveData<ArrayList<User>> searchlistMutable;
    private MutableLiveData<Boolean> internetAvailable;

    public void init(){
        searchRepo = SearchRepository.getInstance();
        this.searchlistMutable = searchRepo.getSearchlistMutable();
        this.internetAvailable = searchRepo.getInternetAvailable();
    }

    public MutableLiveData<ArrayList<User>> getSearchlistMutable() {
        return searchlistMutable;
    }

    public LiveData<Boolean> getInternetAvailable(){
        return internetAvailable;
    }

    public void setCurrentSearch(String currentSearch){
        searchRepo.setCurrentSearch(currentSearch);
    }

    public String getCurrentSearch(){
        return searchRepo.getCurrentSearch();
    }

    public void viewScrolled(){
        searchRepo.viewScrolled();
    }

}

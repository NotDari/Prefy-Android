package com.example.prefy.Search;

import android.app.Activity;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.prefy.Profile.User;
import com.example.prefy.R;
import com.example.prefy.Utils.NoInternetDropDown;

import java.util.ArrayList;


public class SearchFragment extends Fragment {
    private ImageView backButton;
    private ProgressBar progressBar;
    private SearchGateway searchGateway;
    private EditText searchEditText;
    private Boolean viewDestroyed,textSearchActive, firstTopDone, dataRefreshing, fetchingData;
    private String activeText;
    private ArrayList<User> topList;
    private TextView noResultsText, noInternetText;
    private RecyclerView recView;

    private SearchViewModel searchViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        getViews(view);
        setUp(view);
        return view;
    }


    private void getViews(View view){
        viewDestroyed = false;
        backButton= view.findViewById(R.id.SearchTopBarBack);
        progressBar = view.findViewById(R.id.SearchProgressbar);
        searchEditText = view.findViewById(R.id.SearchEditText);
        noResultsText = view.findViewById(R.id.SearchNoResultsText);
        noInternetText = view.findViewById(R.id.SearchNoInternet);
        recView = view.findViewById(R.id.SearchRecyclerView);
    }

    private void setUp(View view){
        resizeSearch(view);
        initEmptyRecView(view);
        initBackButton();
        getData();
        //getInitialData(view);
        initEditTextListener();
    }

    private void resizeSearch(View view){
        EditText searchResize = view.findViewById(R.id.SearchEditText);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        Integer newheight = (int)((int)(displayMetrics.heightPixels * 0.047) * .95);
        if (searchResize.getLayoutParams().height < newheight){
            searchResize.getLayoutParams().height = newheight;
        }



    }

    private void getData(){
        searchViewModel = new SearchViewModel();
        searchViewModel.init();
        searchEditText.setText(searchViewModel.getCurrentSearch());
        fetchingData = false;
        viewDestroyed = false;
        dataRefreshing = false;
        searchViewModel.getSearchlistMutable().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> userArrayList) {
                if (userArrayList != null){
                    recView.setVisibility(View.VISIBLE);
                    noInternetText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    searchGateway.setInitialData(userArrayList);
                    if (!userArrayList.isEmpty()){
                        noResultsText.setVisibility(View.GONE);
                    }else{
                        noResultsText.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        searchViewModel.getInternetAvailable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null){
                    progressBar.setVisibility(View.GONE);
                    if (!aBoolean){
                        System.out.println("Sdad no Internet!");
                        noInternet();
                    } else {
                        internetBack();
                    }
                    fetchingData = false;
                    dataRefreshing = false;
                }
            }
        });
    }

    private void getInitialData(View view){
        Integer limit = Integer.parseInt(view.getContext().getString(R.string.Search_Load_Count));
        topList = new ArrayList<>();
        textSearchActive = false;
        firstTopDone = false;
        dataRefreshing = false;
        progressBar.setVisibility(View.VISIBLE);
        noInternetText.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);
        fetchingData = true;
       // SearchTopUsersRetriever topExecutor = new SearchTopUsersRetriever(null, 15, this, false);
        //topExecutor.initExecutor();
    }

    private void initBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                Navigation.findNavController(buttonView).navigateUp();
            }
        });
    }

    private void initEmptyRecView(View view){
        searchGateway = new SearchGateway(R.id.SearchRecyclerView, view, view.getContext(), getActivity());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        searchGateway.displayEmptyView(height);
        searchGateway.setType("Top");
    }

    private void setInitialData(ArrayList<User> searchUserArrayList){
        firstTopDone = true;
        searchGateway.setInitialData(searchUserArrayList);
        recView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);
    }



    private void initEditTextListener(){
        activeText = "";
        TextWatcher textWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                //searchViewModel.setCurrentSearch(s.toString());

                searchViewModel.setCurrentSearch(s.toString());

            }

        };
        searchEditText.addTextChangedListener(textWatcher);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /**
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        event == null ||
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    //User finihsed typing
                    searchViewModel.setCurrentSearch(searchEditText.getText().toString());
                    return true;
                }
                 */
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        viewDestroyed = true;
        super.onDestroyView();
    }

    private void initSearch(String text){
        this.activeText = text;
        noInternetText.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        searchGateway.resetData();
        searchGateway.setType("String");
        fetchingData = true;
        //SearchDataRetriever dataExecutor = new SearchDataRetriever(text, null, false,  this, 15);
        //dataExecutor.initExecutor();
    }

    private void setData(ArrayList<User> searchUserArrayList){
        recView.setVisibility(View.VISIBLE);
        if (searchUserArrayList.size() == 0){
            noResultsText.setVisibility(View.VISIBLE);
        }else{
            searchGateway.updateData(searchUserArrayList);
        }
        noInternetText.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void noInternet(){
        if (!viewDestroyed && !fetchingData) {
            fetchingData = true;
            recView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            noResultsText.setVisibility(View.GONE);
            noInternetText.setVisibility(View.VISIBLE);
            NoInternetDropDown.getInstance(getActivity()).showDropDown();
            noInternetText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!dataRefreshing) {
                        searchViewModel.setCurrentSearch(searchEditText.getText().toString());
                        progressBar.setVisibility(View.VISIBLE);
                        noInternetText.setVisibility(View.GONE);
                        dataRefreshing = true;
                    }
                }
            });
        }
    }

    private void internetBack(){
        recView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);
        noInternetText.setVisibility(View.GONE);
    }









}
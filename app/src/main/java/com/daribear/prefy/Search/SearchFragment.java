package com.daribear.prefy.Search;

import android.os.Bundle;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.NoInternetDropDown;

import java.util.ArrayList;

/**
 * Fragment that handles the user search screen.
 * Manages search input, displays results in a RecyclerView,
 * and handles "no results" or "no internet" states.
 */
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

    /**
     * Get list of views
     * @param view baseview
     */
    private void getViews(View view){
        viewDestroyed = false;
        backButton= view.findViewById(R.id.SearchTopBarBack);
        progressBar = view.findViewById(R.id.SearchProgressbar);
        searchEditText = view.findViewById(R.id.SearchEditText);
        noResultsText = view.findViewById(R.id.SearchNoResultsText);
        noInternetText = view.findViewById(R.id.SearchNoInternet);
        recView = view.findViewById(R.id.SearchRecyclerView);
    }

    /**
     * Sets up all the sub views
     * @param view baseview
     */
    private void setUp(View view){
        resizeSearch(view);
        initEmptyRecView(view);
        initBackButton();
        getData();
        //getInitialData(view);
        initEditTextListener();
    }

    /**
     * Resize the search, according to the screen size.
     * @param view baseview
     */
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

    /**
     * Get the search data, checking if its in progress or if there is no internet.
     * If there are no results, show specific text.
     */
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


    /**
     * Make the back button pop the stack.
     */
    private void initBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                Navigation.findNavController(buttonView).navigateUp();
            }
        });
    }

    /**
     * Create the empty recycle view
     * @param view
     */
    private void initEmptyRecView(View view){
        searchGateway = new SearchGateway(R.id.SearchRecyclerView, view, view.getContext(), getActivity());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        searchGateway.displayEmptyView(height);
        searchGateway.setType("Top");
    }


    /**
     * Create the edit text which listens for the user's search input.
     */
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
    }

    @Override
    public void onDestroyView() {
        viewDestroyed = true;
        super.onDestroyView();
    }

    /**
     * Shows the correct layout if there is no internet.
     */
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
                        searchViewModel.refreshData();
                        progressBar.setVisibility(View.VISIBLE);
                        noInternetText.setVisibility(View.GONE);
                        dataRefreshing = true;
                    }
                }
            });
        }
    }

    /**
     * Internet access has been restored, so hide no internet text.
     */
    private void internetBack(){
        recView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);
        noInternetText.setVisibility(View.GONE);
    }









}
package com.daribear.prefy.Report;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daribear.prefy.Comments.Comment;
import com.daribear.prefy.Network.UploadController.UploadController;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.CurrentTime;
import com.daribear.prefy.customClasses.Posts.StandardPost;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;


public class ReportFragment extends Fragment {
    private String type;
    private LinearLayout categoryLayout;
    private ArrayList<String> categoryList = new ArrayList<>(Arrays.asList("Spam","Abuse", "Sexually inappropriate", "Violent or prohibited content", "Scam or misleading", "Offensive"));
    private TextView titleIssue, reportSubmittedText;
    private Boolean reportSubmitted;
    private BottomNavigationView bottomNav;
    private ImageView doneButton;
    private StandardPost post;
    private Comment comment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        getViews(view);
        initCategories();
        return view;
    }

    private void getViews(View view){
        this.type = getArguments().getString("Type");
        if (type.equals("Post")) {
            this.post = getArguments().getParcelable("post");
        } else if (type.equals("Comment")){
            this.comment = getArguments().getParcelable("comment");
        }
        bottomNav = getActivity().findViewById(R.id.BottomNav);
        bottomNav.setVisibility(View.GONE);
        ImageView backButton = view.findViewById(R.id.ReportTopBarBack);
        reportSubmitted = false;
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(v);
            }
        });
        categoryLayout = view.findViewById(R.id.ReportCategoriesLinLay);
        titleIssue = view.findViewById(R.id.ReportIssueTextView);
        reportSubmittedText = view.findViewById(R.id.ReportSubmittedTextView);
        doneButton = view.findViewById(R.id.ReportTopBarDone);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
                bottomNav.setVisibility(View.VISIBLE);
            }
        });
    }


    private void initCategories(){
        categoryLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                if (categoryLayout.getViewTreeObserver().isAlive())
                    categoryLayout.getViewTreeObserver().removeOnPreDrawListener(this);

                Integer categoryWidth = categoryLayout.getMeasuredWidth();
                Integer categoryHeight = categoryLayout.getMeasuredHeight();
                createCategories(categoryWidth, categoryHeight);
                return true;
            }
        });
    }

    private void createCategories(Integer layWidth,Integer layHeight){
        for (int i = 0; i < categoryList.size(); i++) {
            Context context = ReportFragment.this.getContext();
            LinearLayout innerLay = new LinearLayout(context);
            innerLay.setOrientation(LinearLayout.VERTICAL);
            innerLay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            View topView = new View(context);
            if (i == 0){
                topView.setLayoutParams(new ViewGroup.LayoutParams(layWidth, getPixels(1F)));
            } else {
                topView.setLayoutParams(new ViewGroup.LayoutParams(layWidth, getPixels(.5F)));
            }

            topView.setBackgroundColor(context.getColor(R.color.grey));

            TextView categoryText = new TextView(context);
            categoryText.setText(categoryList.get(i));
            categoryText.setTypeface(null, Typeface.BOLD);
            ViewGroup.MarginLayoutParams mLP = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mLP.setMarginStart((int) (layWidth * 0.1));
            categoryText.setLayoutParams(mLP);
            categoryText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            categoryText.setGravity(View.TEXT_ALIGNMENT_CENTER);
            categoryText.setPadding(0, (int)(layHeight * .025), 0, (int)(layHeight * .025));
            categoryText.setTextColor(context.getColor(R.color.text_color));

            View bottomView = new View(context);
            if (i == categoryList.size() - 1){
                bottomView.setLayoutParams(new ViewGroup.LayoutParams(layWidth, getPixels(1F)));
            } else {
                bottomView.setLayoutParams(new ViewGroup.LayoutParams(layWidth, getPixels(.5F)));
            }
            bottomView.setBackgroundColor(context.getColor(R.color.grey));
            innerLay.addView(topView);
            innerLay.addView(categoryText);
            innerLay.addView(bottomView);
            innerLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initReportCompleted(categoryText.getText().toString());
                }
            });
            categoryLayout.addView(innerLay);
        }
    }


    private Integer getPixels(Float density){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (density * scale + 0.5f);
    }

    private void initReportCompleted(String reason){
        categoryLayout.setVisibility(View.GONE);
        Context context = categoryLayout.getContext();
        /**
        TextView textView = new TextView(categoryLayout.getContext());
        textView.setTextColor(context.getColor(R.color.text_color));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        Typeface poppins = getResources().getFont(R.font.poppins_medium);
        textView.setTypeface(poppins);
        textView.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setText("Your report has been submitted. \n Thank you for making Prefy better!");
        categoryLayout.addView(textView);
         */
        reportSubmittedText.setVisibility(View.VISIBLE);
        titleIssue.setVisibility(View.GONE);
        doneButton.setVisibility(View.VISIBLE);
        reportSubmitted = true;
        if (type.equals("Post")) {
            submitPostReport(reason);
        } else if (type.equals("Comment")){
            submitCommentReport(reason);
        }
    }

    private void submitPostReport(String reason){
        //TODO fix PostReport
        Report report = new Report();
        if (post != null) {
            report.setPostId(post.getPostId());
            report.setUserId(post.getUserId());
            report.setCommentId(null);
            report.setRepCategory(reason);
            report.setType(type);
            report.setCreationDate((double) CurrentTime.getCurrentTime());
        }
        UploadController.saveReport(getActivity().getApplicationContext(),report);
        /**
        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        HashMap<String, Object> reportMap = new HashMap<>();
        if (post != null) {
            reportMap.put("postId", post.getKey());
            reportMap.put("userUID", post.getUid());
        } else {
            reportMap.put("postId", null);
            reportMap.put("userUID", null);
        }
        reportMap.put("repCategory", reason);
        reportMap.put("active", true);
        reportMap.put("Type", type);
        Double currentDate = (double) System.currentTimeMillis();
        ff.collection("Reports").document(currentDate.toString()).set(reportMap);
         */
    }

    private void submitCommentReport(String reason){
        //TODO fix Commentreport
        Report report = new Report();
        if (comment != null) {
            report.setPostId(comment.getPostId());
            report.setUserId(comment.getUser().getId());
            report.setCommentId(comment.getCommentId());
            report.setRepCategory(reason);
            report.setType(type);
            report.setCreationDate((double) CurrentTime.getCurrentTime());
        }
        UploadController.saveReport(getActivity().getApplicationContext(),report);
        /**
        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        HashMap<String, Object> reportMap = new HashMap<>();
        if (comment != null) {
            reportMap.put("postId", comment.getPostId());
            reportMap.put("userUID", comment.getUid());
            reportMap.put("commentKey", comment.getKey());
        } else {
            reportMap.put("postId", null);
            reportMap.put("userUID", null);
            reportMap.put("commentKey", null);
        }
        reportMap.put("repCategory", reason);
        reportMap.put("active", true);
        reportMap.put("Type", type);
        Double currentDate = (double) System.currentTimeMillis();
        ff.collection("Reports").document(currentDate.toString()).set(reportMap);
         */
    }


    public void onBackPressed(View v){
        if (!reportSubmitted) {
            Navigation.findNavController(v).navigateUp();
            bottomNav.setVisibility(View.VISIBLE);
        } else {
            reportSubmittedText.setVisibility(View.GONE);
            titleIssue.setVisibility(View.VISIBLE);
            categoryLayout.setVisibility(View.VISIBLE);
            reportSubmitted = false;
            doneButton.setVisibility(View.GONE);
        }
    }


}
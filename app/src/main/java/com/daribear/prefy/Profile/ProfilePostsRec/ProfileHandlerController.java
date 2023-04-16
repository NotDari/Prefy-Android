package com.daribear.prefy.Profile.ProfilePostsRec;

/**
public class ProfileHandlerController implements ProfileHandlerInt {
    private View view;
    private String uid;
    private Boolean viewDestroyed;
    private String username, profileImageLink;
    private ProfilePostsGateway gateway;
    private TextView postCounter, voteCounter, prefCounter, profileBio;
    private ImageButton twitterButton, instagramButton, vkButton;

    public ProfileHandlerController(View view, String uid, String username, String profileImageLink) {
        this.view = view;
        this.uid = uid;
        this.username = username;
        this.profileImageLink = profileImageLink;
    }

    private void getUserInfoViews(){
        postCounter = view.findViewById(R.id.ProfilePostsNumber);
        voteCounter = view.findViewById(R.id.ProfileVotesNumber);
        prefCounter = view.findViewById(R.id.ProfilePreferencesNumber);
        profileBio = view.findViewById(R.id.ProfileBio);
    }

    public void initControllerSystem(){
        viewDestroyed = false;
        RecyclerView recView = view.findViewById(R.id.ProfileFragmentPostsRecView);
        ProgressBar progressBar = view.findViewById(R.id.ProfileFragmentProgress);
        RelativeLayout noPostsLay = view.findViewById(R.id.ProfileFragmentNoPosts);
        progressBar.setVisibility(View.VISIBLE);
        recView.setVisibility(View.GONE);
        noPostsLay.setVisibility(View.GONE);
        getUserInfoViews();
        Integer limit = Integer.parseInt(view.getContext().getString(R.string.Search_Load_Count));
        ProfileExecutor executor = new ProfileExecutor(uid, this, false, limit, null, false);
        executor.initExecutor();
    }

    public void viewDestroyed(){
        viewDestroyed = true;
        if (gateway != null) {
            gateway.destroyView();
        }
        this.view = null;

    }
    private void inituserInfo(userInfo userInfo){
        TextView ProfileBio = view.findViewById(R.id.ProfileBio);
        ProfileBio.setVisibility(View.VISIBLE);
        ProfileBio.setText(userInfo.getBio());
    }

    private void setUpPosts(WholeProfile wholeProfile){
        gateway = new ProfilePostsGateway(wholeProfile.getPostList(), R.id.ProfileFragmentPostsRecView, view, view.getContext(), username, profileImageLink, true);
        gateway.displayView();
        RecyclerView recView = view.findViewById(R.id.ProfileFragmentPostsRecView);
        ProgressBar progressBar = view.findViewById(R.id.ProfileFragmentProgress);
        progressBar.setVisibility(View.GONE);
        recView.setVisibility(View.VISIBLE);
    }

    private void setUpInfo(WholeProfile wholeProfile){
        if ((wholeProfile.getUserInfo().getBio() != null) && (!wholeProfile.getUserInfo().getBio().isEmpty())){
            profileBio.setVisibility(View.VISIBLE);
            profileBio.setText(wholeProfile.getUserInfo().getBio());
        } else {
            profileBio.setVisibility(View.GONE);
        }

        postCounter.setText(wholeProfile.getUserInfo().getPostsNumber().toString());
        voteCounter.setText(wholeProfile.getUserInfo().getVotesNumber().toString());
        prefCounter.setText(wholeProfile.getUserInfo().getPrefsNumber().toString());
        twitterButton = view.findViewById(R.id.ProfileTwitterButton);
        vkButton = view.findViewById(R.id.ProfileVKButton);
        instagramButton = view.findViewById(R.id.ProfileInstagramButton);
        if (wholeProfile.getUserInfo().getTwitter() != null && !wholeProfile.getUserInfo().getTwitter().isEmpty()){
            twitterButton.setImageDrawable(view.getContext().getDrawable(R.drawable.twitter));
        }
        if (wholeProfile.getUserInfo().getInstagram() != null && !wholeProfile.getUserInfo().getInstagram().isEmpty()){
            instagramButton.setImageDrawable(view.getContext().getDrawable(R.drawable.instagram));
        }
        if (wholeProfile.getUserInfo().getVk() != null && !wholeProfile.getUserInfo().getVk().isEmpty()){
            vkButton.setImageDrawable(view.getContext().getDrawable(R.drawable.vk));
        }
    }


    @Override
    public void taskDone(Boolean successful, WholeProfile wholeProfile) {
        if (!viewDestroyed){
            if (successful){
                setUpPosts(wholeProfile);
                setUpInfo(wholeProfile);
                inituserInfo(wholeProfile.getUserInfo());
            } else{

            }
        }
    }


}
*/
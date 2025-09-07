package com.daribear.prefy.Settings.DetailsEdit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daribear.prefy.R;
import com.daribear.prefy.SubmitPost.imageCompresser;
import com.daribear.prefy.Utils.NoInternetDropDown;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.Utils.SharedPreferences.Utils;
import com.daribear.prefy.Utils.GeneralUtils.usernameValidityChecker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The details edit fragment which allows the user to edit one of a user's details.
 * Receives which detail to alter from arguments, and what the current value is based on what is stored.
 */
public class DetailsEditFragment extends Fragment {
    private ImageView backButton, saveButton;
    private TextView titleText;
    private String defaultValue;
    private RelativeLayout relLay;
    private Drawable currentDrawable;
    private EditText editText;
    private ProfileImageSelector profileImageSelector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        profileImageSelector = new ProfileImageSelector();
        profileImageSelector.registerForActivityResult(getActivity(), DetailsEditFragment.this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_edit, container, false);
        getViews(view);
        getDetail(view);
        return view;
    }


    private void getViews(View view){
        this.backButton = view.findViewById(R.id.DetailsEditTopBarBack);
        this.saveButton = view.findViewById(R.id.DetailsEditTopBarSave);
        this.titleText = view.findViewById(R.id.DetailsEditTopBarTitle);
        relLay = view.findViewById(R.id.DetailsEditRelLay);
        initBackButton();
    }

    private void initBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
    }
    private void getDetail(View view){
        String detail = this.getArguments().getString("detail");
        getValue(view,detail);
    }

    /**
     * Retrieve which value to allow the user to edit and calls the correct function to allow the clicking and saving.
     * Changes the drawable based on which was selected.
     * @param view the baseview to find other views from
     * @param detail which value to edit
     */
    private void getValue(View view, String detail){
        Context appContext = view.getContext().getApplicationContext();
        Utils utils = new Utils(appContext);
        saveButton.setVisibility(View.GONE);
        switch (detail){
            case "Profile Image":
                this.defaultValue = utils.loadString(getString(R.string.save_profileP_pref), "");
                createImage(view);
                break;
            case "Username":
                this.defaultValue = utils.loadString(getString(R.string.save_username_pref), "");
                currentDrawable = ContextCompat.getDrawable(appContext, R.drawable.username);
                currentDrawable.setTint(ContextCompat.getColor(appContext, R.color.grey));
                createEditText(view, "Enter a username", false, 30);
                initUsernameSaveButton();
                break;
            case "FullName":
                this.defaultValue = utils.loadString(getString(R.string.save_fullname_pref), "");
                currentDrawable = ContextCompat.getDrawable(appContext, R.drawable.fullname);
                currentDrawable.setTint(ContextCompat.getColor(appContext, R.color.grey));
                createEditText(view, "Enter your name", true, 30);
                initSaveFullName();
                break;
            case "Bio":
                this.defaultValue = utils.loadString(getString(R.string.save_bio_pref), "");
                currentDrawable = ContextCompat.getDrawable(appContext, R.drawable.bio);
                currentDrawable.setTint(ContextCompat.getColor(appContext, R.color.grey));
                createEditText(view, "Enter a bio", true, 150);
                initSaveBio();
                break;
            case "Instagram":
                this.defaultValue = utils.loadString(getString(R.string.save_instagram_pref), "");
                currentDrawable = ContextCompat.getDrawable(appContext, R.drawable.instagram);
                currentDrawable.setTint(ContextCompat.getColor(appContext, R.color.grey));
                createEditText(view, "Enter your username", true, 30);
                initSocialMediaSave(detail);
                break;
            case "Twitter":
                this.defaultValue = utils.loadString(getString(R.string.save_twitter_pref), "");
                currentDrawable = ContextCompat.getDrawable(appContext, R.drawable.twitter);
                currentDrawable.setTint(ContextCompat.getColor(appContext, R.color.grey));
                createEditText(view, "Enter your username", true, 30);
                initSocialMediaSave(detail);
                break;
            case "VK":
                this.defaultValue = utils.loadString(getString(R.string.save_vk_pref), "");
                currentDrawable = ContextCompat.getDrawable(appContext, R.drawable.vk);
                currentDrawable.setTint(ContextCompat.getColor(appContext, R.color.grey));
                createEditText(view, "Enter your username", true, 30);
                initSocialMediaSave(detail);
                break;
            case "Email":
                this.defaultValue = utils.loadString(getString(R.string.save_email_pref), "");
                currentDrawable = ContextCompat.getDrawable(appContext, R.drawable.ic_form_icon);
                //currentDrawable.setTint(ContextCompat.getColor(appContext, R.color.grey));
                createEditText(view, "Enter your email", false, 25);
                break;
            case "Password":
                this.defaultValue = utils.loadString(getString(R.string.save_password_pref), "");
                currentDrawable = ContextCompat.getDrawable(appContext, R.drawable.ic_form_icon);
                //currentDrawable.setTint(ContextCompat.getColor(appContext, R.color.grey));
                createEditText(view, "Enter your new password", false, 25);
                break;

        }
        titleText.setText(detail);
    }

    /**
     * Create the edit text with a specific image.
     * Allows creates a checker to make sure the detail cannot be changed if the user tries to save the same value.
     * Also checks the user can't save a null value if that detail doesn't allow it
     * @param view baseview to use
     * @param extraText hint text
     * @param nullable whether this detail can be empty
     * @param characterLim character limit for this detail
     */
    private void createEditText(View view, String extraText, Boolean nullable, Integer characterLim){
        editText = new EditText(view.getContext());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * .8);
        int marginHeight = (int) (displaymetrics.heightPixels * .03);
        RelativeLayout.LayoutParams relLp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        relLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        relLp.topMargin = marginHeight;
        editText.setWidth(width);
        if (extraText != null){
            editText.setHint(extraText);
        }
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(currentDrawable, null, null, null);
        editText.setCompoundDrawablePadding((int) (width * .03));
        editText.setText(defaultValue);
        if (characterLim != null){
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(characterLim);
            editText.setFilters(filterArray);
        }

        relLay.addView(editText, relLp);
        TextWatcher textWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (nullable){
                    if (!s.toString().equals(defaultValue)){
                        saveButton.setVisibility(View.VISIBLE);
                    } else {
                        saveButton.setVisibility(View.GONE);
                    }
                } else {
                    if (!s.toString().isEmpty()){
                        if (!s.toString().equals(defaultValue)){
                            saveButton.setVisibility(View.VISIBLE);
                        } else {
                            saveButton.setVisibility(View.GONE);
                        }
                    } else {
                        saveButton.setVisibility(View.GONE);
                    }
                }
            }
        };
        editText.addTextChangedListener(textWatcher);
    }


    private void createImage(View view)  {
        ImageView imageView = new ImageView(view.getContext());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        RelativeLayout innerLP = new RelativeLayout(view.getContext());
        int width = (int) (displaymetrics.widthPixels * .35);
        int marginHeight = (int) (displaymetrics.heightPixels * .03);

        RelativeLayout.LayoutParams innerLaylp = new RelativeLayout.LayoutParams(
                width,
                width);
        innerLaylp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        innerLaylp.topMargin = marginHeight;
        relLay.addView(innerLP, innerLaylp);

        RelativeLayout.LayoutParams relLp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        //relLp.topMargin = marginHeight;
        innerLP.addView(imageView, relLp);
        loadImage(imageView);

        ImageButton alterImageButton = new ImageButton(view.getContext());
        alterImageButton.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Drawable drawable = ContextCompat.getDrawable(view.getContext(),R.drawable.plus).mutate();
        drawable.setTint(ContextCompat.getColor(view.getContext(), R.color.grey));
        Glide.with(view)
                .load(drawable)
                .into(alterImageButton);
        RelativeLayout.LayoutParams bnuttonLp = new RelativeLayout.LayoutParams(
                (int) (width * .2),
                (int) (width * .2));
        bnuttonLp.setMarginEnd((int) (width * .05));
        bnuttonLp.bottomMargin = ((int) (width * .05));
        bnuttonLp.addRule(RelativeLayout.ALIGN_PARENT_END);
        bnuttonLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        innerLP.addView(alterImageButton, bnuttonLp);
        alterImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImageSelector.imageClicked(imageView, saveButton);
            }
        });
        initImageSaveButton(imageView);
    }

    private void loadImage(ImageView imageView) {
        if (defaultValue != null){
            if (!defaultValue.equals("none")){
                Glide.with(imageView)
                        .load(defaultValue)
                        .circleCrop()
                        .into(imageView);
            } else {
                Glide.with(imageView)
                        .load(R.drawable.user_photo)
                        .circleCrop()
                        .into(imageView);
            }
        } else {
            Glide.with(imageView)
                    .load(R.drawable.user_photo)
                    .circleCrop()
                    .into(imageView);
        }
    }

    private void initImageSaveButton(ImageView imageView){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterButtons();
                Uri imageURI = profileImageSelector.getCurrentURI();
                Bitmap tempBitmap = null;
                Boolean errorChecker = false;
                try {
                    tempBitmap = MediaStore.Images.Media.getBitmap(imageView.getContext().getContentResolver(), imageURI);
                }
                catch (Exception e) {
                    errorChecker = true;
                }
                if (!errorChecker) {
                    imageCompresser imageCompresser = new imageCompresser();
                    Bitmap scaled = imageCompresser.compressBitmap(tempBitmap);
                    ByteArrayOutputStream blob = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.JPEG, 80 /* Ignored for PNGs */, blob);
                    byte[] bitmapdata = blob.toByteArray();

                    StorageReference fStorageReference = FirebaseStorage.getInstance().getReference("Posts/" + ServerAdminSingleton.getInstance().getLoggedInId() + "/ProfileImages");
                    String uniqueID = UUID.randomUUID().toString();
                    StorageReference fileReference = fStorageReference.child(uniqueID);
                    FirebaseFirestore ff = FirebaseFirestore.getInstance();
                    fileReference.putBytes(bitmapdata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String photoLink = uri.toString();
                                    Utils utils = new Utils(getContext().getApplicationContext());
                                    HttpUrl.Builder httpBuilder = HttpUrl.parse(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Users/UserDetails/UpdateUserImage").newBuilder();
                                    JSONObject jsonObject = new JSONObject();
                                    OkHttpClient client = new OkHttpClient();
                                    try {
                                        jsonObject.put("imageURL", photoLink);
                                    } catch (JSONException e) {
                                        fileReference.delete();
                                        Toast.makeText(getActivity(), "Failed to upload Image", Toast.LENGTH_SHORT).show();
                                    }
                                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                    Request request = new Request.Builder()
                                            .url(httpBuilder.build())
                                            .method("POST", body)
                                            .addHeader("Content-Type", "application/json")
                                            .addHeader("Authorization", ServerAdminSingleton.getInstance().getServerAuthToken())
                                            .build();
                                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Response response = client.newCall(request).execute();
                                                if (response.isSuccessful()) {
                                                    utils.saveString(getString(R.string.save_profileP_pref), photoLink);
                                                    ToastOnOtherThread("Profile Image Changed");
                                                    otherThreadBack();
                                                } else {
                                                    fileReference.delete();
                                                    ToastOnOtherThread("Failed to upload Image");
                                                }
                                            } catch (IOException e) {
                                                fileReference.delete();
                                                ToastOnOtherThread("Failed to upload Image");
                                            }
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    fileReference.delete();
                                    Toast.makeText(getActivity(), "Failed to upload Image", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            NoInternetDropDown dropDown = NoInternetDropDown.getInstance(getActivity());
                            dropDown.showDropDown();
                            Toast.makeText(getActivity(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Create the button to save the username, and perform the saving of the username if clicked (to both server and local)
     */
    private void initUsernameSaveButton(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editText.getText().toString();
                alterButtons();
                usernameValidityChecker validityChecker = new usernameValidityChecker();
                if (validityChecker.checkInput(newUsername)){
                    Utils utils = new Utils(getContext().getApplicationContext());
                    HttpUrl.Builder httpBuilder = HttpUrl.parse(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Users/UserDetails/UpdateUsername").newBuilder();
                    JSONObject jsonObject = new JSONObject();
                    OkHttpClient client = new OkHttpClient();
                    try {
                        jsonObject.put("username", newUsername);
                    } catch (JSONException e) {
                        ToastOnOtherThread("Unknown Error");
                        resetButtons();
                    }
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                    Request request = new Request.Builder()
                            .url(httpBuilder.build())
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", ServerAdminSingleton.getInstance().getServerAuthToken())
                            .build();
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Response response = client.newCall(request).execute();
                                if (response.isSuccessful()) {
                                   JSONObject responseJSON = new JSONObject(response.body().string());
                                   if (responseJSON.getBoolean("usernameTaken")){
                                       utils.saveString(getString(R.string.save_username_pref), newUsername);
                                       ToastOnOtherThread("Username Changed");
                                       otherThreadBack();
                                   } else {
                                       ToastOnOtherThread("Username taken");
                                       resetButtons();
                                   }
                                } else {
                                    ToastOnOtherThread("Failed to access server");
                                    resetButtons();

                                }
                            } catch (IOException | JSONException e) {
                                ToastOnOtherThread("Failed to access server");
                                resetButtons();
                            }
                        }
                    });

                } else {
                    Toast.makeText(saveButton.getContext(), "Invalid Username", Toast.LENGTH_SHORT).show();
                    editText.setEnabled(true);
                    saveButton.setColorFilter(null);
                }
            }
        });
    }

    /**
     * Create the button to save the bio, and perform the saving of the bio if clicked (to both server and local)
     */
    private void initSaveBio(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils utils = new Utils(getActivity().getApplicationContext());
                String bio = editText.getText().toString();
                alterButtons();

                HttpUrl.Builder httpBuilder = HttpUrl.parse(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Users/UserDetails/UpdateBio").newBuilder();
                JSONObject jsonObject = new JSONObject();
                OkHttpClient client = new OkHttpClient();
                try {
                    jsonObject.put("bio", bio);
                } catch (JSONException e) {
                    ToastOnOtherThread("Unknown Error");
                    resetButtons();
                }
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", ServerAdminSingleton.getInstance().getServerAuthToken())
                        .build();
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response response = client.newCall(request).execute();
                            if (response.isSuccessful()) {
                                utils.saveString(getString(R.string.save_bio_pref), bio);
                                ToastOnOtherThread("Bio Changed");
                                otherThreadBack();
                            } else {
                                ToastOnOtherThread("Failed to access server");
                                resetButtons();

                            }
                        } catch (IOException e) {
                            ToastOnOtherThread("Failed to access server");
                            resetButtons();
                        }
                    }
                });
            }
        });
    }


    /**
     * Create the button to save the full name, and perform the saving of the full name if clicked (to both server and local)
     */
    private void initSaveFullName(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils utils = new Utils(getActivity().getApplicationContext());
                String fullName = editText.getText().toString();
                alterButtons();

                HttpUrl.Builder httpBuilder = HttpUrl.parse(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Users/UserDetails/UpdateFullname").newBuilder();
                JSONObject jsonObject = new JSONObject();
                OkHttpClient client = new OkHttpClient();
                try {
                    jsonObject.put("fullname", fullName);
                } catch (JSONException e) {
                    ToastOnOtherThread("Unknown Error");
                    resetButtons();
                }
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", ServerAdminSingleton.getInstance().getServerAuthToken())
                        .build();
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response response = client.newCall(request).execute();
                            if (response.isSuccessful()) {
                                if (!isDetached()) {
                                    utils.saveString(getString(R.string.save_fullname_pref), fullName);
                                    ToastOnOtherThread("Fullname Changed");
                                    otherThreadBack();
                                }
                            } else {
                                if (!isDetached()) {
                                    ToastOnOtherThread("Failed to access server");
                                    resetButtons();
                                }

                            }
                        } catch (IOException e) {
                            ToastOnOtherThread("Failed to access server");
                            resetButtons();
                        }
                    }
                });

            }
        });
    }

    /**
     * Create the button to save one of the social medias, and perform the saving of the social media if clicked. (to both server and local)
     * Saves the individual social media
     * @param socialMedia which social media to save
     */
    private void initSocialMediaSave(String socialMedia){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils utils = new Utils(getActivity().getApplicationContext());
                String username = editText.getText().toString();
                alterButtons();
                FirebaseFirestore ff = FirebaseFirestore.getInstance();
                String updater = null;
                String sharedPref = null;
                if (socialMedia.equals("Instagram")){
                    updater = "Instagram";
                    sharedPref = getString(R.string.save_instagram_pref);
                } else if (socialMedia.equals("Twitter")){
                    updater = "Twitter";
                    sharedPref = getString(R.string.save_twitter_pref);
                } else if (socialMedia.equals("VK")) {
                    updater = "Vk";
                    sharedPref = getString(R.string.save_vk_pref);
                }
                if (updater != null && sharedPref != null) {

                    HttpUrl.Builder httpBuilder = HttpUrl.parse(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Users/UserDetails/UpdateSocialMedia").newBuilder();
                    JSONObject jsonObject = new JSONObject();
                    OkHttpClient client = new OkHttpClient();
                    try {
                        jsonObject.put("type", updater);
                        jsonObject.put("value", username);
                    } catch (JSONException e) {
                        ToastOnOtherThread("Unknown Error");
                        resetButtons();
                    }
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                    Request request = new Request.Builder()
                            .url(httpBuilder.build())
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", ServerAdminSingleton.getInstance().getServerAuthToken())
                            .build();
                    String finalSharedPref = sharedPref;
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Response response = client.newCall(request).execute();
                                if (response.isSuccessful()) {
                                    if (!isDetached()) {
                                        utils.saveString(finalSharedPref, username);
                                        ToastOnOtherThread("Updated");
                                        otherThreadBack();
                                    }
                                } else {
                                    ToastOnOtherThread("Failed to access server");
                                    resetButtons();
                                }
                            } catch (IOException e) {
                                ToastOnOtherThread("Failed to access server");
                                resetButtons();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Performs a toast on another thread.
     * @param text text of toast to show
     */
    private void ToastOnOtherThread(String text){
        if (!isDetached()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Resets the buttons on the main thread
     */
    private void resetButtons(){
        if (!isDetached()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    editText.setEnabled(true);
                    saveButton.setColorFilter(null);
                    backButton.setColorFilter(null);
                    backButton.setEnabled(true);
                }
            });
        }
    }

    /**
     * Alters the colours of the buttons
     */
    private void alterButtons(){
        if (!isDetached()) {
            if (editText != null) {
                editText.setEnabled(false);
            }
            saveButton.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.teal_200));
            backButton.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.grey));
            backButton.setEnabled(false);
        }
    }

    /**
     * Navigate back on the main thread.
     */
    private void otherThreadBack(){
        if (!isDetached()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Navigation.findNavController(DetailsEditFragment.this.getView()).navigateUp();
                }
            });
        }
    }

}
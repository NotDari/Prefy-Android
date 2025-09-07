package com.daribear.prefy.SubmitPost;

import static android.app.Activity.RESULT_OK;
import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daribear.prefy.Activities.MainActivity;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.CurrentTime;
import com.daribear.prefy.Utils.NoInternetDropDown;
import com.daribear.prefy.Utils.Permissions.PermissionReceived;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.Utils.SharedPreferences.Utils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The dialog for submitting a post.
 * Allows the user to set the category, select images add questions etc.
 * Everything needed for a post.
 * Then uploads the post image to firebase and the post to the backend.
 * Is a singleton to stop multiple dialogs being opened.
 */
public class SubmitPostDialog implements PermissionReceived {
    private Context context;
    private BottomSheetDialog bottomSheetDialog;
    private String category;
    ImageView normalSelector, duelSelector, beforeAfterSelector, crazySelector;
    private static SubmitPostDialog instance = null;
    private final int CODE_IMG_GALLERY = 1;
    private Activity activity;
    private ImageView PostDialogLeftClickerView, PostDialogRightClickerView, leftButton, rightButton;
    private Integer imageSelected;
    private final String UCROP_IMAGE_FILE_NAME = "UCROP_IMAGE_FILE";
    private Boolean leftImageSelected, rightImageSelected;
    private AppCompatButton leftRemove, rightRemove;
    private ImageView sendButton;
    private EditText questionEdit;
    private StorageReference fStorageReference;
    private FirebaseDatabase database;


    private FlexboxLayout flexboxLayout;
    private ArrayList<Boolean> categoryActiveList;
    private ArrayList<String> categoriesList;
    private Integer categoryHeight = 0;
    private MaterialButton moreCategories;
    private Boolean allCategories, postSubmitting;



    //Gets the instance of the dialog
    public static synchronized SubmitPostDialog getInstance(Activity activity) {
        if(instance == null)
            instance = new SubmitPostDialog(activity);


        return instance;
    }


    private SubmitPostDialog(Activity activity) {
        this.activity = activity;
    }

    /**
     * Displays the dialog and creates all the actions for the buttons,
     * including the dismiss button.
     *
     * @param context the context to launch the activity with
     */
    public void displaySheet(Context context){
        this.context = context;
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.post_bottom_dialog);
        postSubmitting = false;
        getViews(bottomSheetDialog);
        //handleCategorySelector(bottomSheetDialog);
        handleClose(bottomSheetDialog);
        handleButtonsPressed(bottomSheetDialog);
        initImageRemoveButtons();
        initFlex(bottomSheetDialog);

        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                instance = null;
            }
        });
    }

    /**
     * Retrieves and stores references to all relevant views in the dialog.
     */
    private void getViews(BottomSheetDialog bottomSheetDialog){
        imageSelected = 0;
        PostDialogLeftClickerView = bottomSheetDialog.findViewById(R.id.PostDialogLeftClickerView);
        PostDialogRightClickerView = bottomSheetDialog.findViewById(R.id.PostDialogRightClickerView);
        leftRemove = bottomSheetDialog.findViewById(R.id.PostDialogLeftImageRemoveButton);
        rightRemove = bottomSheetDialog.findViewById(R.id.PostDialogRightImageRemoveButton);
        leftButton = bottomSheetDialog.findViewById(R.id.PostDialogLeftUploadButton);
        rightButton = bottomSheetDialog.findViewById(R.id.PostDialogRightUploadButton);
        sendButton = bottomSheetDialog.findViewById(R.id.PostDialogSendButton);
        questionEdit = bottomSheetDialog.findViewById(R.id.PostDialogQuestionEdit);
        flexboxLayout = bottomSheetDialog.findViewById(R.id.PostDialogCategoriesLayout);
        moreCategories = bottomSheetDialog.findViewById(R.id.PostDialogMoreCategories);
    }

    /**
     * Handles the image selection and send button clicks.
     */
    private void handleButtonsPressed(BottomSheetDialog bottomSheetDialog){
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostDialogLeftClickerView.setImageDrawable(null);
                imageSelected = 1;
                attemptToGetPost();
                //GetImageChoiceDialog dialog = new GetImageChoiceDialog(context);
                //dialog.initDialog();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSelected = 2;
                attemptToGetPost();
                //GetImageChoiceDialog dialog = new GetImageChoiceDialog(context);
                //dialog.initDialog();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSendButton();
            }
        });
    }

    /**
     * Handles the close button behavior.
     * Prevents closing while post is being uploaded.
     */
    private void handleClose(BottomSheetDialog bottomSheetDialog){
        ImageView closeButton = bottomSheetDialog.findViewById(R.id.PostDialogCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!postSubmitting) {
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(context, "Wait until post uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Requests storage permission to select an image from device.
     */
    private void attemptToGetPost(){
        ((MainActivity)activity).requestPermission(this::granted, Manifest.permission.READ_EXTERNAL_STORAGE);
    }


    /**
     * Handles the result of image selection from gallery.
     */
    public void OnImageActivityResult(Activity activity, int resultCode, @Nullable Intent data){
        if (resultCode == RESULT_OK && data != null & data.getData() != null){
            Uri imageUri = data.getData();
            initUCrop(activity, imageUri);
        }
    }

    /**
     * Handles the result of UCrop activity after cropping an image.
     */
    public void OnUcropActivityResult(Activity activity, int resultCode, @Nullable Intent data){
        if (resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);

            displayImage(activity,resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    /**
     * Starts UCrop activity for cropping the selected image.
     */
    private void initUCrop(Activity activity ,Uri sourceUri){
        String destinationName = UCROP_IMAGE_FILE_NAME + ".png";
        UCrop.Options UcropOptions = new UCrop.Options();
        File file = new File(activity.getCacheDir(), destinationName);
        UcropOptions.setStatusBarColor(ContextCompat.getColor(activity, R.color.black));
        Intent intent = UCrop
                .of(sourceUri, Uri.fromFile(file))
                .withAspectRatio(8, 16)
                .withOptions(UcropOptions)
                .getIntent(activity);
        if (activity instanceof MainActivity){
            ((MainActivity) activity).getPostCrop(intent);
        }




    }

    /**
     * Displays the cropped image in the appropriate image view (left/right).
     */
    private void displayImage(Activity activity,Uri resultUri){
        if (imageSelected == 1){
            Glide.with(activity)
                    .load(resultUri)
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy((DiskCacheStrategy.NONE))
                    .into(PostDialogLeftClickerView);
            leftImageSelected = true;
            leftRemove.setVisibility(View.VISIBLE);
            leftButton.setVisibility(View.GONE);
            imageSelected = 0;
        } else if (imageSelected == 2){
            Glide.with(activity)
                    .load(resultUri)
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy((DiskCacheStrategy.NONE))
                    .into(PostDialogRightClickerView);
            rightImageSelected = true;
            rightRemove.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.GONE);
            imageSelected = 0;
        } else{
            Toast.makeText(activity, ("Error " + imageSelected), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initialises the remove buttons for left and right images.
     */
    private void initImageRemoveButtons(){
        leftRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostDialogLeftClickerView.setImageDrawable(null);
                leftImageSelected = false;
                leftRemove.setVisibility(View.GONE);
                leftButton.setVisibility(View.VISIBLE);
            }
        });
        rightRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostDialogRightClickerView.setImageDrawable(null);
                rightImageSelected = false;
                rightRemove.setVisibility(View.GONE);
                rightButton.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Checks input validity and initiates sending the post.
     */
    private void initSendButton(){
        if (PostDialogLeftClickerView.getDrawable() != null && PostDialogRightClickerView.getDrawable() != null ){
            String question = questionEdit.getText().toString();
            if (question != null && !question.isEmpty()){
                if (!postSubmitting) {
                    postSubmitting = true;
                    database = FirebaseDatabase.getInstance();
                    fStorageReference = FirebaseStorage.getInstance().getReference("Posts/" + ServerAdminSingleton.getInstance().getLoggedInId() + "/PostImages");
                    Bitmap postBitmap = combineBitmaps(PostDialogLeftClickerView, PostDialogRightClickerView);
                    initSendPost(question, postBitmap);
                }
            } else {
                Toast.makeText(activity, "Please provide a question", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Please select both images", Toast.LENGTH_SHORT).show();
        }



    }

    /**
     * Sends the post image to the firebase database
     * @param question the question for the post
     * @param image the image of the post.
     */
    private void initSendPost(String question, Bitmap image){
        imageCompresser imageCompresser = new imageCompresser();

        Bitmap scaled = imageCompresser.compressBitmap(image);
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 80 /* Ignored for PNGs */, blob);
        byte[] bitmapdata = blob.toByteArray();



        String uniqueID = UUID.randomUUID().toString();
        StorageReference fileReference = fStorageReference.child(uniqueID);
        Long longTime = CurrentTime.getCurrentTime();
        Double time= (double) CurrentTime.getCurrentTime();
        Double date = time / 1000;
        Long finalTime = date.longValue();
        ArrayList<String> categories = new ArrayList<>();
        for (int i = 0; i < categoryActiveList.size(); i++){
            if (categoryActiveList.get(i) == true){
                categories.add(categoriesList.get(i));
            }
        }
        if (categories.size() == 0){
            categories.add("none");
        }
        fileReference.putBytes(bitmapdata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                String photoLink = uri.toString();
                                OkHttpClient client = new OkHttpClient();
                                HttpUrl.Builder httpBuilder = HttpUrl.parse(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Posts/SubmitPost").newBuilder();
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("allVotes", 0);
                                    jsonObject.put("categoryList", new JSONArray(categories));
                                    jsonObject.put("commentsNumber", 0);
                                    jsonObject.put("creationDate", finalTime);
                                    jsonObject.put("featured", false);
                                    jsonObject.put("popular", false);
                                    jsonObject.put("imageURL", photoLink);
                                    jsonObject.put("leftVotes", 0);
                                    jsonObject.put("question", question);
                                    jsonObject.put("rightVotes", 0);
                                    jsonObject.put("userId", ServerAdminSingleton.getInstance().getLoggedInId());
                                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                    Request request = new Request.Builder()
                                            .url(httpBuilder.build())
                                            .method("POST", body)
                                            .addHeader("Content-Type", "application/json")
                                            .addHeader("Authorization", ServerAdminSingleton.getInstance().getServerAuthToken())
                                            .build();
                                    try {
                                        Response response = client.newCall(request).execute();
                                        if (response.isSuccessful()) {
                                            initPostActions(photoLink);
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, "Post Successful", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            postSubmitting = false;
                                            fileReference.delete();
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, "Failed to upload Image", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    } catch (IOException e) {
                                        postSubmitting = false;
                                        fileReference.delete();
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "Failed to upload Image", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                } catch (JSONException e){
                                    postSubmitting = false;
                                    fileReference.delete();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "Failed to upload Image", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        postSubmitting = false;
                        fileReference.delete();
                        Toast.makeText(context, "Failed to upload Image", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                postSubmitting = false;
                NoInternetDropDown dropDown = NoInternetDropDown.getInstance(activity);
                dropDown.showDropDown();

                Toast.makeText(activity, "Image Upload Failed " + e, Toast.LENGTH_SHORT).show();
            }
        });


    }


    /**
     * Combines left and right images into a single bitmap for uploading.
     */
    private Bitmap combineBitmaps(ImageView leftImageView, ImageView rightImageView){
        Bitmap leftBitmap = ((BitmapDrawable)PostDialogLeftClickerView.getDrawable()).getBitmap();
        Bitmap rightBitmap = ((BitmapDrawable)PostDialogRightClickerView.getDrawable()).getBitmap();
        Bitmap combinedBitmap;

        int width, height = 0;

        if(leftBitmap.getWidth() > rightBitmap.getWidth()) {
            width = leftBitmap.getWidth() + rightBitmap.getWidth();
            height = leftBitmap.getHeight();
        } else {
            width = rightBitmap.getWidth() + rightBitmap.getWidth();
            height = leftBitmap.getHeight();
        }

        combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(combinedBitmap);

        comboImage.drawBitmap(leftBitmap, 0f, 0f, null);
        comboImage.drawBitmap(rightBitmap, leftBitmap.getWidth(), 0f, null);
        return combinedBitmap;
    }

    /**
     * Increases the user's posts count in the shared preferences and closes the dialog
     * @param photoLink
     */
    private void initPostActions(String photoLink){
        Glide.with(context)
                .load(photoLink)
                .preload();
        Utils utils = new Utils(context);
        utils.saveLong(context.getString(R.string.save_postCount_pref), (utils.loadLong(context.getString(R.string.save_postCount_pref), 0) + 1));
        bottomSheetDialog.dismiss();
    }

    /**
     * Initialises the category selection Flexbox layout.
     * Dynamically adds TextViews for each category and sets up click listeners.
     * Also configures the "More/Less" button to expand/collapse categories.
     *
     * @param bottomSheetDialog the dialog
     */
    private void initFlex(BottomSheetDialog bottomSheetDialog){
        Context applicationContext = bottomSheetDialog.getContext().getApplicationContext();
        String[] categoriesArrayList = applicationContext.getResources().getStringArray(R.array.post_categories);
        categoriesList = new ArrayList<>(Arrays.asList(categoriesArrayList));
        Integer verticalpadding = 16;
        allCategories = false;
        categoryActiveList = new ArrayList<>();
        for (int i = 0; i < categoriesArrayList.length; i ++){
            categoryActiveList.add(false);
            TextView textView = new TextView(bottomSheetDialog.getContext());
            textView.setText(categoriesArrayList[i]);
            textView.setBackgroundResource(R.drawable.newpost_categories_background);
            textView.setTextColor(Color.parseColor("#858585"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            textView.setPadding(20, 10, 20, 10);
            flexboxLayout.addView(textView);
            ViewGroup.MarginLayoutParams marginparams = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            marginparams.setMargins(15, (verticalpadding/2), 15, (verticalpadding/2));
            int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CategoryClicked(textView, finalI);
                }
            });
            if (finalI == 0){
                textView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
                {
                    @Override
                    public boolean onPreDraw()
                    {
                        if (textView.getViewTreeObserver().isAlive())
                            textView.getViewTreeObserver().removeOnPreDrawListener(this);

                        categoryHeight = textView.getMeasuredHeight();
                        ViewGroup.LayoutParams lp = flexboxLayout.getLayoutParams();
                        lp.height = (categoryHeight + verticalpadding);
                        flexboxLayout.setLayoutParams(lp);
                        return true;
                    }
                });
            }

        }
        moreCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer temporaryHeight;
                if (!allCategories){
                    moreCategories.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_upward_24), null);
                    moreCategories.setText("Less");
                    temporaryHeight = (flexboxLayout.getFlexLines().size()) * (categoryHeight + verticalpadding);
                } else {
                    moreCategories.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_downward_24),null);
                    moreCategories.setText("More");
                    temporaryHeight = (categoryHeight + verticalpadding);
                }
                allCategories = !allCategories;
                ViewGroup.LayoutParams lp = flexboxLayout.getLayoutParams();
                lp.height = (temporaryHeight);
                flexboxLayout.setLayoutParams(lp);
            }
        });


    }

    /**
     * Called when a category is clicked to enable the UX of showing the category is selected/unselected.
     * @param textView textview of the category
     * @param position position of the category in the lsit
     */
    private void CategoryClicked(TextView textView,Integer position){
        Boolean selected = categoryActiveList.get(position);
        if (!selected){
            textView.setBackgroundResource(R.drawable.newpost_categories_selected_background);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            textView.setBackgroundResource(R.drawable.newpost_categories_background);
            textView.setTextColor(Color.parseColor("#858585"));
        }
        categoryActiveList.set(position, !selected);
    }

    /**
     * Interface callback when the storage permissions is granted or denied.
     * @param Granted whether the permission was granted
     */
    @Override
    public void granted(Boolean Granted) {
        if (Granted) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).getPostImage(intent);
            }
        } else {
            Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.itheamc.hamroclassroom_student.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.itheamc.hamroclassroom_student.R;
import com.itheamc.hamroclassroom_student.adapters.ImageAdapter;
import com.itheamc.hamroclassroom_student.callbacks.ImageCallbacks;
import com.itheamc.hamroclassroom_student.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_student.callbacks.StorageCallbacks;
import com.itheamc.hamroclassroom_student.databinding.FragmentSubmitBinding;
import com.itheamc.hamroclassroom_student.handlers.QueryHandler;
import com.itheamc.hamroclassroom_student.handlers.StorageHandler;
import com.itheamc.hamroclassroom_student.models.Assignment;
import com.itheamc.hamroclassroom_student.models.Notice;
import com.itheamc.hamroclassroom_student.models.School;
import com.itheamc.hamroclassroom_student.models.Subject;
import com.itheamc.hamroclassroom_student.models.Submission;
import com.itheamc.hamroclassroom_student.models.Teacher;
import com.itheamc.hamroclassroom_student.models.User;
import com.itheamc.hamroclassroom_student.utils.ArrayUtils;
import com.itheamc.hamroclassroom_student.utils.IdGenerator;
import com.itheamc.hamroclassroom_student.utils.ImageUtils;
import com.itheamc.hamroclassroom_student.utils.LocalStorage;
import com.itheamc.hamroclassroom_student.utils.NotifyUtils;
import com.itheamc.hamroclassroom_student.utils.TimeUtils;
import com.itheamc.hamroclassroom_student.utils.ViewUtils;
import com.itheamc.hamroclassroom_student.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SubmitFragment extends Fragment implements StorageCallbacks, ImageCallbacks {
    private static final String TAG = "SubmitFragment";
    private FragmentSubmitBinding submitBinding;
    private MainViewModel viewModel;
    private NavController navController;

    /*
    Image Picker Related
     */
    private ActivityResultLauncher<Intent> imagePickerResultLauncher;
    private List<Uri> imagesUri;
    private ImageAdapter imageAdapter;

    /*
   List to store the uploaded image url
    */
    private String[] images;

    /*
    TextInputLayout
     */
    private TextInputLayout textInputLayout;

    /*
    EditTexts
     */
    private EditText textEdittext;

    /*
    Strings
     */
    private String _text = "";

    /*
    Boolean
     */
    private boolean is_uploading = false;   // To handle the image remove


    // Constructor
    public SubmitFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        submitBinding = FragmentSubmitBinding.inflate(inflater, container, false);
        return submitBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and MainViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing Image adapter and setting to the recycler view
        imageAdapter = new ImageAdapter(this);
        submitBinding.submissionRecyclerView.setAdapter(imageAdapter);

        // Initializing InputLayout and Edittext
        textInputLayout = submitBinding.textInputLayout;

        textEdittext = textInputLayout.getEditText();

        // Activity Result launcher to listen the result of the multi image picker
        imagePickerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (submitBinding == null) return;
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data == null) return;

                        ViewUtils.hideViews(submitBinding.imagePickerButton);     // To hide the image picker
                        imagesUri = new ArrayList<>();

                        // Get the Images from data
                        ClipData mClipData = data.getClipData();
                        if (mClipData != null) {
                            int count = mClipData.getItemCount();
                            for (int i = 0; i < count; i++) {
                                // adding imageUri in array
                                Uri imageUri = mClipData.getItemAt(i).getUri();
                                imagesUri.add(imageUri);
                            }

                            submitImagesToImageAdapter();   // Submitting image to adapter
                            return;
                        }

                        Uri imageUri = data.getData();
                        imagesUri.add(imageUri);
                        submitImagesToImageAdapter();   // Submitting image to adapter

                    } else {
                        NotifyUtils.logDebug(TAG, "|____Image picker closed____|");
                    }
                });

        /*
        Setting OnClickListener
         */
        submitBinding.imagePickerButton.setOnClickListener(v -> showImagePicker());
        submitBinding.submitButton.setOnClickListener(v -> {
            storeOnDatabase();
        });


    }

    /**
     * -----------------------------------------------------------------------------
     * Function to start the image picker intent
     */
    private void showImagePicker() {
        // initialising intent
        Intent intent = new Intent();

        // setting type to select to be image
        intent.setType("image/*");

        // allowing multiple image to be selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerResultLauncher.launch(Intent.createChooser(intent, "Select Images"));
    }

    /*
    Function to submit data to image adapter
     */
    private void submitImagesToImageAdapter() {
        if (imagesUri != null && imagesUri.size() > 0) imageAdapter.submitList(imagesUri);
    }


    /**
     * --------------------------------------------------------------------------
     * Function to handle Firestore upload
     * It will bi triggered only after all the images uploaded
     */
    private void storeOnDatabase() {
        if (getActivity() == null) return;
        Assignment assignment = viewModel.getAssignment();
        String userId = LocalStorage.getInstance(getActivity()).getUserId();
        if (textEdittext != null) _text = textEdittext.getText().toString().trim();

        HandlerCompat.createAsync(Looper.getMainLooper()).post(() -> submitBinding.uploadedProgress.setText("Please Wait.."));

        // Creating new submission object
        Submission submission = new Submission(
                IdGenerator.generateRandomId(),
                null,
                null,
                _text,
                assignment.get_id(),
                null,
                userId,
                null,
                TimeUtils.now(),
                "",
                false,
                ""

        );

        if (getActivity() != null) {
            showProgress();
            is_uploading = true;
            StorageHandler.getInstance(getActivity(), this)
                    .addSubmission(imagesUri, submission);
        }
    }

    /*
    Function to show progress
     */
    private void showProgress() {
        ViewUtils.showProgressBar(submitBinding.progressBarContainer);
        ViewUtils.disableViews(submitBinding.submitButton, textInputLayout);
    }

    /*
    Function to hide progress
     */
    private void hideProgress() {
        ViewUtils.hideProgressBar(submitBinding.progressBarContainer);
        ViewUtils.enableViews(submitBinding.submitButton, textInputLayout);
    }



    /**
     * ---------------------------------------------------------------------------
     * Function to make edittext clear
     */
    private void clearEdittext() {
        if (submitBinding == null) return;

        ViewUtils.clearEditTexts(textEdittext);
        if (imagesUri != null) {
            imagesUri.clear();
            imageAdapter.submitList(imagesUri);
        }
        ViewUtils.visibleViews(submitBinding.imagePickerButton);    // To Show the image picker button
    }


    /**
     * ----------------------------------------------------------------------------------------
     * These are the methods implemented from the StorageCalbacks
     * @param message - success response message
     */

    @Override
    public void onSuccess(String message) {
        if (submitBinding == null) return;
        hideProgress();
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Added Successfully");
        is_uploading = false;
        clearEdittext();
    }

    @Override
    public void onFailure(Exception e) {
        if (submitBinding == null) return;
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Upload Failed");
        NotifyUtils.logError(TAG, "onFailure()", e);
        is_uploading = false;
        hideProgress();
    }

    @Override
    public void onCanceled() {

    }

    /**
     * -------------------------------------------------------------------
     * This method is implemented from the ImageViewCallback
     * -------------------------------------------------------------------
     */

    @Override
    public void onRemove(int _position) {
        if (is_uploading) return;
        imagesUri.remove(_position);
        imageAdapter.notifyItemRemoved(_position);
        NotifyUtils.logDebug(TAG, imagesUri.toString());
        if (imagesUri.size() == 0) ViewUtils.visibleViews(submitBinding.imagePickerButton);
    }


    /*
    Overrided function to view destroy
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        submitBinding = null;
    }



}
package com.itheamc.hamroclassroom_student.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.itheamc.hamroclassroom_student.adapters.SliderAdapter;
import com.itheamc.hamroclassroom_student.databinding.FragmentSubmissionBinding;
import com.itheamc.hamroclassroom_student.models.Submission;
import com.itheamc.hamroclassroom_student.utils.ArrayUtils;
import com.itheamc.hamroclassroom_student.viewmodel.MainViewModel;


public class SubmissionFragment extends Fragment {
    private static final String TAG = "SubmissionFragment";
    private FragmentSubmissionBinding submissionBinding;


    // Constructor
    public SubmissionFragment() {
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
        submissionBinding = FragmentSubmissionBinding.inflate(inflater, container, false);
        return submissionBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and ViewModel
        NavController navController = Navigation.findNavController(view);
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);


        // Initializing slider adapter...
        SliderAdapter sliderAdapter = new SliderAdapter();
        submissionBinding.submissionViewPager.setAdapter(sliderAdapter);

        Submission submission = viewModel.getSubmission();
        if (submission != null) {
            submissionBinding.setSubmission(submission);
            if (submission.get_images().length > 0) sliderAdapter.submitList(ArrayUtils.asList(submission.get_images()));
        }
    }

    /*
    Overrided function to view destroy
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        submissionBinding = null;
    }
}
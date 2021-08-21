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

import com.itheamc.hamroclassroom_student.R;
import com.itheamc.hamroclassroom_student.adapters.SubmissionAdapter;
import com.itheamc.hamroclassroom_student.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_student.callbacks.SubmissionCallbacks;
import com.itheamc.hamroclassroom_student.databinding.FragmentSubmissionsBinding;
import com.itheamc.hamroclassroom_student.handlers.QueryHandler;
import com.itheamc.hamroclassroom_student.models.Assignment;
import com.itheamc.hamroclassroom_student.models.Notice;
import com.itheamc.hamroclassroom_student.models.School;
import com.itheamc.hamroclassroom_student.models.Subject;
import com.itheamc.hamroclassroom_student.models.Submission;
import com.itheamc.hamroclassroom_student.models.Teacher;
import com.itheamc.hamroclassroom_student.models.User;
import com.itheamc.hamroclassroom_student.utils.LocalStorage;
import com.itheamc.hamroclassroom_student.utils.NotifyUtils;
import com.itheamc.hamroclassroom_student.utils.ViewUtils;
import com.itheamc.hamroclassroom_student.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SubmissionsFragment extends Fragment implements QueryCallbacks, SubmissionCallbacks {
    private static final String TAG = "SubmissionsFragment";
    private FragmentSubmissionsBinding submissionsBinding;
    private NavController navController;
    private MainViewModel viewModel;
    private SubmissionAdapter submissionAdapter;


    public SubmissionsFragment() {
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
        submissionsBinding = FragmentSubmissionsBinding.inflate(inflater, container, false);
        return submissionsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and ViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing AssignmentAdapter and setting to recyclerview
        submissionAdapter = new SubmissionAdapter(this);
        submissionsBinding.submissionsRecyclerView.setAdapter(submissionAdapter);


        // Setting swipe and refresh layout
        submissionsBinding.submissionsSwipeRefreshLayout.setOnRefreshListener(() -> {
            ViewUtils.hideViews(submissionsBinding.noSubmissionLayout);

            checksUser();
        });

        // Handling back button
        submissionsBinding.backButton.setOnClickListener(v -> {
            navController.popBackStack();
        });


        // Checking if submissions are already stored in the viewModel or not
        List<Submission> listOfSubmissions = viewModel.getAllSubmissions();
        if (listOfSubmissions != null && !listOfSubmissions.isEmpty()) {
            submissionAdapter.submitList(listOfSubmissions);
        } else {
            checksUser();
        }
    }

    /*
    Function to check user first
     */
    private void checksUser() {
        User user = viewModel.getUser();
        if (user == null) {
            if (getActivity() != null) {
                QueryHandler.getInstance(this).getUser(LocalStorage.getInstance(getActivity()).getUserId());
                showProgressBar();
            }
            return;
        }

        retrieveSubmissions();
        showProgressBar();
    }


    /*
    Function to retrieve submissions
     */
    private void retrieveSubmissions() {
        QueryHandler.getInstance(this).getSubmissions(viewModel.getUser().get_id());
    }


    /*
    Function to hide progressbar
     */
    private void hideProgressBar() {
        ViewUtils.hideProgressBar(submissionsBinding.submissionsOverlayLayLayout);
        ViewUtils.handleRefreshing(submissionsBinding.submissionsSwipeRefreshLayout);
    }

    /*
    Function to show progressbar
     */
    private void showProgressBar() {
        ViewUtils.showProgressBar(submissionsBinding.submissionsOverlayLayLayout);
        ViewUtils.handleRefreshing(submissionsBinding.submissionsSwipeRefreshLayout);
    }


    /**
     * --------------------------------------------------------------------------
     * Function Implemented from the FirestoreCallbacks
     */
    @Override
    public void onQuerySuccess(List<User> users, List<School> schools, List<Teacher> teachers, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Notice> notices) {
        if (submissionsBinding == null) return;

        // If submissions is retrieved
        if (submissions != null) {
            if (!submissions.isEmpty()) {
                viewModel.setAllSubmissions(submissions);
                submissionAdapter.submitList(submissions);
                return;
            }
            ViewUtils.visibleViews(submissionsBinding.noSubmissionLayout);
        }
        hideProgressBar();
    }

    @Override
    public void onQuerySuccess(User user, School school, Teacher teacher, Subject subject, Assignment assignment, Submission submission, Notice notice) {
        if (submissionsBinding == null) return;

        if (user != null) {
            viewModel.setUser(user);
            retrieveSubmissions();
        }
    }

    @Override
    public void onQuerySuccess(String message) {

    }

    @Override
    public void onQueryFailure(Exception e) {
        if (submissionsBinding == null) return;
        hideProgressBar();
        if (e.getMessage() == null) return;
        if (e.getMessage().toLowerCase(Locale.ROOT).contains("submission")) {
            ViewUtils.visibleViews(submissionsBinding.noSubmissionLayout);
            return;
        }
        if (getContext() != null) NotifyUtils.showToast(getContext(),getString(R.string.went_wrong_message));
    }


    /**
     * ---------------------------------------------------------------------------
     * Function implemented from the SubmissionCallbacks
     * @param _position - It is the position of the clicked item in the recyclerview
     */
    @Override
    public void onClick(int _position) {
        List<Submission> lists = viewModel.getAllSubmissions();
        Submission sub = null;
        if (lists != null && !lists.isEmpty()) {
            sub = lists.get(_position);
        }

        if (sub != null) viewModel.setSubmission(sub);
        navController.navigate(R.id.action_submissionsFragment_to_submissionFragment);
    }



    // Overriding function to handle the destroy of view

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        submissionsBinding = null;
    }
}
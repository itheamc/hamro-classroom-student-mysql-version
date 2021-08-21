package com.itheamc.hamroclassroom_student.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.itheamc.hamroclassroom_student.adapters.ClassesAdapter;
import com.itheamc.hamroclassroom_student.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_student.callbacks.SubjectCallbacks;
import com.itheamc.hamroclassroom_student.databinding.FragmentClassesBinding;
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

import java.util.List;


public class ClassesFragment extends Fragment implements SubjectCallbacks, QueryCallbacks {
    private static final String TAG = "ClassesFragment";
    private FragmentClassesBinding classesBinding;
    private NavController navController;
    private MainViewModel viewModel;
    private ClassesAdapter classesAdapter;

    /*
    Boolean Variable
     */
    private boolean isFetching = false;


    public ClassesFragment() {
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
        classesBinding = FragmentClassesBinding.inflate(inflater, container, false);
        return classesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and ViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);


        classesAdapter = new ClassesAdapter(this);
        classesBinding.recyclerView.setAdapter(classesAdapter);

        // Calling function to checks and load data
        checksUser();

        // Handling back button icon
        classesBinding.backButton.setOnClickListener(v -> {
            navController.popBackStack();
        });

        // Handling add button
        classesBinding.addSubjectButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_classesFragment_to_subjectsFragment);
        });

        /*
        Setting OnRefreshListener on the swipe-refresh layout
         */
        classesBinding.classesSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isFetching) return;
            viewModel.setSubjects(null);
            checksUser();

        });
    }

    /**
     * Function to checks whether the user is already stored in viewmodel or not
     */
    private void checksUser() {
        User user = viewModel.getUser();
        if (user == null) {
            if (getActivity() == null) return;
            isFetching = true;
            QueryHandler.getInstance(this).getUser(LocalStorage.getInstance(getActivity()).getUserId());
            showProgress();
            return;
        }
        retrieveSubjects();
    }

    /**
     * Function to get the subjects list according to the student's class and school
     */
    private void retrieveSubjects() {
        User user = viewModel.getUser();
        List<Subject> subjects = viewModel.getSubjects();
        if (subjects != null && !subjects.isEmpty()) {
            hideProgress();
            classesAdapter.submitList(Subject.filterSubjects(subjects));
            isFetching = false;
            return;
        }

        isFetching = true;
        QueryHandler.getInstance(this).getSubjects(user.get_school_ref(), user.get_class());
        showProgress();
    }


    /**
     * Function to show progressbar
     */
    private void showProgress() {
        ViewUtils.showProgressBar(classesBinding.classesOverlayLayLayout);
    }

    /**
     * Function to hide progressbar
     */
    private void hideProgress() {
        ViewUtils.handleRefreshing(classesBinding.classesSwipeRefreshLayout);
        ViewUtils.hideProgressBar(classesBinding.classesOverlayLayLayout);
    }


    /**
     * -----------------------------------------------------------------------------
     * These are the methods overrided from the SubjectCallbacks
     *
     * @param _position - It is the position of the clicked item
     */

    @Override
    public void onClick(int _position) {

    }

    @Override
    public void onJoinClassClick(int _position) {
        List<Subject> subjects = Subject.filterSubjects(viewModel.getSubjects());
        Subject subject = subjects.get(_position);
        String joinUrl = null;
        if (subject == null) return;

        joinUrl = subject.get_join_url();

        if (joinUrl == null || joinUrl.isEmpty()) return;

        if (!joinUrl.contains("https")) joinUrl = "https://" + joinUrl;

        Uri uri = Uri.parse(joinUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onCopyClick(int _position) {
        // Gets a handle to the clipboard service.
        if (getActivity() == null) return;

        List<Subject> subjects = Subject.filterSubjects(viewModel.getSubjects());
        Subject subject = subjects.get(_position);
        String joinUrl = null;
        if (subject == null) return;

        joinUrl = subject.get_join_url();

        if (joinUrl == null || joinUrl.isEmpty()) return;

        if (!joinUrl.contains("https")) joinUrl = "https://" + joinUrl;

        // Link Code
        String linkCode = joinUrl;
        if (joinUrl.contains("google")) linkCode = linkCode.substring(24);

        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("link-code", linkCode);
        clipboard.setPrimaryClip(clip);
        NotifyUtils.showToast(getContext(), "Copied");
    }

    @Override
    public void onLongClick(int _position) {

    }

    @Override
    public void onAddClick(int _position) {

    }


    /**
     * -------------------------------------------------------------------------
     * These are the methods overrided from the QueryCallbacks
     */
    @Override
    public void onQuerySuccess(List<User> users, List<School> schools, List<Teacher> teachers, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Notice> notices) {
        if (classesBinding == null) return;

        // If Subjects retrieve from the firestore
        if (subjects != null) {
            handleSubjects(subjects);
        }

        hideProgress();
    }

    @Override
    public void onQuerySuccess(User user, School school, Teacher teacher, Subject subject, Assignment assignment, Submission submission, Notice notice) {
        if (classesBinding == null) return;

        // If User retrieved from the Firestore
        if (user != null) {
            viewModel.setUser(user);
            retrieveSubjects();
        }
    }

    @Override
    public void onQuerySuccess(String message) {

    }

    @Override
    public void onQueryFailure(Exception e) {
        if (classesBinding == null) return;
        if (getContext() != null) NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        hideProgress();
    }


    /**
     * ----------------------------------------------------------------------
     * Function to handle subjects data
     */
    private void handleSubjects(List<Subject> subjects) {
        if (subjects == null) {
            hideProgress();
            return;
        }

        User u = viewModel.getUser();
        List<Subject> processedSubjects = Subject.processedSubjects(subjects, u);
        viewModel.setSubjects(processedSubjects);
        classesAdapter.submitList(Subject.filterSubjects(processedSubjects));
        isFetching = false;
        hideProgress();
    }


    /*
    This is the method overrided to handle the view destroy
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        classesBinding = null;
    }
}
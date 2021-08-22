package com.itheamc.hamroclassroom_student.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheamc.hamroclassroom_student.R;
import com.itheamc.hamroclassroom_student.adapters.SubjectAdapter;
import com.itheamc.hamroclassroom_student.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_student.callbacks.SubjectCallbacks;
import com.itheamc.hamroclassroom_student.databinding.FragmentSubjectsBinding;
import com.itheamc.hamroclassroom_student.handlers.QueryHandler;
import com.itheamc.hamroclassroom_student.models.Assignment;
import com.itheamc.hamroclassroom_student.models.Notice;
import com.itheamc.hamroclassroom_student.models.School;
import com.itheamc.hamroclassroom_student.models.Subject;
import com.itheamc.hamroclassroom_student.models.Submission;
import com.itheamc.hamroclassroom_student.models.Teacher;
import com.itheamc.hamroclassroom_student.models.User;
import com.itheamc.hamroclassroom_student.models.UserSubject;
import com.itheamc.hamroclassroom_student.utils.IdGenerator;
import com.itheamc.hamroclassroom_student.utils.LocalStorage;
import com.itheamc.hamroclassroom_student.utils.NotifyUtils;
import com.itheamc.hamroclassroom_student.utils.ViewUtils;
import com.itheamc.hamroclassroom_student.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;


public class SubjectsFragment extends Fragment implements SubjectCallbacks, QueryCallbacks {
    private static final String TAG = "SubjectsFragment";
    private FragmentSubjectsBinding subjectsBinding;
    private NavController navController;
    private MainViewModel viewModel;
    private SubjectAdapter subjectAdapter;
    private UserSubject userSubject;
    private Subject subject;

    private String _message = "";
    private boolean is_processing = false;


    public SubjectsFragment() {
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
        subjectsBinding = FragmentSubjectsBinding.inflate(inflater, container, false);
        return subjectsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and ViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        subjectAdapter = new SubjectAdapter(this);
        subjectsBinding.subjectsRecyclerView.setAdapter(subjectAdapter);

        // Handling back button icon
        subjectsBinding.backButton.setOnClickListener(v -> {
            navController.popBackStack();
        });

        // Implementing swipe refresh listener
        subjectsBinding.subjectsSwipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.setSubjects(null);
            viewModel.setUser(null);
            retrieveSubjects();
        });

        // Calling function to processed subject lists
        checkSubjects();


    }

    /**
     * ----------------------------------------------------------------------
     * Function to process the subjects data
     */
    private void checkSubjects() {
        List<Subject> subjects = viewModel.getSubjects();

        if (subjects == null) {
            retrieveSubjects();
            return;
        }

        subjectAdapter.submitList(subjects);
    }

    /**
     * Function to get the subjects list according to the student's class and school
     */
    private void retrieveSubjects() {
        User user = viewModel.getUser();
        if (user == null) {
            retrieveUser();
            return;
        }
        QueryHandler.getInstance(this).getSubjects(user.get_school_ref(), user.get_class());
        showProgress();
    }

    /**
     * Function to retrieve user
     */
    private void retrieveUser() {
        String userId = null;
        if (getActivity() != null) userId = LocalStorage.getInstance(getActivity()).getUserId();
        if (userId != null) {
            QueryHandler.getInstance(this).getUser(userId);
            showProgress();
        }
    }


    /**
     * Function to show progressbar
     */
    private void showProgress() {
        ViewUtils.showProgressBar(subjectsBinding.subjectsOverlayLayLayout);
    }

    /**
     * Function to hide progressbar
     */
    private void hideProgress() {
        ViewUtils.handleRefreshing(subjectsBinding.subjectsSwipeRefreshLayout);
        ViewUtils.hideProgressBar(subjectsBinding.subjectsOverlayLayLayout);
    }



    /**
     * -----------------------------------------------------------------------------
     * These are the methods overrided from the SubjectCallbacks
     * @param _position - It is the position of the clicked item
     */
    @Override
    public void onClick(int _position) {

    }

    @Override
    public void onJoinClassClick(int _position) {

    }

    @Override
    public void onCopyClick(int _position) {

    }

    @Override
    public void onLongClick(int _position) {

    }

    @Override
    public void onAddClick(int _position) {
        handleAddRemove(_position);
    }

    /**
     * Function to handle onAddClick() event
     */
    private void handleAddRemove(int _position) {
        if (is_processing) return;
        List<Subject> subjects = viewModel.getSubjects();
        if (subjects == null || subjects.isEmpty()) return;

        subject = subjects.get(_position);
        is_processing = true;
        if (subject.is_added()) {
            subject.set_added(false);
            removeSubject(subject.get_id());
        } else {
            subject.set_added(true);
            addSubject(subject.get_id());
        }

    }

    // Function to remove subjects from UserSubjects
    private void removeSubject(String _id) {
        _message = "Removed";
        User user = viewModel.getUser();
        List<UserSubject> userSubjects = user.get_subjects();
        if (userSubjects == null || userSubjects.isEmpty()) {
            is_processing = false;
            return;
        }
        for (UserSubject sub: userSubjects) {
            if (sub.get_subject().equals(_id)) userSubject = sub;
        }
        if (userSubject != null) QueryHandler.getInstance(this).removeSubjectToUser(userSubject.get_id());
    }

    // Function to remove subjects from UserSubjects
    private void addSubject(String _id) {
        _message = "Added";
        User user = viewModel.getUser();
        userSubject = new UserSubject(
                IdGenerator.generateRandomId(),
                user.get_id(),
                _id
        );
        QueryHandler.getInstance(this).addSubjectToUser(userSubject);
    }


    /**
     * -------------------------------------------------------------------------
     * These are the methods overrided from the QueryCallbacks
     */

    @Override
    public void onQuerySuccess(List<User> users, List<School> schools, List<Teacher> teachers, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Notice> notices) {
        if (subjectsBinding == null) return;
        // If Subjects retrieve from the firestore
        if (subjects != null) {
            handleSubjects(subjects);
        }
        hideProgress();
    }

    @Override
    public void onQuerySuccess(User user, School school, Teacher teacher, Subject subject, Assignment assignment, Submission submission, Notice notice) {
        if (subjectsBinding == null) return;
        // If User retrieved from the Firestore
        if (user != null) {
            viewModel.setUser(user);
            retrieveSubjects();
        }
    }

    @Override
    public void onQuerySuccess(String message) {
        if (subjectsBinding == null) return;
        if (message.equals("Not found")) {
            ViewUtils.visibleViews(subjectsBinding.noSubjectsLayout);
            hideProgress();
        } else if (message.equals("success")){
            User user = viewModel.getUser();
            List<UserSubject> userSubjects = user.get_subjects();
            if (_message.equals("Added")) {
                userSubjects.add(userSubject);
            } else {
                userSubjects.remove(userSubject);
            }
            user.set_subjects(userSubjects);
            viewModel.replaceSubject(subject);
            viewModel.setUser(user);
            is_processing = false;
            userSubject = null;
            checkSubjects();
            if (getContext() != null) NotifyUtils.showToast(getContext(), _message);
        } else {
            hideProgress();
        }

    }

    @Override
    public void onQueryFailure(Exception e) {
        if (subjectsBinding == null) return;
        if (getContext() != null) NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        hideProgress();
    }

    /**
     * ----------------------------------------------------------------------
     * Function to process the subjects data
     */
    private void handleSubjects(List<Subject> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            ViewUtils.visibleViews(subjectsBinding.noSubjectsLayout);
            hideProgress();
            return;
        }

        User u = viewModel.getUser();
        List<Subject> processedSubjects = Subject.processedSubjects(subjects, u);
        viewModel.setSubjects(processedSubjects);
        subjectAdapter.submitList(processedSubjects);
        hideProgress();
    }

}
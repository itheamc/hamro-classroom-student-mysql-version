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
import com.itheamc.hamroclassroom_student.adapters.AssignmentAdapter;
import com.itheamc.hamroclassroom_student.callbacks.AssignmentCallbacks;
import com.itheamc.hamroclassroom_student.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_student.databinding.FragmentAssignmentsBinding;
import com.itheamc.hamroclassroom_student.handlers.QueryHandler;
import com.itheamc.hamroclassroom_student.models.Assignment;
import com.itheamc.hamroclassroom_student.models.Notice;
import com.itheamc.hamroclassroom_student.models.School;
import com.itheamc.hamroclassroom_student.models.Subject;
import com.itheamc.hamroclassroom_student.models.Submission;
import com.itheamc.hamroclassroom_student.models.Teacher;
import com.itheamc.hamroclassroom_student.models.User;
import com.itheamc.hamroclassroom_student.models.UserSubject;
import com.itheamc.hamroclassroom_student.utils.LocalStorage;
import com.itheamc.hamroclassroom_student.utils.NotifyUtils;
import com.itheamc.hamroclassroom_student.utils.ViewUtils;
import com.itheamc.hamroclassroom_student.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;


public class AssignmentsFragment extends Fragment implements QueryCallbacks, AssignmentCallbacks {
    private static final String TAG = "AssignmentsFragment";
    private FragmentAssignmentsBinding assignmentsBinding;
    private MainViewModel viewModel;
    private NavController navController;
    private AssignmentAdapter assignmentAdapter;

    private List<Assignment> listOfAssignments;

    public AssignmentsFragment() {
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
        assignmentsBinding = FragmentAssignmentsBinding.inflate(inflater, container, false);
        return assignmentsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and ViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing AssignmentAdapter and setting to recyclerview
        assignmentAdapter = new AssignmentAdapter(this);
        assignmentsBinding.assignmentsRecyclerView.setAdapter(assignmentAdapter);

        // Initializing list
        listOfAssignments = new ArrayList<>();


        // Setting swipe and refresh layout
        assignmentsBinding.assignmentsSwipeRefreshLayout.setOnRefreshListener(() -> {
            listOfAssignments = new ArrayList<>();
            viewModel.setAllAssignments(null);
            ViewUtils.hideViews(assignmentsBinding.noAssignmentsLayout);
            checksUser();
        });

        // Handling back button
        assignmentsBinding.backButton.setOnClickListener(v -> {
            navController.popBackStack();
        });



        // Checks User for assignment extraction
        checksUser();
    }


    /**
     * Function to checks whether the user is already stored in viewmodel or not
     */
    private void checksUser() {
        User user = viewModel.getUser();
        if (user == null) {
            if (getActivity() != null) {
                QueryHandler.getInstance(this).getUser(LocalStorage.getInstance(getActivity()).getUserId());
                ViewUtils.showProgressBar(assignmentsBinding.assignmentsOverlayLayLayout);
            }
            return;
        }

        retrieveAssignments();
        ViewUtils.showProgressBar(assignmentsBinding.assignmentsOverlayLayLayout);
    }

    /**
     * Function to retrieve assignments
     */
    private void retrieveAssignments() {
        User user = viewModel.getUser();
        if (user != null) QueryHandler.getInstance(this).getAssignments(user.get_id(), user.get_school_ref(), user.get_class());
    }

    /**
     * ----------------------------------------------------------------------
     * Function to handle subjects data
     */
    private void handleAssignments(List<Assignment> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            ViewUtils.visibleViews(assignmentsBinding.noAssignmentsLayout);
            hideProgress();
            return;
        }

        User user = viewModel.getUser();
        List<UserSubject> userSubjects = user.get_subjects();

        for (Assignment assignment: assignments) {
            if (!isAddable(userSubjects, assignment.get_subject_ref())) continue;
            listOfAssignments.add(assignment);
        }

        viewModel.setAllAssignments(listOfAssignments);
        assignmentAdapter.submitList(listOfAssignments);
        hideProgress();

    }

    /**
    Function to check if assignment is from the user subject or not
     */
    private boolean isAddable(List<UserSubject> userSubjects, String _subject_ref) {
        boolean isAddable = false;
        for (UserSubject userSubject: userSubjects) {
            if (!userSubject.get_subject().equals(_subject_ref)) continue;
            isAddable = true;
        }

        return isAddable;
    }


    /*
    Handle views
     */
    private void hideProgress() {
        ViewUtils.hideProgressBar(assignmentsBinding.assignmentsOverlayLayLayout);
        ViewUtils.handleRefreshing(assignmentsBinding.assignmentsSwipeRefreshLayout);
    }


    /**
     * -----------------------------------------------------------------------------
     * These are the methods implemented from the AssignmentCallbacks
     *
     * @param _position - It is the position of the clicked item in the list
     */
    @Override
    public void onSubmissionsClick(int _position) {
        List<Assignment> lists = viewModel.getAllAssignments();
        Assignment ass = null;
        if (lists != null && !lists.isEmpty()) {
            ass = lists.get(_position);
        }

        if (ass != null) viewModel.setAssignment(ass);
        navController.navigate(R.id.action_assignmentsFragment_to_submitFragment);
    }

    @Override
    public void onClick(int _position) {
        List<Assignment> lists = viewModel.getAllAssignments();
        Assignment ass = null;
        if (lists != null && !lists.isEmpty()) {
            ass = lists.get(_position);
        }

        if (ass != null) viewModel.setAssignment(ass);
        navController.navigate(R.id.action_assignmentsFragment_to_assignmentFragment);
    }

    @Override
    public void onLongClick(int _position) {

    }

    /**
     * -----------------------------------------------------------------------------
     * These are the methods implemented from the QueryCallbacks
     */

    @Override
    public void onQuerySuccess(List<User> users, List<School> schools, List<Teacher> teachers, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Notice> notices) {
        if (assignmentsBinding == null) return;

        // If Assignment is retrieved
        if (assignments != null) {
            handleAssignments(assignments);
        }
    }

    @Override
    public void onQuerySuccess(User user, School school, Teacher teacher, Subject subject, Assignment assignment, Submission submission, Notice notice) {
        if (assignmentsBinding == null) return;

        // If User retrieved from the Firestore
        if (user != null) {
            viewModel.setUser(user);
            retrieveAssignments();
        }
    }

    @Override
    public void onQuerySuccess(String message) {

    }

    @Override
    public void onQueryFailure(Exception e) {
        if (assignmentsBinding == null) return;
        hideProgress();
        if (e.getMessage() == null) return;
        if (getContext() != null) NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
    }



    // Overriding function to handle the destroy of view

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        assignmentsBinding = null;
    }


}
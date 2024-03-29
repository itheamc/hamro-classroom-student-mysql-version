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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.itheamc.hamroclassroom_student.R;
import com.itheamc.hamroclassroom_student.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_student.databinding.FragmentHomeBinding;
import com.itheamc.hamroclassroom_student.handlers.QueryHandler;
import com.itheamc.hamroclassroom_student.models.Assignment;
import com.itheamc.hamroclassroom_student.models.Material;
import com.itheamc.hamroclassroom_student.models.Notice;
import com.itheamc.hamroclassroom_student.models.School;
import com.itheamc.hamroclassroom_student.models.Subject;
import com.itheamc.hamroclassroom_student.models.Submission;
import com.itheamc.hamroclassroom_student.models.Teacher;
import com.itheamc.hamroclassroom_student.models.User;
import com.itheamc.hamroclassroom_student.utils.LocalStorage;
import com.itheamc.hamroclassroom_student.utils.NotifyUtils;
import com.itheamc.hamroclassroom_student.utils.OtherUtils;
import com.itheamc.hamroclassroom_student.utils.ViewUtils;
import com.itheamc.hamroclassroom_student.viewmodel.MainViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class HomeFragment extends Fragment implements QueryCallbacks, View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding homeBinding;
    private MainViewModel viewModel;
    private NavController navController;

    public HomeFragment() {
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
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return homeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and ViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Setting greeting label text
        homeBinding.greetingLabel.setText(OtherUtils.showGreeting());


        // Setting OnClickListener on Views
        homeBinding.userIconCardView.setOnClickListener(this);
        homeBinding.classesCardView.setOnClickListener(this);
        homeBinding.assignmentCardView.setOnClickListener(this);
        homeBinding.submissionsCardView.setOnClickListener(this);
        homeBinding.noticesCardView.setOnClickListener(this);
        homeBinding.materialsCardView.setOnClickListener(this);

        // OnRefresh Listener on Swipe Refresh Layout
        homeBinding.homeSwipeRefreshLayout.setOnRefreshListener(this::retrieveUser);

        // Retrieving user
        if (viewModel.getUser() != null) {
            setUserData(viewModel.getUser());
        } else {
            retrieveUser();
        }
    }

    /**
     * Function to retrieve user
     */
    private void retrieveUser() {
        String userId = null;
        if (getActivity() != null) userId = LocalStorage.getInstance(getActivity()).getUserId();
        if (userId != null) QueryHandler.getInstance(this).getUser(userId);
    }

    /**
     * Function to pass data on the views
     */
    private void setUserData(User user) {
        Picasso.get().load(user.get_image())
                .into(homeBinding.userIcon);
        homeBinding.userName.setText(user.get_name().split(" ")[0]);
    }


    /**
     * -----------------------------------------------------------------------------------
     * This is the method overrided from the View.OnClickListener to listen the click event
     * @param v - It is the instance of the view got clicked
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == homeBinding.userIconCardView.getId()) {
            navigateTo(R.id.action_homeFragment_to_profileFragment);
        } else if (id == homeBinding.classesCardView.getId()) {
            navigateTo(R.id.action_homeFragment_to_classesFragment);
        } else if (id == homeBinding.assignmentCardView.getId()) {
            navigateTo(R.id.action_homeFragment_to_assignmentsFragment);
        } else if (id == homeBinding.submissionsCardView.getId()) {
            navigateTo(R.id.action_homeFragment_to_submissionsFragment);
        } else if (id == homeBinding.noticesCardView.getId()) {
            navigateTo(R.id.action_homeFragment_to_noticesFragment);
        } else if (id == homeBinding.materialsCardView.getId()) {
            navigateTo(R.id.action_homeFragment_to_materialsFragment);
        } else {
            NotifyUtils.logDebug(TAG, "Unspecified view is clicked");
        }
    }

    /*
    Function to handle navigation
     */
    private void navigateTo(int actionId) {
        navController.navigate(actionId);
    }


    /**
     * -------------------------------------------------------------------------
     * These are the methods overrided from the QueryCallbacks
     */
    @Override
    public void onQuerySuccess(List<User> users, List<School> schools, List<Teacher> teachers, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Material> materials, List<Notice> notices) {

    }

    @Override
    public void onQuerySuccess(User user, School school, Teacher teacher, Subject subject, Assignment assignment, Submission submission, Material material, Notice notice) {
        if (homeBinding == null) return;
        // If User retrieved from the Firestore
        if (user != null) {
            viewModel.setUser(user);
            viewModel.setSchool(user.get_school());
            setUserData(user);
        }
        ViewUtils.handleRefreshing(homeBinding.homeSwipeRefreshLayout);
    }

    @Override
    public void onQuerySuccess(String message) {
        if (homeBinding == null) return;
        ViewUtils.handleRefreshing(homeBinding.homeSwipeRefreshLayout);
    }

    @Override
    public void onQueryFailure(Exception e) {
        if (homeBinding == null) return;
        if (getContext() != null) NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        ViewUtils.handleRefreshing(homeBinding.homeSwipeRefreshLayout);

    }
}
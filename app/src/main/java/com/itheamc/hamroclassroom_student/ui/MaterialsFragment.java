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
import com.itheamc.hamroclassroom_student.adapters.MaterialAdapter;
import com.itheamc.hamroclassroom_student.callbacks.MaterialCallbacks;
import com.itheamc.hamroclassroom_student.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_student.databinding.FragmentMaterialsBinding;
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
import com.itheamc.hamroclassroom_student.utils.ViewUtils;
import com.itheamc.hamroclassroom_student.viewmodel.MainViewModel;

import java.util.List;

public class MaterialsFragment extends Fragment implements QueryCallbacks, MaterialCallbacks {
    private static final String TAG = "MaterialsFragment";
    private FragmentMaterialsBinding materialsBinding;
    private NavController navController;
    private MainViewModel viewModel;
    private MaterialAdapter materialAdapter;

    public MaterialsFragment() {
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
        materialsBinding = FragmentMaterialsBinding.inflate(inflater, container, false);
        return materialsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing ViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing Adapter
        materialAdapter = new MaterialAdapter(this);
        materialsBinding.materialsRecyclerView.setAdapter(materialAdapter);

        materialsBinding.backButton.setOnClickListener(v -> {
            navController.popBackStack();
        });

         /*
        Setting OnRefreshListener on the swipe-refresh layout
         */
        materialsBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            ViewUtils.hideViews(materialsBinding.noItemFoundLayout);
            getMaterials();
        });

        /*
        Getting assignments from cloud
         */
        checkMaterials();
    }

    /*
    Function to check submissions in the ViewModel
     */
    private void checkMaterials() {
        List<Material> materials = viewModel.getMaterials();
        if (materials != null) {
            submitListToAdapter(materials);
            return;
        }

        getMaterials();
    }


    /*
    Function to get subjects
     */
    private void getMaterials() {
        if (getActivity() == null) return;
        QueryHandler.getInstance(this).getMaterials(LocalStorage.getInstance(getActivity()).getUserId());
        if (!materialsBinding.swipeRefreshLayout.isRefreshing()) showProgress();
    }


    /*
    Function to submit List<Submission> to the SubmissionAdapter
     */
    private void submitListToAdapter(List<Material> materials) {
        if (materials.size() == 0) {
            ViewUtils.visibleViews(materialsBinding.noItemFoundLayout);
            return;
        }
        viewModel.setMaterials(materials);
        materialAdapter.submitList(materials);
    }

    /*
    Function to show progress
     */
    private void showProgress() {
        ViewUtils.showProgressBar(materialsBinding.progressBarContainer);
    }

    /*
    Function to hide progress
     */
    private void hideProgress() {
        ViewUtils.hideProgressBar(materialsBinding.progressBarContainer);
        ViewUtils.handleRefreshing(materialsBinding.swipeRefreshLayout);
    }

    /**
     * -------------------------------------------------------------------------------
     * Method implemented from the MaterialCallbacks
     */
    @Override
    public void onClick(int _position) {
        setMaterial(_position);
        navController.navigate(R.id.action_materialsFragment_to_materialFragment);
    }

    // Custom function to set assignment in ViewModel
    private void setMaterial(int _position) {
        Material material = null;
        if (viewModel.getMaterials() != null && !viewModel.getMaterials().isEmpty())
            material = viewModel.getMaterials().get(_position);

        if (material != null) viewModel.setMaterial(material);
    }

    /**
     *---------------------------------------------------------------------------------
     * These are the functions overrided from the QueryCallbacks
     */

    @Override
    public void onQuerySuccess(List<User> users, List<School> schools, List<Teacher> teachers, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Material> materials, List<Notice> notices) {
        if (materialsBinding == null) return;

        if (materials != null) {
            if (materials.size() == 0) {
                ViewUtils.visibleViews(materialsBinding.noItemFoundLayout);
                return;
            }

            viewModel.setMaterials(materials);
            checkMaterials();
        }

        hideProgress();

    }

    @Override
    public void onQuerySuccess(User user, School school, Teacher teacher, Subject subject, Assignment assignment, Submission submission, Material material, Notice notice) {

    }

    @Override
    public void onQuerySuccess(String message) {
        if (materialsBinding == null) return;

        hideProgress();
        if (message.equals("Not found")) ViewUtils.visibleViews(materialsBinding.noItemFoundLayout);
        if (getContext() != null && !message.equals("Not found")) NotifyUtils.showToast(getContext(), message);
    }

    @Override
    public void onQueryFailure(Exception e) {
        if (materialsBinding == null) return;
        hideProgress();
        if (getContext() != null)
            NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        NotifyUtils.logError(TAG, "onFailure: ", e);
    }

    /*
    Overrided method to handle view destroy
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        materialsBinding = null;
    }

}
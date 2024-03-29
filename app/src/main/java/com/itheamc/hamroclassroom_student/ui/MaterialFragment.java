package com.itheamc.hamroclassroom_student.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.itheamc.hamroclassroom_student.adapters.SliderAdapter;
import com.itheamc.hamroclassroom_student.databinding.FragmentMaterialBinding;
import com.itheamc.hamroclassroom_student.models.Material;
import com.itheamc.hamroclassroom_student.utils.ArrayUtils;
import com.itheamc.hamroclassroom_student.viewmodel.MainViewModel;


public class MaterialFragment extends Fragment {
    private static final String TAG = "MaterialFragment";
    private FragmentMaterialBinding materialBinding;

    public MaterialFragment() {
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
        materialBinding = FragmentMaterialBinding.inflate(inflater, container, false);
        return materialBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing ViewModel
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing slider adapter...
        SliderAdapter sliderAdapter = new SliderAdapter();
        materialBinding.materialViewPager.setAdapter(sliderAdapter);

        Material material = viewModel.getMaterial();
        if (material != null) {
            materialBinding.setMaterial(material);
            if (material.get_images().length > 0)
                sliderAdapter.submitList(ArrayUtils.asList(material.get_images()));
        }
    }


    // View Destroy
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        materialBinding = null;
    }
}
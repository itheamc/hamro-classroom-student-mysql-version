package com.itheamc.hamroclassroom_student.adapters;

import static com.itheamc.hamroclassroom_student.models.Material.materialItemCallback;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.itheamc.hamroclassroom_student.R;
import com.itheamc.hamroclassroom_student.callbacks.MaterialCallbacks;
import com.itheamc.hamroclassroom_student.databinding.MaterialViewBinding;
import com.itheamc.hamroclassroom_student.models.Material;
import com.itheamc.hamroclassroom_student.utils.NotifyUtils;
import com.itheamc.hamroclassroom_student.utils.TimeUtils;

import java.text.DateFormat;

public class MaterialAdapter extends ListAdapter<Material, MaterialAdapter.MaterialViewHolder> {
    private static final String TAG = "MaterialAdapter";
    private final MaterialCallbacks callbacks;

    public MaterialAdapter(@NonNull MaterialCallbacks materialCallbacks) {
        super(materialItemCallback);
        this.callbacks = materialCallbacks;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MaterialViewBinding materialViewBinding = MaterialViewBinding.inflate(inflater, parent, false);
        return new MaterialViewHolder(materialViewBinding, callbacks);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        Material material = getItem(position);
        holder.viewBinding.setMaterial(material);
        holder.viewBinding.setNumber(String.valueOf(position + 1));
        String formattedDate = DateFormat.getDateInstance().format(TimeUtils.toDate(material.get_added_date()));
        holder.viewBinding.setDate(formattedDate);
    }

    public static class MaterialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialCallbacks callbacks;
        private final MaterialViewBinding viewBinding;

        public MaterialViewHolder(@NonNull MaterialViewBinding materialViewBinding, @NonNull MaterialCallbacks materialCallbacks) {
            super(materialViewBinding.getRoot());
            this.callbacks = materialCallbacks;
            this.viewBinding = materialViewBinding;
            this.viewBinding.materialCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int _id = v.getId();
            if (_id == viewBinding.materialCardView.getId())
                callbacks.onClick(getAdapterPosition());
            else NotifyUtils.logDebug(TAG, "Unspecified view is clicked!!");
        }
    }
}

package com.itheamc.hamroclassroom_student.models;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DiffUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Subject {
    private String _id;
    private String _name;
    private String _class;
    private String _teacher_ref;
    private Teacher _teacher;
    private String _school_ref;
    private School _school;
    private String _join_url;
    private String _start_time;
    private String _added_on;
    private boolean _hidden;
    private boolean _added;

    // Constructor
    public Subject() {
    }

    // Constructor with parameters
    public Subject(String _id, String _name, String _class, String _teacher_ref, Teacher _teacher, String _school_ref, School _school, String _join_url, String _start_time, String _added_on, boolean _hidden, boolean _added) {
        this._id = _id;
        this._name = _name;
        this._class = _class;
        this._teacher_ref = _teacher_ref;
        this._teacher = _teacher;
        this._school_ref = _school_ref;
        this._school = _school;
        this._join_url = _join_url;
        this._start_time = _start_time;
        this._added_on = _added_on;
        this._hidden = _hidden;
        this._added = _added;
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String get_teacher_ref() {
        return _teacher_ref;
    }

    public void set_teacher_ref(String _teacher_ref) {
        this._teacher_ref = _teacher_ref;
    }

    public Teacher get_teacher() {
        return _teacher;
    }

    public void set_teacher(Teacher _teacher) {
        this._teacher = _teacher;
    }

    public String get_school_ref() {
        return _school_ref;
    }

    public void set_school_ref(String _school_ref) {
        this._school_ref = _school_ref;
    }

    public School get_school() {
        return _school;
    }

    public void set_school(School _school) {
        this._school = _school;
    }

    public String get_join_url() {
        return _join_url;
    }

    public void set_join_url(String _join_url) {
        this._join_url = _join_url;
    }

    public String get_start_time() {
        return _start_time;
    }

    public void set_start_time(String _start_time) {
        this._start_time = _start_time;
    }

    public String get_added_on() {
        return _added_on;
    }

    public void set_added_on(String _added_on) {
        this._added_on = _added_on;
    }

    public boolean is_hidden() {
        return _hidden;
    }

    public void set_hidden(boolean _hidden) {
        this._hidden = _hidden;
    }

    public boolean is_added() {
        return _added;
    }

    public void set_added(boolean _added) {
        this._added = _added;
    }

    // Overriding toString() method
    @Override
    public String toString() {
        return "Subject{" +
                "_id='" + _id + '\'' +
                ", _name='" + _name + '\'' +
                ", _class='" + _class + '\'' +
                ", _teacher_ref='" + _teacher_ref + '\'' +
                ", _teacher=" + _teacher +
                ", _school_ref='" + _school_ref + '\'' +
                ", _school=" + _school +
                ", _join_url='" + _join_url + '\'' +
                ", _start_time='" + _start_time + '\'' +
                ", _added_on='" + _added_on + '\'' +
                ", _hidden=" + _hidden +
                ", _added=" + _added +
                '}';
    }

    // Overriding equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return _hidden == subject._hidden &&
                _added == subject._added &&
                Objects.equals(_id, subject._id) &&
                Objects.equals(_name, subject._name) &&
                Objects.equals(_class, subject._class) &&
                Objects.equals(_teacher_ref, subject._teacher_ref) &&
                Objects.equals(_teacher, subject._teacher) &&
                Objects.equals(_school_ref, subject._school_ref) &&
                Objects.equals(_school, subject._school) &&
                Objects.equals(_join_url, subject._join_url) &&
                Objects.equals(_start_time, subject._start_time) &&
                Objects.equals(_added_on, subject._added_on);
    }



    // DiffUtil.ItemCallback
    public static DiffUtil.ItemCallback<Subject> subjectItemCallback = new DiffUtil.ItemCallback<Subject>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Subject oldItem, @NonNull @NotNull Subject newItem) {
            return newItem.equals(oldItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Subject oldItem, @NonNull @NotNull Subject newItem) {
            return false;
        }
    };


    /**
     * ----------------------------------------------------------------------
     * Function to process the subjects data
     */
    public static List<Subject> processedSubjects(List<Subject> subjects, User user) {
        List<String> subject_ids = null;
        List<Subject> processedSubjects = new ArrayList<>();


        return processedSubjects;
    }


    /**
     * Function to filter the subject that user has added
     * @param subjects - Lists of subjects
     * @return - It will return the subjects list after filtering it
     */
    public static List<Subject> filterSubjects(List<Subject> subjects) {
        List<Subject> filteredList = new ArrayList<>();
        for (Subject subject: subjects) {
            if (!subject.is_added()) continue;

            filteredList.add(subject);
        }

        return filteredList;
    }


    /**
     * Function to check if subject is already added
     * @param subjectIds - It is the list of subjects id
     * @param _id - It is the unique id of the subject
     * @return - true or false
     */
    public static boolean isAlreadyAdded(List<String> subjectIds, String _id) {
        boolean is_already_added = false;

        if (subjectIds == null || subjectIds.isEmpty()) return false;

        for (String s: subjectIds) {
            if (!_id.equals(s)) continue;

            is_already_added = true;
            break;
        }

        return is_already_added;
    }

    // Binding Adapters
    @BindingAdapter("android:imageSource")
    public static void setImage(ImageView imageView, String _imageUrl) {
//        Picasso.get()
//                .load(_imageUrl)
//                .into(imageView);
    }
}

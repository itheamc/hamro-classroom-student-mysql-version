package com.itheamc.hamroclassroom_student.handlers;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;

import com.itheamc.hamroclassroom_student.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_student.models.Assignment;
import com.itheamc.hamroclassroom_student.models.Notice;
import com.itheamc.hamroclassroom_student.models.School;
import com.itheamc.hamroclassroom_student.models.Subject;
import com.itheamc.hamroclassroom_student.models.Submission;
import com.itheamc.hamroclassroom_student.models.Teacher;
import com.itheamc.hamroclassroom_student.models.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueryHandler {
    private final Handler handler;
    private final QueryCallbacks callbacks;
    private final ExecutorService executorService;

    // Constructor
    public QueryHandler(@NonNull QueryCallbacks callbacks) {
        this.callbacks = callbacks;
        this.handler = HandlerCompat.createAsync(Looper.getMainLooper());
        this.executorService = Executors.newFixedThreadPool(4);
    }

    // Getter for instance
    public static QueryHandler getInstance(@NonNull QueryCallbacks callbacks) {
        return new QueryHandler(callbacks);
    }

    /**
     * Function to get user info from the cloud Firestore
     * --------------------------------------------------------------------------------------
     */
    public void getUser(String userId) {

    }


    /**
     * Function to store user in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void storeUser(User user) {

    }

    /**
     * Function to update user in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void updateUser(String _uid, Map<String, Object> data) {

    }

    /**
     * Function to add subject to user in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void addSubjectToUser(String _uid, String data) {

    }

    /**
     * Function to remove subject from user in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void removeSubjectToUser(String _uid, String data) {

    }


    /**
     * Function to add submission Id to user in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void addSubmissionToUser(String _uid, String submissionId) {

    }

    /**
     * Function to get subjects list from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getSubjects(String schoolId, String _class) {

    }


    /**
     * Function to get assignments list from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getAssignments(String subjectId) {

    }


    /**
     * Function to get assignment from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getAssignment(String assignment_ref) {

    }


    /**
     * Function to add submissions in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void addSubmission(Submission submission) {

    }


    /**
     * Function to update submission in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void updateSubmission(String subjectId, String assignmentId, String submissionId, Map<String, Object> data) {

    }


    /**
     * Function to get submission from the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void getSubmissions(String userId) {

    }


    /**
     * Function to get teacher from the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void getTeacher(String teacherId) {

    }


    /**
     * Function to get teachers
     */
    public void getTeachers(String _school_ref) {

    }

    /**
     * Function to get notices list from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getNotices(String schoolId, String _class) {

    }


    /**
     * Function to get schools list from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getSchools() {

    }

    /**
     * Function to get schools list from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getSchool(String _schoolId) {

    }


    /**
     * Function to notify whether getUser() is success or failure
     * --------------------------------------------------------------------------------------
     */
    private void notifyOnSuccess(User user,
                                 List<School> schools,
                                 List<Teacher> teachers,
                                 List<Subject> subjects,
                                 List<Assignment> assignments,
                                 List<Submission> submissions,
                                 List<Notice> notices) {
        handler.post(() -> {

        });
    }

    private void notifyOnFailure(Exception e) {
        handler.post(() -> {

        });
    }
}

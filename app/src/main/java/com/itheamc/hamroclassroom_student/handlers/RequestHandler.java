package com.itheamc.hamroclassroom_student.handlers;

import androidx.annotation.NonNull;

import com.itheamc.hamroclassroom_student.models.Assignment;
import com.itheamc.hamroclassroom_student.models.Notice;
import com.itheamc.hamroclassroom_student.models.School;
import com.itheamc.hamroclassroom_student.models.Teacher;
import com.itheamc.hamroclassroom_student.models.Subject;
import com.itheamc.hamroclassroom_student.models.Submission;
import com.itheamc.hamroclassroom_student.models.User;
import com.itheamc.hamroclassroom_student.models.UserSubject;
import com.itheamc.hamroclassroom_student.utils.Amcryption;
import com.itheamc.hamroclassroom_student.utils.ArrayUtils;
import com.itheamc.hamroclassroom_student.utils.TimeUtils;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestHandler {
     /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Schools
     */

    // GET REQUEST
    public static final Request GET_REQUEST_SCHOOLS = new Request.Builder().url(PathHandler.SCHOOLS_PATH).headers(AuthHandler.authHeaders(null, null)).get().build();

    // GET REQUEST
    public static Request schoolGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.SCHOOLS_PATH + _id).headers(AuthHandler.authHeaders(null, null)).get().build();
    }

    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Teachers
     */

    // GET REQUEST
    public static final Request GET_REQUEST_TEACHERS = new Request.Builder().url(PathHandler.TEACHERS_PATH).headers(AuthHandler.authHeaders(null, null)).get().build();

    // GET REQUEST
    public static Request teacherGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.TEACHERS_PATH + _id).headers(AuthHandler.authHeaders("id", null)).get().build();
    }

    // GET REQUEST
    public static Request teacherGetRequestBySchool(@NonNull String _schoolId) {
        return new Request.Builder().url(PathHandler.TEACHERS_PATH + _schoolId).headers(AuthHandler.authHeaders("school", null)).get().build();
    }


    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Students
     */

    // GET REQUEST
    public static final Request GET_REQUEST_STUDENTS = new Request.Builder().url(PathHandler.STUDENTS_PATH).headers(AuthHandler.authHeaders(null, null)).get().build();

    // GET REQUEST
    public static Request studentGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.STUDENTS_PATH + _id).headers(AuthHandler.authHeaders("id", null)).get().build();
    }

    // POST REQUEST
    public static Request studentPostRequest(@NonNull User student) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", student.get_id())
                .add("_name", Amcryption.getEncoder().encode(student.get_name()))
                .add("_gender", Amcryption.getEncoder().encode(student.get_gender()))
                .add("_image", student.get_image())
                .add("_phone", Amcryption.getEncoder().encode(student.get_phone()))
                .add("_email", Amcryption.getEncoder().encode(student.get_email()))
                .add("_address", Amcryption.getEncoder().encode(student.get_address()))
                .add("_guardian", Amcryption.getEncoder().encode(student.get_guardian()))
                .add("_class", student.get_class())
                .add("_section", student.get_section())
                .add("_roll_number", student.get_roll_number())
                .add("_school", student.get_school_ref())
                .add("_joined_on", student.get_joined_on())
                .build();

        return new Request.Builder().url(PathHandler.STUDENTS_PATH).headers(AuthHandler.authHeaders(null, null)).post(requestBody).build();
    }


     /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Student Subjects
     */

    // GET REQUEST
    public static Request studentSubjectsDeleteRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.STUDENTS_SUBJECT_PATH + _id).headers(AuthHandler.authHeaders("id", null)).delete().build();
    }

    // POST REQUEST
    public static Request studentSubjectsPostRequest(@NonNull UserSubject userSubject) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", userSubject.get_id())
                .add("_student", userSubject.get_user())
                .add("_subject", userSubject.get_subject())
                .build();

        return new Request.Builder().url(PathHandler.STUDENTS_SUBJECT_PATH).headers(AuthHandler.authHeaders(null, null)).post(requestBody).build();
    }



    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Subjects
     */

    // GET REQUEST
    public static final Request GET_REQUEST_SUBJECTS = new Request.Builder().url(PathHandler.SUBJECTS_PATH).headers(AuthHandler.authHeaders(null, null)).get().build();

    // GET REQUEST
    public static Request subjectGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.SUBJECTS_PATH + _id).headers(AuthHandler.authHeaders("id", null)).get().build();
    }

    // GET REQUEST
    public static Request subjectGetRequestByTeacherId(@NonNull String _teacherId) {
        return new Request.Builder().url(PathHandler.SUBJECTS_PATH + _teacherId).headers(AuthHandler.authHeaders("teacher", null)).get().build();
    }

    // GET REQUEST
    public static Request subjectGetRequestBySchoolIdAndClass(@NonNull String _schoolId, String _class) {
        return new Request.Builder().url(PathHandler.SUBJECTS_PATH + _schoolId + "___" + _class).headers(AuthHandler.authHeaders("school", null)).get().build();
    }


    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Assignments
     */

    // GET REQUEST
    public static final Request GET_REQUEST_ASSIGNMENTS = new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH).get().build();

    // GET REQUEST
    public static Request assignmentGetRequestById(@NonNull String _userId, @NonNull String _id) {
        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH + _id).headers(AuthHandler.authHeaders("id", _userId)).get().build();
    }

    // GET REQUEST
    public static Request assignmentGetRequestByTeacherId(@NonNull String _userId, @NonNull String _teacherId) {
        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH + _teacherId).headers(AuthHandler.authHeaders("teacher", _userId)).get().build();
    }

    // GET REQUEST
    public static Request assignmentGetRequestBySubjectId(@NonNull String _userId, @NonNull String _subjectId) {
        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH + _subjectId).headers(AuthHandler.authHeaders("subject", _userId)).get().build();
    }

    // GET REQUEST
    public static Request assignmentGetRequestBySchoolIdAndClass(@NonNull String _userId, @NonNull String _schoolId, @NonNull String _class) {
        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH + _schoolId + "___" + _class).headers(AuthHandler.authHeaders("school", _userId)).get().build();
    }

    // POST REQUEST
    public static Request assignmentPostRequest(@NonNull Assignment assignment) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", assignment.get_id())
                .add("_title", assignment.get_title())
                .add("_desc", assignment.get_desc())
                .add("_images", ArrayUtils.toString(assignment.get_images()))
                .add("_docs", ArrayUtils.toString(assignment.get_docs()))
                .add("_class", assignment.get_class())
                .add("_teacher", assignment.get_teacher_ref())
                .add("_subject", assignment.get_subject_ref())
                .add("_school", assignment.get_school_ref())
                .add("_assigned_date", assignment.get_assigned_date())
                .add("_due_date", assignment.get_due_date())
                .build();

        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH).headers(AuthHandler.authHeaders(null, null)).post(requestBody).build();
    }


    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Submissions
     */

    // GET REQUEST
    public static final Request GET_REQUEST_SUBMISSIONS = new Request.Builder().url(PathHandler.SUBMISSIONS_PATH).headers(AuthHandler.authHeaders(null, null)).get().build();

    // GET REQUEST
    public static Request submissionGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH + _id).headers(AuthHandler.authHeaders("id", null)).get().build();
    }

    // GET REQUEST
    public static Request submissionGetRequestByAssignmentId(@NonNull String _assignmentId) {
        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH + _assignmentId).headers(AuthHandler.authHeaders("assignment", null)).get().build();
    }

    // GET REQUEST
    public static Request submissionGetRequestByStudentId(@NonNull String _studentId) {
        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH + _studentId).headers(AuthHandler.authHeaders("student",null)).get().build();
    }


    // POST REQUEST
    public static Request submissionPostRequest(@NonNull Submission submission) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", submission.get_id())
                .add("_texts", submission.get_texts())
                .add("_images", ArrayUtils.toString(submission.get_images()))
                .add("_docs", ArrayUtils.toString(submission.get_docs()))
                .add("_assignment", submission.get_assignment_ref())
                .add("_student", submission.get_student_ref())
                .add("_submitted_date", submission.get_submitted_date())
                .add("_checked_date", submission.get_checked_date())
                .add("_checked", String.valueOf(submission.is_checked()))
                .add("_comment", submission.get_comment())
                .build();

        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH).headers(AuthHandler.authHeaders(null, null)).post(requestBody).build();
    }


    /*
   ------------------------------------------------------------------------------------------------
   ------------------------------------------------------------------------------------------------
   Requests for Notices
    */

    // GET REQUEST
    public static final Request GET_REQUEST_NOTICES = new Request.Builder().url(PathHandler.NOTICES_PATH).headers(AuthHandler.authHeaders(null, null)).get().build();

    // GET REQUEST
    public static Request noticeGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.NOTICES_PATH + _id).headers(AuthHandler.authHeaders("id", null)).get().build();
    }

    // GET REQUEST
    public static Request noticeGetRequestBySchoolAndClass(@NonNull String schoolId) {
        return new Request.Builder().url(PathHandler.NOTICES_PATH + schoolId).headers(AuthHandler.authHeaders("school", null)).get().build();
    }

    // POST REQUEST
    public static Request noticePostRequest(@NonNull Notice notice) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", notice.get_id())
                .add("_title", notice.get_title())
                .add("_desc", notice.get_desc())
                .add("_school", notice.get_school_ref())
                .add("_classes", ArrayUtils.toString(notice.get_classes()))
                .add("_teacher", notice.get_teacher_ref())
                .add("_notified_on", notice.get_notified_on())
                .build();

        return new Request.Builder().url(PathHandler.NOTICES_PATH).headers(AuthHandler.authHeaders(null, null)).post(requestBody).build();
    }


     /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Materials
     */

    // GET REQUEST
    public static final Request  GET_REQUEST_MATERIALS= new Request.Builder().url(PathHandler.MATERIALS_PATH).headers(AuthHandler.authHeaders(null, null)).get().build();

    // GET REQUEST
    public static Request materialsGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.MATERIALS_PATH + _id).headers(AuthHandler.authHeaders("id", null)).get().build();
    }

    // GET REQUEST
    public static Request materialsGetRequestByUserRef(@NonNull String _ref) {
        return new Request.Builder().url(PathHandler.MATERIALS_PATH + _ref).headers(AuthHandler.authHeaders("student", null)).get().build();
    }

}

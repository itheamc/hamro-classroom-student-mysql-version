package com.itheamc.hamroclassroom_student.handlers;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;

import com.itheamc.hamroclassroom_student.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_student.models.Assignment;
import com.itheamc.hamroclassroom_student.models.Material;
import com.itheamc.hamroclassroom_student.models.Notice;
import com.itheamc.hamroclassroom_student.models.School;
import com.itheamc.hamroclassroom_student.models.Subject;
import com.itheamc.hamroclassroom_student.models.Submission;
import com.itheamc.hamroclassroom_student.models.Teacher;
import com.itheamc.hamroclassroom_student.models.User;
import com.itheamc.hamroclassroom_student.models.UserSubject;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class QueryHandler {
    private final Handler handler;
    private final ExecutorService executorService;
    private final QueryCallbacks callbacks;
    private final OkHttpClient client;

    // Constructor
    public QueryHandler(@NonNull QueryCallbacks callbacks) {
        this.handler = HandlerCompat.createAsync(Looper.getMainLooper());
        this.executorService = Executors.newFixedThreadPool(4);
        this.callbacks = callbacks;
        this.client = new OkHttpClient();
    }

    // Getter for instance
    public static QueryHandler getInstance(@NonNull QueryCallbacks callbacks) {
        return new QueryHandler(callbacks);
    }

    /**
     * Function to get user info from the Database
     * --------------------------------------------------------------------------------------
     */
    public void getUser(String userId) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.studentGetRequestById(userId)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            User user = JsonHandler.getStudent(jsonObject);
                            notifySuccess(user,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to store user in the Database
     * --------------------------------------------------------------------------------------
     */
    public void storeUser(User user) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.studentPostRequest(user)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }

    /**
     * Function to update user in the Database
     * --------------------------------------------------------------------------------------
     */
    public void updateUser(String _uid, Map<String, Object> data) {

    }

    /**
     * Function to add subject to user in the Database
     * --------------------------------------------------------------------------------------
     */
    public void addSubjectToUser(@NonNull UserSubject userSubject) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.studentSubjectsPostRequest(userSubject)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }
                        notifyFailure(new Exception("Unable to store"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }

    /**
     * Function to remove subject from user in the Database
     * --------------------------------------------------------------------------------------
     */
    public void removeSubjectToUser(String student_sub_id) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.studentSubjectsDeleteRequestById(student_sub_id)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }

    /**
     * Function to get subjects list from the Database
     * --------------------------------------------------------------------------------------
     */
    public void getSubjects(String schoolId, String _class) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.subjectGetRequestBySchoolIdAndClass(schoolId, _class)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<Subject> subjects = JsonHandler.getSubjects(jsonObject);
                            notifySuccess(null,
                                    null,
                                    null,
                                    subjects,
                                    null,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to get assignments list from the Database
     * --------------------------------------------------------------------------------------
     */
    public void getAssignmentsBySubject(String userId, String subjectId) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.assignmentGetRequestBySubjectId(userId, subjectId)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<Assignment> assignments = JsonHandler.getAssignments(jsonObject);
                            notifySuccess(null,
                                    null,
                                    null,
                                    null,
                                    assignments,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to get assignments list from the Database
     * --------------------------------------------------------------------------------------
     */
    public void getAssignments(String _userId, String schoolId, String _class) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.assignmentGetRequestBySchoolIdAndClass(_userId, schoolId, _class)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<Assignment> assignments = JsonHandler.getAssignments(jsonObject);
                            notifySuccess(null,
                                    null,
                                    null,
                                    null,
                                    assignments,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to get assignments list from the Database
     * --------------------------------------------------------------------------------------
     */
    public void getMaterials(String _userId) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.materialsGetRequestByUserRef(_userId)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<Material> materials = JsonHandler.getMaterials(jsonObject);
                            notifySuccess(null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    materials,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to get submission from the Database
     * --------------------------------------------------------------------------------------
     */
    public void getSubmissions(String studentId) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.submissionGetRequestByStudentId(studentId)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<Submission> submissions = JsonHandler.getSubmissions(jsonObject);
                            notifySuccess(null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    submissions,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to get teacher from the Database
     * --------------------------------------------------------------------------------------
     */
    public void getTeacher(String teacherId) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.teacherGetRequestById(teacherId)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            Teacher teacher = JsonHandler.getTeacher(jsonObject);

                            notifySuccess(null,
                                    null,
                                    teacher,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to get teachers
     */
    public void getTeachers(String _school_ref) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.teacherGetRequestBySchool(_school_ref)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<Teacher> teachers = JsonHandler.getTeachers(jsonObject);
                            notifySuccess(null,
                                    null,
                                    teachers,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }

    /**
     * Function to get notices list from the Database
     * --------------------------------------------------------------------------------------
     */
    public void getNotices(String schoolId) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.noticeGetRequestBySchoolAndClass(schoolId)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<Notice> notices = JsonHandler.getNotices(jsonObject);
                            notifySuccess(null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    notices);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to get schools list from the Database
     * --------------------------------------------------------------------------------------
     */
    public void getSchools() {
        executorService.execute(() -> {
            client.newCall(RequestHandler.GET_REQUEST_SCHOOLS).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<School> schools = JsonHandler.getSchools(jsonObject);
                            notifySuccess(null,
                                    schools,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }

    /**
     * Function to get schools list from the Database
     * --------------------------------------------------------------------------------------
     */
    public void getSchool(String _schoolId) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.schoolGetRequestById(_schoolId)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            School school = JsonHandler.getSchool(jsonObject);

                            notifySuccess(null,
                                    school,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to notify whether getUser() is success or failure
     * --------------------------------------------------------------------------------------
     */
    private void notifySuccess(List<User> users,
                                 List<School> schools,
                                 List<Teacher> teachers,
                                 List<Subject> subjects,
                                 List<Assignment> assignments,
                                 List<Submission> submissions,
                                 List<Material> materials,
                                 List<Notice> notices) {
        handler.post(() -> {
            callbacks.onQuerySuccess(users, schools, teachers, subjects, assignments, submissions, materials, notices);
        });
    }


    private void notifySuccess(User user,
                               School school,
                               Teacher teacher,
                               Subject subject,
                               Assignment assignment,
                               Submission submission,
                               Material material,
                               Notice notice) {
        handler.post(() -> {
            callbacks.onQuerySuccess(user, school, teacher, subject, assignment, submission, material, notice);
        });
    }


    private void notifySuccess(String message) {
        handler.post(() -> {
            callbacks.onQuerySuccess(message);
        });
    }

    private void notifyFailure(Exception e) {
        handler.post(() -> {
            callbacks.onQueryFailure(e);
        });
    }
}

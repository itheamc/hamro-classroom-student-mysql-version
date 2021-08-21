package com.itheamc.hamroclassroom_student.callbacks;

import com.itheamc.hamroclassroom_student.models.Assignment;
import com.itheamc.hamroclassroom_student.models.Notice;
import com.itheamc.hamroclassroom_student.models.School;
import com.itheamc.hamroclassroom_student.models.Subject;
import com.itheamc.hamroclassroom_student.models.Submission;
import com.itheamc.hamroclassroom_student.models.Teacher;
import com.itheamc.hamroclassroom_student.models.User;

import java.util.List;

public interface QueryCallbacks {
    void onQuerySuccess(
            List<User> users,
            List<School> schools,
            List<Teacher> teachers,
            List<Subject> subjects,
            List<Assignment> assignments,
            List<Submission> submissions,
            List<Notice> notices);

    void onQuerySuccess(
            User user,
            School school,
            Teacher teacher,
            Subject subject,
            Assignment assignment,
            Submission submission,
            Notice notice);

    void onQuerySuccess(String message);

    void onQueryFailure(Exception e);
}

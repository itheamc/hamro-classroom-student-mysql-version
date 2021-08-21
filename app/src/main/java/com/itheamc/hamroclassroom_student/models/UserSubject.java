package com.itheamc.hamroclassroom_student.models;

public class UserSubject {
    private String _id;
    private String _user;
    private String _subject;

    // Constructor
    public UserSubject() {
    }

    // Constructors
    public UserSubject(String _id, String _user, String _subject) {
        this._id = _id;
        this._user = _user;
        this._subject = _subject;
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_user() {
        return _user;
    }

    public void set_user(String _user) {
        this._user = _user;
    }

    public String get_subject() {
        return _subject;
    }

    public void set_subject(String _subject) {
        this._subject = _subject;
    }

    // toString() method
    @Override
    public String toString() {
        return "UserSubject{" +
                "_id='" + _id + '\'' +
                ", _user='" + _user + '\'' +
                ", _subject='" + _subject + '\'' +
                '}';
    }
}

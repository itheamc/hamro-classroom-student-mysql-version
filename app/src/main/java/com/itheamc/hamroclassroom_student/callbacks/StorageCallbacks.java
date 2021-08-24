package com.itheamc.hamroclassroom_student.callbacks;


public interface StorageCallbacks {
    void onSuccess(String message);
    void onFailure(Exception e);
    void onCanceled();
}

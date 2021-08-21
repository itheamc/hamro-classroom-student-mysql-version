package com.itheamc.hamroclassroom_student.handlers;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;

import com.itheamc.hamroclassroom_student.callbacks.StorageCallbacks;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StorageHandler {
    private static final String TAG = "StorageHandler";
    private static StorageHandler instance;
    private StorageCallbacks storageCallback;
    private final ExecutorService executorService;
    private Handler handler;

    /**
     * ----------------------------------------------------------------------
     * ---------------------- Constructor for Cloud Storage------------------
     *
     * @param storageCallback -> instance of the StorageCallbacks
     */

    // Constructor
    public StorageHandler(@NonNull StorageCallbacks storageCallback) {
        this.storageCallback = storageCallback;
        this.executorService = Executors.newFixedThreadPool(4);
        this.handler = HandlerCompat.createAsync(Looper.getMainLooper());
    }

    // Instance for Cloud Storage
    public static StorageHandler getInstance(@NonNull StorageCallbacks storageCallback) {
        if (instance == null) {
            instance = new StorageHandler(storageCallback);
        }
        return instance;
    }


    /**
     * -------------------------------------------------------------------------------------------
     * -------------------------------------------------------------------------------------------
     * -------------------------------------------------------------------------------------------
     * ----------------------- THESE ARE THE FUNCTIONS FOR CLOUD STORAGE--------------------------
     * -------------------------------------------------------------------------------------------
     */

    /**
     * ---------------------------------------------
     * Function to upload image on the cloud storage
     */
    public void uploadImage(Bitmap bitmap, String imageName, String subjectId, String assignmentId, String submissionId) {

    }


    /**
     * ---------------------------------
     * Function to compress bitmap image
     */
    private byte[] getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
        return out.toByteArray();
    }


    /**
     * ------------------------------------------------------------------------------------
     * Function to notify the image upload status
     * - success
     * - failure
     * - canceled
     * - progress changed
     */
    private void notifySuccess(String imageUrl) {
        handler.post(() -> {
            storageCallback.onSuccess(imageUrl);
        });
    }

    private void notifyFailure(Exception e) {
        handler.post(() -> {
            storageCallback.onFailure(e);
        });
    }

    private void notifyCancel() {
        handler.post(() -> {
            storageCallback.onCanceled();
        });
    }

    private void notifyProgress() {
        handler.post(() -> {

        });
    }
}

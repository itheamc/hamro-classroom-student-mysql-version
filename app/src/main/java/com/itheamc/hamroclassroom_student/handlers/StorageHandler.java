package com.itheamc.hamroclassroom_student.handlers;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.FragmentActivity;

import com.itheamc.hamroclassroom_student.callbacks.StorageCallbacks;
import com.itheamc.hamroclassroom_student.models.Assignment;
import com.itheamc.hamroclassroom_student.models.Submission;
import com.itheamc.hamroclassroom_student.utils.ImageUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StorageHandler {
    private static final String TAG = "StorageHandler";
    private final StorageCallbacks storageCallback;
    private final FragmentActivity activity;
    private final ExecutorService executorService;
    private final Handler handler;

    /**
     * ----------------------------------------------------------------------
     * ---------------------- Constructor for Cloud Storage------------------
     *
     * @param storageCallback -> instance of the StorageCallbacks
     */

    // Constructor
    private StorageHandler(@NonNull FragmentActivity activity, @NonNull StorageCallbacks storageCallback) {
        this.storageCallback = storageCallback;
        this.activity = activity;
        this.executorService = Executors.newFixedThreadPool(4);;
        this.handler = HandlerCompat.createAsync(Looper.getMainLooper());
    }

    // Instance for Cloud Storage
    public static StorageHandler getInstance(@NonNull FragmentActivity activity, @NonNull StorageCallbacks storageCallback) {
        return new StorageHandler(activity, storageCallback);
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
    public void addSubmission(List<Uri> imagesUri, @NonNull Submission submission) {

        // Handling all the image processing and uploads in background
        executorService.execute(() -> {

            Request.Builder requestBuilder = new Request.Builder()
                    .url(PathHandler.SUBMISSIONS_PATH);

            // If images are selected for upload
            if (imagesUri != null && imagesUri.size() > 0) {
                // Creating the instance of ImageUtils
                ImageUtils imageUtils = ImageUtils.getInstance(activity.getContentResolver());

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                for (Uri uri : imagesUri) {
                    if (uri == null) continue;

                    String s = imageUtils.getFilePath(uri);
                    File file = new File(activity.getCacheDir(), s);
                    try {
                        FileOutputStream outputStream = new FileOutputStream(file);
                        byte[] bytes = imageUtils.getByteArray(uri, 10);
                        outputStream.write(bytes);
                        outputStream.close();
                        Uri uris = Uri.fromFile(file);
                        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uris.toString());
                        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
                        String imageName = file.getName();

                        builder.addFormDataPart("file", imageName, RequestBody.create(file, MediaType.parse(mime)));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                // Outside for loop
                RequestBody requestBody = builder
                        .addFormDataPart("_id", submission.get_id())
                        .addFormDataPart("_images", "")
                        .addFormDataPart("_docs", "")
                        .addFormDataPart("_texts", submission.get_texts())
                        .addFormDataPart("_assignment", submission.get_assignment_ref())
                        .addFormDataPart("_student", submission.get_student_ref())
                        .addFormDataPart("_submitted_date", submission.get_submitted_date())
                        .addFormDataPart("_checked_date", submission.get_checked_date())
                        .addFormDataPart("_checked", String.valueOf(submission.is_checked()))
                        .addFormDataPart("_comment", submission.get_comment())
                        .build();

                requestBuilder
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .post(requestBody)
                        .build();

            } else {
                // If images are not selected for upload
                RequestBody rb = new FormBody.Builder()
                        .add("_id", submission.get_id())
                        .add("_images", "")
                        .add("_docs", "")
                        .add("_texts", submission.get_texts())
                        .add("_assignment", submission.get_assignment_ref())
                        .add("_student", submission.get_student_ref())
                        .add("_submitted_date", submission.get_submitted_date())
                        .add("_checked_date", submission.get_checked_date())
                        .add("_checked", String.valueOf(submission.is_checked()))
                        .add("_comment", submission.get_comment())
                        .build();

                requestBuilder
                        .post(rb)
                        .build();
            }

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .callTimeout(300, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            okHttpClient.newCall(requestBuilder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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
                        notifyFailure(new Exception("Unable to add"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * ------------------------------------------------------------------------------------
     * Function to notify the image upload status
     * - success
     * - failure
     */
    private void notifySuccess(String message) {
        handler.post(() -> {
            storageCallback.onSuccess(message);
        });
    }

    private void notifyFailure(Exception e) {
        handler.post(() -> {
            storageCallback.onFailure(e);
        });
    }
}
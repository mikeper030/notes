package org.ultimatetoolsil.mike.note;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import org.ultimatetoolsil.mike.note.models.NoteTitle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mike on 20 Jun 2018.
 */

class BackupJob extends JobIntentService implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(BackupJob.this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(BackupJob.this)
                    .addOnConnectionFailedListener(BackupJob.this)
                    .build();
        }
        mGoogleApiClient.connect();



    }
    public static void enqueueWork(Context context, Intent work){
        enqueueWork(context,BackupJob.class,9,work);
    }
    private void uploadFileToDrive(DriveApi.DriveContentsResult result) {
        ArrayList<NoteTitle> titles=utils.getallsavednotes();
        if(titles!=null){
            final DriveContents driveContents = result.getDriveContents();

            // Perform I/O off the UI thread.
            new Thread() {
                @Override
                public void run() {


                    utils.serializeListToFile(BackupJob.this);
                    String storage_path= Environment.getExternalStorageDirectory()+"/MyNotes/data.bin";


                    Log.d("file",storage_path);


                    FileInputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(storage_path);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    byte[] buffer = new byte[8 * 1024];

                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(driveContents.getOutputStream());
                    int n = 0;
                    try {
                        while ((n = bufferedInputStream.read(buffer)) > 0) {
                            bufferedOutputStream.write(buffer, 0, n);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {

                        bufferedInputStream.close();
                        bufferedOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    final MimeTypeMap mime = MimeTypeMap.getSingleton();
                    //String tmptype = mime.getMimeTypeFromExtension("vcf");
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("data.bin")
                            .setMimeType("text/plain")
                            .setStarred(true).build();

                    Drive.DriveApi.getAppFolder(getGoogleApiClient())
                            .createFile(getGoogleApiClient(), changeSet, driveContents)
                            .setResultCallback(fileCallback);
                }
            }.start();
        }
    }
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {

                        return;
                    }

                    PreferenceManager.getDefaultSharedPreferences(BackupJob.this).edit().putString("fileid", result.getDriveFile().getDriveId().encodeToString()).apply();
                    utils.deleteFile(Environment.getExternalStorageDirectory()+"/MyNotes/data.bin");
                }
            };
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(driveContentsCallback);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (result.getStatus().isSuccess()) {

                      uploadFileToDrive(result);

                }}
            };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

package cpen391.team6.bored.Fragments;

import android.app.Fragment;
//import android.os.AsyncTask;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;

//import com.google.appengine.tools.cloudstorage.GcsFileOptions;
//import com.google.appengine.tools.cloudstorage.GcsFilename;
//import com.google.appengine.tools.cloudstorage.GcsInputChannel;
//import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
//import com.google.appengine.tools.cloudstorage.GcsService;
//import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
//import com.google.appengine.tools.cloudstorage.RetryParams;
//import com.google.cloud.datastore.Datastore;
//import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.net.URLEncoder;
import java.util.Collections;

import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.CloudStorage;


/**
 * Created by neema on 2017-03-14.
 * Implemented by Andy Tertzakian on 2017-03-19
 */
public class ViewNotesFragment extends Fragment{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.view_notes_fragment_layout, container, false);

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {

                    CloudStorage.createBucket("my-bucket");

                }catch(Exception e){
                    //Log.d("TEST", e.printStackTrace());
                    e.printStackTrace();
                }
            }

        });

        thread.start();

        return view;
    }
}

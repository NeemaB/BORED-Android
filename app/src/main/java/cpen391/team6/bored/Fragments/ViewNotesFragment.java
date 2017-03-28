package cpen391.team6.bored.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cpen391.team6.bored.R;


/**
 * Created by neema on 2017-03-14.
 * Implemented by Andy Tertzakian on 2017-03-19
 */
public class ViewNotesFragment extends Fragment {

    private  String mKeyPath = "/Bored-c22e5b0a43a4.json";
    private String bucketName = "boredpupil-ceed0.appspot.com";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.view_notes_fragment_layout, container, false);
        Log.d("TEST", "HERE");
        try {
            Log.d("TEST", "HERE1");

            File file = new File(mKeyPath);

            Log.d("TEST", Long.toString(file.length()));

            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(file.getPath())))
                    .build()
                    .getService();

            Log.d("TEST", "HERE2");
            Bucket bucket = storage.create(BucketInfo.of(bucketName));
            Page<Blob> blobs = bucket.list();
            Iterator<Blob> blobIterator = blobs.iterateAll();
            while (blobIterator.hasNext()) {
                Blob blob = blobIterator.next();
                blob.getAcl();
            }

        } catch (Exception e){
            Log.d("TEST", e.toString());
        }

        return view;
    }


}

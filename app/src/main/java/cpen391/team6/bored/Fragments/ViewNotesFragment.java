package cpen391.team6.bored.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.google.cloud.storage.Bucket;
//import com.google.cloud.storage.BucketInfo;
//import com.google.cloud.storage.Storage;
//import com.google.cloud.storage.StorageOptions;

import cpen391.team6.bored.R;



/**
 * Created by neema on 2017-03-14.
 */
public class ViewNotesFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.view_notes_fragment_layout, container, false);
//
//        Storage storage = StorageOptions.getDefaultInstance().getService();
//        String bucketName = "boredpupil-ceed0.appspot.com";  // "my-new-bucket";
//
//        Bucket bucket = storage.create(BucketInfo.of(bucketName));


        return view;
    }



}

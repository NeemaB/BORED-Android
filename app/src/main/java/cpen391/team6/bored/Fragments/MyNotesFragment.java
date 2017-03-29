package cpen391.team6.bored.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import cpen391.team6.bored.R;

/**
 * Created by neema on 2017-03-28.
 */
public class MyNotesFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_notes_fragment_layout, container, false);
        ImageView tempImageView;
        tempImageView = (ImageView) view.findViewById(R.id.temp_image);

        File file = getActivity().getFilesDir();
        String path = file.getAbsolutePath();

        Bitmap bm = BitmapFactory.decodeFile(path + "/temp.jpg");


        tempImageView.setImageBitmap(bm);

        return view;

    }
}

package cpen391.team6.bored.Adapters;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
/**
 * Created by andytertzakian on 2017-03-16.
 */


//TODO
public class NoteImageAdapter {

    private List<Drawable> mImageList;

    public void addPhoto(Drawable drawable) {
    }
1
    //constructor for setting the image list
    public NoteImageAdapter(List<Drawable> mImageList) {
        this.mImageList = mImageList;
    }

    //Get the number of items in the list
    public int getItemCount() {
        return mImageList.size();
    }
}

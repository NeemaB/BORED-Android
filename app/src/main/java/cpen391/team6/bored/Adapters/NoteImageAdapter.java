package cpen391.team6.bored.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cpen391.team6.bored.R;

/**
 * Created by andytertzakian on 2017-03-16.
 */

public class NoteImageAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private String[] imageUrls;

    public NoteImageAdapter(Context context, String[] imageUrls) {
        super(context, R.layout.course_notes_fragment_layout, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.course_notes_fragment_layout, parent, false);
        }

        Glide
                .with(context)
                .load(imageUrls[position])
                .into((ImageView) convertView);

        return convertView;
    }
}

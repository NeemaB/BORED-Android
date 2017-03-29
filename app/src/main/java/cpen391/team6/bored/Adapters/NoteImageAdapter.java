package cpen391.team6.bored.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;


import cpen391.team6.bored.Data.ExternalNote;
import cpen391.team6.bored.Data.LocalNote;
import cpen391.team6.bored.Data.Note;
import cpen391.team6.bored.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andytertzakian on 2017-03-16.
 */

public class NoteImageAdapter extends ArrayAdapter <Note> {
    private Context context;

    private ArrayList<Note> notes;
    private String[] imageUrls;

    private static class ViewHolder{
        ImageView mNoteImage;
    }

    public NoteImageAdapter(Context context, int resourceId, ArrayList<Note> notes) {
        super(context, resourceId, notes);

        this.context = context;
        this.notes = notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Note note = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.note_list_item, parent, false);

            viewHolder.mNoteImage = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        }

        if(note instanceof LocalNote){
            LocalNote localNote = (LocalNote) note;

            //TODO: Do something with the local note here to fetch the image and populate the image view


        }else if(note instanceof ExternalNote){
            ExternalNote externalNote = (ExternalNote) note;

            //TODO: Do something with the external note here to fetch the image and populate the image view

//        Glide
//                .with(context)
//                .load(imageUrls[position])
//                .into((ImageView) convertView);
        }



        return convertView;
    }
}

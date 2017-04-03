package cpen391.team6.bored.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.codekrypt.greendao.db.LocalNote;
import cpen391.team6.bored.Data.ExternalNote;
import cpen391.team6.bored.Data.Note;
import cpen391.team6.bored.R;


import java.util.ArrayList;


/**
 * Created by andytertzakian on 2017-03-16.
 */

public class NoteImageAdapter extends ArrayAdapter <Note> {
    private Context context;

    private ArrayList<? extends Note> notes;
    private String[] imageUrls;

    private static class ViewHolder{
        ImageView mNoteImage;
        TextView mNoteTitle;
        TextView mNoteTopic;
        TextView mNoteDate;
    }

    public NoteImageAdapter(Context context, int resourceId, ArrayList<? extends Note> notes) {
        super(context, resourceId, (ArrayList<Note>) notes);

        this.context = context;
        this.notes = notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Note note = getItem(position);
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.note_list_item, parent, false);

            viewHolder.mNoteImage = (ImageView) convertView.findViewById(R.id.note_image);
            viewHolder.mNoteTitle = (TextView) convertView.findViewById(R.id.note_title);
            viewHolder.mNoteTopic = (TextView) convertView.findViewById(R.id.note_topic);
            viewHolder.mNoteDate = (TextView) convertView.findViewById(R.id.note_date);

            convertView.setTag(viewHolder);
        }

        if(note instanceof LocalNote){
            LocalNote localNote = (LocalNote) note;

            String date = localNote.getDate().toString();
            String title = localNote.getTitle();
            String topic = localNote.getTopic();

            if(topic.length() == 0)
                viewHolder.mNoteTopic.setVisibility(View.GONE);

            viewHolder.mNoteTitle.setText(title);
            viewHolder.mNoteTopic.setText(topic);
            viewHolder.mNoteDate.setText(date);

            Bitmap bm = BitmapFactory.decodeFile(localNote.getFilePath());
            viewHolder.mNoteImage.setImageBitmap(bm);

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

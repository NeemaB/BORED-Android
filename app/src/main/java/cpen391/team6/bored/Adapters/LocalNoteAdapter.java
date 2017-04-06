package cpen391.team6.bored.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.codekrypt.greendao.db.LocalNote;
import com.codekrypt.greendao.db.LocalNoteDao;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cpen391.team6.bored.Activities.MainActivity;
import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.Data.ExternalNote;
import cpen391.team6.bored.Data.Note;
import cpen391.team6.bored.Items.Point;
import cpen391.team6.bored.Items.PopUpMenu;
import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.DataUtil;

/**
 * Created by neema on 2017-03-30.
 */
public class LocalNoteAdapter extends ArrayAdapter<LocalNote> {

    private Context context;

    private List<? extends Note> notes;
    private String[] imageUrls;

    private static class ViewHolder {
        ImageView mNoteImage;
        TextView mNoteTitle;
        TextView mNoteTopic;
        TextView mNoteDate;
        IconTextView mNoteActions;
    }

    public LocalNoteAdapter(Context context, int resourceId, List<LocalNote> notes) {
        super(context, resourceId, notes);

        this.context = context;
        this.notes = notes;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Note note = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.note_list_item, parent, false);

            viewHolder.mNoteImage = (ImageView) convertView.findViewById(R.id.note_image);
            viewHolder.mNoteTitle = (TextView) convertView.findViewById(R.id.note_title);
            viewHolder.mNoteTopic = (TextView) convertView.findViewById(R.id.note_topic);
            viewHolder.mNoteDate = (TextView) convertView.findViewById(R.id.note_date);
            viewHolder.mNoteActions = (IconTextView) convertView.findViewById(R.id.note_item_actions);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final LocalNote localNote = (LocalNote) note;

        Date date = localNote.getDate();

        String formattedDate = DataUtil.getFormattedDate(date);

        //String date = localNote.getDate().toString();
        String title = localNote.getTitle();
        String topic = localNote.getTopic();

        if (topic.length() == 0) {
            viewHolder.mNoteTopic.setVisibility(View.GONE);
        }else{
            viewHolder.mNoteTopic.setVisibility(View.VISIBLE);
        }


        viewHolder.mNoteTitle.setText(title);
        viewHolder.mNoteTopic.setText(topic);
        viewHolder.mNoteDate.setText(formattedDate);

        viewHolder.mNoteActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, viewHolder.mNoteActions);

                popup.getMenuInflater().inflate(R.menu.note_action_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){

                            case R.id.delete_my_note:


                                //TODO: Add confirmation alert dialog for deleting a note
                                LocalNoteDao localNoteDao = BoredApplication
                                        .getDaoSession()
                                        .getLocalNoteDao();

                                /* Retrieve the local note from the local database */
                                QueryBuilder<LocalNote> qb = localNoteDao.queryBuilder();
                                List<LocalNote> queryList = qb
                                        .where(LocalNoteDao.Properties.Id
                                                .eq(localNote.getId())).list();

                                /* Check to see if we found it, if we did, delete it from the database
                                 * and from the adapter's list of entries, then update the listview
                                 * by calling notifyDataSetChanged
                                 */
                                if(!queryList.isEmpty()) {
                                    LocalNote deleteNote = queryList.get(0);
                                    localNoteDao.delete(deleteNote);
                                    notes.remove(position);
                                    Toast.makeText(context, "Deleted Note!", Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                    return true;
                                }else{
                                    return false;
                                }

                            case R.id.load_my_note:

                                Bundle arguments = new Bundle();
                                arguments.putString("load_note_path", localNote.getFilePath());
                                arguments.putString("command_list", localNote.getCommandList());
                                arguments.putBoolean("external_note", false);

                                ((MainActivity) context).loadNote(arguments);
                                break;

                        }
                        return false;
                    }
                });
                /*Display the popup */
                popup.show();
            }
        });

        /* Load the image asynchronously from local storage */
        Picasso.with(this.context)
                .load(new File(localNote.getFilePath()))
                .into(viewHolder.mNoteImage);

        //TODO: Do something with the local note here to fetch the imag  e and populate the image view


        return convertView;
    }

}

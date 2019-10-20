package com.example.akhilbatchupart2;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    public Context ctx;
    private List<Notes> notesList;
    SQLiteDatabase database;
    int count = 0;
    EditText messageNotes, notesTitles;
    int pstion;

    public NotesAdapter(Context ctx, List<Notes> notesList, SQLiteDatabase database) {
        this.ctx = ctx;
        this.notesList = notesList;
        this.database = database;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.my_note_custom, null);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotesViewHolder notesViewHolder, int i) {
        final Notes note = notesList.get(i);
        notesViewHolder.notesTitle.setText(note.getTitle().toUpperCase());
        notesViewHolder.smallMessage.setText(note.getMessage());
        notesViewHolder.menu.setTag(i);
        notesViewHolder.customCheckBox.setVisibility(View.INVISIBLE);
        notesViewHolder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Menu menu = ShortNotes.getThis().getMenu();
                MenuItem item = menu.findItem(R.id.multiShare);
                note.setSelected(!note.isSelected());
                if (note.isSelected()) {
                    count++;
                } else {
                    count--;
                }
                Log.i("mycount", count + "");
                boolean val = count > 0 ? true : false;
                item.setVisible(val);
                notesViewHolder.customCheckBox.setChecked(note.isSelected());
                notesViewHolder.customCheckBox.setVisibility(note.isSelected() ? View.VISIBLE : View.INVISIBLE);

                return false;
            }
        });

        notesViewHolder.customCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!note.isSelected()) {
                    notesViewHolder.customCheckBox.setVisibility(View.INVISIBLE);
                }
            }
        });

        notesViewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pstion = (int) notesViewHolder.getAdapterPosition();
                PopupMenu popupMenu = new PopupMenu(ctx, notesViewHolder.menu);
                popupMenu.inflate(R.menu.adapter_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.Share) {
                            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(android.content.Intent.EXTRA_TEXT, notesList.get(pstion).getTitle() + "\n" + notesList.get(pstion).getMessage());
                            ctx.startActivity(Intent.createChooser(intent, "share"));
                        } else if (item.getItemId() == R.id.Update) {
                            AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(ctx);
                            myAlertBuilder.setTitle("Update the Short Notes");
                            myAlertBuilder.setCancelable(false);
                            final String info = "Please Enter All The Details";
                            LayoutInflater inflater = LayoutInflater.from(ctx);
                            View layout = inflater.inflate(R.layout.custom_layout, null);
                            myAlertBuilder.setView(layout);
                            messageNotes = (EditText) layout.findViewById(R.id.messageNotes);
                            notesTitles = (EditText) layout.findViewById(R.id.notesTitles);
                            messageNotes.setText(note.getMessage());
                            notesTitles.setText(note.getTitle());
                            myAlertBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (messageNotes.getText().toString().isEmpty() || notesTitles.getText().toString().isEmpty()) {
                                        Toast.makeText(ctx, info, Toast.LENGTH_LONG).show();
                                        return;
                                    } else {
                                        ContentValues cv = new ContentValues();
                                        cv.put("Title", notesTitles.getText().toString());
                                        cv.put("message", messageNotes.getText().toString());
                                        Log.i("mynote", note.getId() + "");
                                        database.update("newnotes", cv, "note_id = " + note.getId(), null);
                                        updated(pstion, notesTitles.getText().toString(), messageNotes.getText().toString(), note.getId());

                                    }
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            myAlertBuilder.show();


                        } else if (item.getItemId() == R.id.Delete) {
                            String column = "Title";
                            database.delete("newnotes", "Title=?", new String[]{note.getTitle()});
                            notesList.remove(pstion);
                            notifyItemRemoved(pstion);
                            notifyItemRangeChanged(pstion, notesList.size());
                            notifyDataSetChanged();
                            new SweetAlertDialog(ctx, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setContentText("Deletion has been done successfully!")
                                    .show();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }


    private void updated(int pstion, String title, String message, int id) {
        notesList.get(pstion).setId(id);
        notesList.get(pstion).setTitle(title);
        notesList.get(pstion).setMessage(message);
        notifyDataSetChanged();
        new SweetAlertDialog(ctx, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText("You have updated changes successfully")
                .show();
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView notesTitle;
        TextView smallMessage;
        TextView menu;
        View view;
        RelativeLayout relativeLayout;
        CustomCheckBox customCheckBox;
        CardView card;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            customCheckBox = itemView.findViewById(R.id.customCheckBox);
            relativeLayout = itemView.findViewById(R.id.customLayout);
            card = itemView.findViewById(R.id.cardView);
            smallMessage = itemView.findViewById(R.id.smallMessage);
            menu = itemView.findViewById(R.id.menu);
            notesTitle = itemView.findViewById(R.id.notesTitle);

        }
    }

}

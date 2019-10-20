package com.example.akhilbatchupart2;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShortNotes extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Notes> notesList;
    NotesAdapter notesAdapter;
    SQLiteDatabase database;
    EditText messageNotes, notesTitles;
    String info;
    private Menu mMenu = null;
    Menu myMenu = null;
    private static ShortNotes mThis = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mThis = this;
        getSupportActionBar().setTitle("ShortNotes");
        FloatingActionButton fab = findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        try {
            database = this.openOrCreateDatabase("MyNotes", MODE_PRIVATE, null);
            notesList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            notesAdapter = new NotesAdapter(ShortNotes.this, notesList, database);
            recyclerView.setAdapter(notesAdapter);
            getData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNotes(View view) {
        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(ShortNotes.this);
        myAlertBuilder.setTitle("Create the Short Notes");
        myAlertBuilder.setCancelable(false);
        info = "Please Enter All The Details";
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.custom_layout, null);
        myAlertBuilder.setView(layout);
        messageNotes = (EditText) layout.findViewById(R.id.messageNotes);
        notesTitles = (EditText) layout.findViewById(R.id.notesTitles);
        myAlertBuilder.setPositiveButton("Save", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (messageNotes.getText().toString().isEmpty() || notesTitles.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            database.execSQL("INSERT INTO newnotes (Title,message) VALUES('" + notesTitles.getText().toString() + "', '" + messageNotes.getText().toString() + "');");
                            getData();
                        }

                    }
                });
        myAlertBuilder.setNegativeButton("Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        myAlertBuilder.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        myMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    public Menu getMenu() {
        return mMenu;
    }


    public static ShortNotes getThis() {
        return mThis;
    }

    public void getData() {
        try {
            notesList.clear();
            Cursor c = database.rawQuery("SELECT * FROM newnotes", null);
            int titleIndex = c.getColumnIndex("Title");
            int messageIndex = c.getColumnIndex("message");
            int idIndex = c.getColumnIndex("note_id");
            c.moveToFirst();
            while (c != null) {
                notesList.add(new Notes(c.getString(titleIndex), c.getString(messageIndex), c.getInt(idIndex)));
                c.moveToNext();
            }
            recyclerView.setAdapter(notesAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(ShortNotes.this.getApplicationContext(), "onActivityResult..:", Toast.LENGTH_SHORT).show();
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                for (int i = 0; i < notesList.size(); i++) {
                    if (notesList.get(i).isSelected()) {
                        notesList.get(i).setSelected(false);
                    }
                }
                MenuItem item = myMenu.findItem(R.id.multiShare);
                item.setVisible(false);
                notesAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.clearAll) {
            database.execSQL("delete from " + "newnotes");
            notesList.clear();
            notesAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.Resume) {
            Intent intent = new Intent(ShortNotes.this, Resume.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.multiShare) {
            String message = "";
            String output = "";
            for (int i = 0; i < notesList.size(); i++) {

                if (notesList.get(i).isSelected()) {
                    message = "*" + notesList.get(i).getTitle() + "*" + "\n" + "  " +
                            notesList.get(i).getMessage() + "\n";
                    Log.i("Message", message);
                }
                output = output + message + "\n";
                message = "";
            }
            Log.i("outputs", output);
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, output);
            intent.setPackage("com.whatsapp");
            startActivityForResult(intent, 1);
        }


        return true;
    }
}

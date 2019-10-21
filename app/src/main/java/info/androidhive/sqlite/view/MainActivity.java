package info.androidhive.sqlite.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.database.DatabaseHelper;
import info.androidhive.sqlite.database.model.Hobby;
import info.androidhive.sqlite.utils.MyDividerItemDecoration;
import info.androidhive.sqlite.utils.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity {
    private HobbiesAdapter mAdapter;
    private List<Hobby> hobbiesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noHobbiesView;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noHobbiesView = findViewById(R.id.empty_hobbies_view);

        db = new DatabaseHelper(this);

        hobbiesList.addAll(db.getAllHobbies());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHobbyDialog(false, null, -1);
            }
        });

        mAdapter = new HobbiesAdapter(this, hobbiesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyHobby();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * Inserting new hobby in db
     * and refreshing the list
     */
    private void createHobby(String hobby) {
        // inserting hobby in db and getting
        // newly inserted hobby id
        long id = db.insertHobby(hobby);

        // get the newly inserted hobby from db
        Hobby n = db.getHobby(id);

        if (n != null) {
            // adding new hobby to array list at 0 position
            hobbiesList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyHobby();
        }
    }

    /**
     * Updating hobby in db and updating
     * item in the list by its position
     */
    private void updateHobby(String hobby, int position) {
        Hobby n = hobbiesList.get(position);
        // updating hobby text
        n.setHobby(hobby);

        // updating hobby in db
        db.updateHobby(n);

        // refreshing the list
        hobbiesList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyHobby();
    }

    /**
     * Deleting hobby from SQLite and removing the
     * item from the list by its position
     */
    private void deleteHobby(int position) {
        // deleting the hobby from db
        db.deleteHobby(hobbiesList.get(position));

        // removing the hobby from the list
        hobbiesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyHobby();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showHobbyDialog(true, hobbiesList.get(position), position);
                } else {
                    deleteHobby(position);
                }
            }
        });
        builder.show();
    }


    /**
     * Shows alert dialog with EditText options to enter / edit
     * a hobby.
     * when shouldUpdate=true, it automatically displays old hobby and changes the
     * button text to UPDATE
     */
    private void showHobbyDialog(final boolean shouldUpdate, final Hobby hobby, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.hobby_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputHobby = view.findViewById(R.id.hobby);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_hobby_title) : getString(R.string.lbl_edit_hobby_title));

        if (shouldUpdate && hobby != null) {
            inputHobby.setText(hobby.getHobby());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputHobby.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter hobby!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating hobby
                if (shouldUpdate && hobby != null) {
                    // update hobby by it's id
                    updateHobby(inputHobby.getText().toString(), position);
                } else {
                    // create new hobby
                    createHobby(inputHobby.getText().toString());
                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyHobby() {
        // you can check hobbiesList.size() > 0

        if (db.getHobbiesCount() > 0) {
            noHobbiesView.setVisibility(View.GONE);
        } else {
            noHobbiesView.setVisibility(View.VISIBLE);
        }
    }
}

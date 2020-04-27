package com.example.pets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pets.data.SQL;
import com.example.pets.data.SQL.PetData;
import com.example.pets.data.ShelterDb;

import java.net.URI;

public final class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    private boolean mPetHasChanged = false;

    private int mGender = SQL.PetData.UNKNOWN;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);

        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);

        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);

        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();

        Intent intent=getIntent();
        uri= intent.getData();
       if(uri==null)
       {
           setTitle(R.string.editor_activity_title_new_pet);
           invalidateOptionsMenu();
       }
       else
       {
           setTitle("Edit a pet");
       LoaderManager.getInstance(this).initLoader(0,null,this);
       }

        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);
    }
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = SQL.PetData.MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = SQL.PetData.FEMALE; // Female
                    } else {
                        mGender = SQL.PetData.UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = SQL.PetData.UNKNOWN; // Unknown
            }
        });
    }
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (uri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    private int addInfo()
    {   int weight;
        ShelterDb helper=new ShelterDb((this));
        SQLiteDatabase db= helper.getWritableDatabase();
        ContentValues value=new ContentValues();
        String  name=mNameEditText.getText().toString().trim();
        String breed= mBreedEditText.getText().toString().trim();
      String w=  mWeightEditText.getText().toString().trim();

      if(w.equals(""))
      {
          weight=0;
      }
       else {
          weight = Integer.parseInt(w);
      }


        value.put(PetData.NAME,name);
        value.put(PetData.BREED,breed);
        value.put(PetData.WEIGHT,weight);
        value.put(PetData.GENDER,mGender);
        if(uri==null) {

            Uri uri1= getContentResolver().insert(SQL.PetData.petsUri, value);
            if(uri1==null)
                return -1;
            else
            return 0;
        }
        else
        return getContentResolver().update(uri,value, null,null );


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
              int i= addInfo();
                finish();
                if(i==0)
                Toast.makeText(getApplicationContext(),R.string.petAddition,Toast.LENGTH_LONG).show();
                else if (i==-1)
                {
                    //taken care by insert in PetsProvder
                }

                else
                    Toast.makeText(getApplicationContext(),"Pet is Edited",Toast.LENGTH_LONG).show();


                return true;
            case R.id.action_delete:
                 showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {

        String [] preferences=new String[]{PetData._ID, PetData.NAME, PetData.BREED,PetData.GENDER,PetData.WEIGHT};
        return  new CursorLoader(this,uri,preferences,null,null,null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        Cursor cursor=(Cursor) data;
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if(cursor.moveToFirst())
        {
            mNameEditText.setText(cursor.getString(1));
            mBreedEditText.setText((cursor.getString(2)));
            mWeightEditText.setText(""+cursor.getInt(4));
            switch (cursor.getInt(3))
            {
                case PetData.MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetData.FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                    default:
                        mGenderSpinner.setSelection(0);
                        break;

            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private  void deletePet()
    {
        if(uri!=null)
        {
       int rowsDeleted= getContentResolver().delete(uri,null,null);
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_LONG).show();
            }
            finish();
    }
    }
}

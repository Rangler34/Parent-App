package ca.cmpt276.parentApp.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.databinding.ActivityChildInfoBinding;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;
import ca.cmpt276.parentApp.model.TaskManager;
import ca.cmpt276.parentApp.model.TurnOnTask;

/**
 * AddChildActivity class - add and save a child's name feature in this activity
 */
public class AddChildActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityChildInfoBinding binding;

    private static final String NAME_PREF_NAME = "Child name";
    private static final String PIC_PREF_NAME = "Child pic";
    private static final String PREF_NAME = "AppPrefs";

    ImageButton childProfileImage;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;

    String cameraPermission[];
    String galleryPermission[];

    private Uri imageUri = null;
    private String picUriString;

    GeneralManager genManager = GeneralManager.getInstance();
    private static final String UNASSIGNED = "unassigned";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        getSupportActionBar().setTitle(R.string.title_activity_add_child);

        selectChildPic();
        setupSaveButton();

    }

    private void assignUnassignedTasksToNewChild() {
        TaskManager taskManager = genManager.getTaskManager();
        if (genManager.getChildren().getNumChildren() == 1) {
            String firstChild = genManager.getChildren().getChildNameAt(0);
            for (int i=0; i< taskManager.getNumTasks(); i++) {
                TurnOnTask currTurn = taskManager.getCurrentTurnOnTaskAt(i);
                if (currTurn.getChildName().equals(UNASSIGNED)) {
                    currTurn.setChildName(firstChild);
                }
            }
        }
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, AddChildActivity.class);
    }

    private void setupSaveButton() {

        CoinFlipManager coinFlipManager = genManager.getCoinFlipManager();
        EditText nameInput = findViewById(R.id.nameEdit);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            if (nameInput.getText().toString().trim().isEmpty()){
                Toast.makeText(AddChildActivity.this, getString(R.string.enter_a_name), Toast.LENGTH_SHORT).show();
            }else {
                genManager.getChildren().addChildName(nameInput.getText().toString());

               if (coinFlipManager.getCurrentPicker() == null) {
                    coinFlipManager.setCurrentPicker(nameInput.getText().toString());
                }
                coinFlipManager.addChildToPickerQueue(nameInput.getText().toString());

                Toast.makeText(AddChildActivity.this, getString(R.string.child_added), Toast.LENGTH_SHORT).show();
                saveChildName(nameInput.getText().toString());
                saveChildPic(picUriString);
                assignUnassignedTasksToNewChild();
                finish(); //terminate current activity and return to previous activity
            }
        });
    }

    private void saveChildName(String name) {

        SharedPreferences prefs = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NAME_PREF_NAME, name);
        editor.apply();

    }

    private void saveChildPic(String pic) {

        genManager.getChildren().addChildPic(imageUri);

        if (imageUri != null) {
           childProfileImage = findViewById(R.id.child_profile_pic);
           childProfileImage.setImageURI(Uri.parse(pic));
        }

        SharedPreferences prefs = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PIC_PREF_NAME, pic);
        editor.apply();
    }

    private void selectChildPic() {
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        galleryPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        childProfileImage = findViewById(R.id.child_profile_pic);
        childProfileImage.setImageResource(R.drawable.default_photo);

        childProfileImage.setOnClickListener(view -> {
            int pic = 0;
            if (pic == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                }
                else {
                    pickFromGallery();
                }
            }
            else if (pic == 1) {
                if (!checkGalleryPermission()) {
                    requestGalleryPermission();
                }
                else {
                    pickFromGallery();
                }
            }
        });
    }

    private void pickFromGallery() {
        CropImage.activity().start(this);
    }

    private boolean checkCameraPermission() {
        boolean openCameraPermit = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean useTakenPhotoPermit = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return openCameraPermit && useTakenPhotoPermit;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkGalleryPermission() {
        boolean openGalleryPermit = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return openGalleryPermit;
    }

    private void requestGalleryPermission() {
        requestPermissions(galleryPermission, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = (grantResults[0] == (PackageManager.PERMISSION_GRANTED));
                    boolean storage_accepted = (grantResults[1] == (PackageManager.PERMISSION_GRANTED));
                    if (camera_accepted && storage_accepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case GALLERY_REQUEST_CODE: {
                boolean storage_accepted = (grantResults[1] == (PackageManager.PERMISSION_GRANTED));
                if (storage_accepted) {
                    pickFromGallery();
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Picasso.with(this).load(imageUri).into(childProfileImage);
                picUriString = imageUri.toString();
            }
        }
    }
}
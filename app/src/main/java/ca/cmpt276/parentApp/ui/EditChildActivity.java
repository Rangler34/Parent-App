package ca.cmpt276.parentApp.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import ca.cmpt276.parentApp.model.Children;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;

/**
 * EditChildActivity class - display edit feature for user to rename
 */
public class EditChildActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityChildInfoBinding binding;

    private static final String NAME = "ca.cmpt276.parentApp.ui.EditChildActivity - child name";
    private static final String POSITION = "ca.cmpt276.parentApp.ui.EditChildActivity - list editPosition";
    private String childName;
    private int editPosition;

    ImageButton childProfileImage;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;

    String cameraPermission[];
    String galleryPermission[];

    private Uri imageUri;
    private String picUriString;

    GeneralManager genManager = GeneralManager.getInstance();
    CoinFlipManager coinFlipManager = genManager.getCoinFlipManager();
    Children childrenData = genManager.getChildren();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);
        getSupportActionBar().setTitle(R.string.title_activity_edit_child);

        extractDataFromIntent();
        setupEditText();
        selectChildPic();
        removeChildPic();
        setupSaveButton();
    }

    public static Intent makeIntent(Context context, String name, int position){
        Intent intent = new Intent(context, EditChildActivity.class);
        intent.putExtra(NAME, name); //any unique string
        intent.putExtra(POSITION, position);
        return intent;
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        childName = intent.getStringExtra(NAME);
        editPosition = intent.getIntExtra(POSITION, 0);
    }

    private void setupEditText() {
        EditText editText = findViewById(R.id.nameEdit);
        editText.setText(childName);
    }

    private void setupSaveButton() {
        EditText editInput = findViewById(R.id.nameEdit);


        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            if (editInput.getText().toString().trim().isEmpty()){
                Toast.makeText(EditChildActivity.this,
                        getString(R.string.enter_a_name),
                        Toast.LENGTH_SHORT).show();
            }else {

                String oldName = childrenData.getChildNameAt(editPosition);
                String newName = editInput.getText().toString();
                childrenData.editChildNameAt(editPosition, newName);

                int indexInPickerQueue = coinFlipManager.getPickerQueue().getIndexOfChild(oldName);
                coinFlipManager.getPickerQueue().editChildNameAt(indexInPickerQueue, newName);
                if (coinFlipManager.getCurrentPicker().equals(oldName)) {
                    coinFlipManager.setCurrentPicker(newName);
                }

                genManager.updateChildNameInAllLists(oldName, newName);

                Toast.makeText(EditChildActivity.this,
                        getString(R.string.name_is_updated),
                        Toast.LENGTH_SHORT).show();
                        finish(); //terminate current activity and return to previous activity
            }
        });
    }

    private void removeChildPic() {
        Button removeBtn = findViewById(R.id.remove_pic_button);

        removeBtn.setOnClickListener(view -> {
            childrenData.editChildPicAt(editPosition, null);
            genManager.updateChildPicInAllLists(childName, null);
            ImageView profilePic = findViewById(R.id.child_profile_pic);
            profilePic.setImageResource(R.drawable.default_photo);
            Toast.makeText(EditChildActivity.this, R.string.photo_is_removed, Toast.LENGTH_SHORT).show();
        });
    }

    private void selectChildPic() {
        childProfileImage = findViewById(R.id.child_profile_pic);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        galleryPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
        boolean cameraPermit = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean galleryPermit = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return cameraPermit && galleryPermit;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkGalleryPermission() {
        boolean galleryPermit = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return galleryPermit;
    }

    private void requestGalleryPermission() {
        requestPermissions(galleryPermission, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
                Uri newPicUri = result.getUri();
                Picasso.with(this).load(newPicUri).into(childProfileImage);
                childrenData.editChildPicAt(editPosition, newPicUri);
                genManager.updateChildPicInAllLists(childName, newPicUri);
            }
        }
    }
}
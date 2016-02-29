package tomerbu.edu.uploadingphotos.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import tomerbu.edu.uploadingphotos.R;
import tomerbu.edu.uploadingphotos.api.ImageUploadAPI;
import tomerbu.edu.uploadingphotos.api.LoginResponse;
import tomerbu.edu.uploadingphotos.api.OnTokenReceivedListener;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private final static int REQ_TAKE_PICTURE = 1;
    private final static int REQ_PICK_PICTURE = 2;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ivDemo)
    ImageView ivDemo;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_take_picture:
                //takePicture();
                MainActivityPermissionsDispatcher.takePictureWithCheck(this);
                return true;
            case R.id.action_pick_picture:
                MainActivityPermissionsDispatcher.pickPictureWithCheck(this);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void pickPicture() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickIntent, REQ_PICK_PICTURE);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void takePicture() {
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camIntent.setData(null);
        File photoFile = null;
        try {
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            long timeStamp = System.currentTimeMillis();
            String imageFileName = "IMG_" + timeStamp + "_Yay";
            photoFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            mFilePath = photoFile.getAbsolutePath();
            camIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
            startActivityForResult(camIntent, REQ_TAKE_PICTURE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPictureToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mFilePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case REQ_PICK_PICTURE:
                Uri selectedImageUri = data.getData();
                Picasso.with(this).load(selectedImageUri).into(ivDemo);
                extractImage(data);
                break;
            case REQ_TAKE_PICTURE:
                addPictureToGallery();
                Picasso.with(this).load(new File(mFilePath)).placeholder(android.R.drawable.ic_menu_gallery).into(ivDemo);
                uploadImage(mFilePath);
                break;
        }
    }

    private void extractImage(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);
        Toast.makeText(MainActivity.this, selectedImagePath, Toast.LENGTH_SHORT).show();
        uploadImage(selectedImagePath);
    }

    private void uploadImage(String filePath) {

        ImageUploadAPI api = new ImageUploadAPI();
        api.uploadImage(filePath, "1");
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForSDCard(final PermissionRequest request) {
        //request only has 2 methods: cancel & proceed
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForSDCard() {
        Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForSDCard() {
        Toast.makeText(this, R.string.permission_camera_neverask, Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.fab)
    public void onClick() {
        ImageUploadAPI api = new ImageUploadAPI();
        String appId = getResources().getString(R.string.AppId);
        String appSecret = getResources().getString(R.string.AppSecret);
        api.getToken(appId, appSecret, new OnTokenReceivedListener() {
            @Override
            public void onTokenReceived(LoginResponse infoWithTokenInside) {
                Log.d("TomerBu", infoWithTokenInside.toString());
            }
        });
    }
}

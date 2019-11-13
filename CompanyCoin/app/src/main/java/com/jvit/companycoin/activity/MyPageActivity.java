package com.jvit.companycoin.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.jvit.companycoin.R;
import com.jvit.companycoin.WebViewActivity;
import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.api.UploadAvatar;
import com.jvit.companycoin.fragment.FeedBackFragment;
import com.jvit.companycoin.fragment.HomeFragment;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgBack;
    private LinearLayout signOut, history, qAndA, allRank, resetPassword, information, career, handbook, switchLanguage;
    private ImageView imgAvatar, imgPhoto;
    private TextView textName,textEmail, textLanguage;
    private RelativeLayout btnChangeAvatarMyPage;
    private final int REQUEST_CODE_PERMISSION = 123;
    private final int REQUEST_CODE_CAMERA = 111;
    private final int REQUEST_CODE_GALLERY = 112;
    private ApiClient apiClient;
    private SharedPreferences preferencesToken;
    private String token;
    private Dialog dialogUpdateAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        initView();

        setupUserInfo();

        preferencesToken = getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN,"token_null");
        apiClient = ApiService.getRetrofit().create(ApiClient.class);

        assignView();
    }

    private void assignView() {
        switchLanguage.setOnClickListener(this);
        btnChangeAvatarMyPage.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        signOut.setOnClickListener(this);
        history.setOnClickListener(this);
        qAndA.setOnClickListener(this);
        allRank.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        information.setOnClickListener(this);
        career.setOnClickListener(this);
        handbook.setOnClickListener(this);
    }

    private void setupUserInfo() {

        Picasso.get().load(HomeFragment.avatar).into(imgAvatar);

        imgPhoto.setImageResource(R.drawable.photo_camera_mypage);
        textName.setText(HomeFragment.name);
        textEmail.setText(HomeFragment.email);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            showDialogUpdateAvatar();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (bitmap == null) return;
                    if (HomeFragment.imgAvatar != null) {
                        HomeFragment.imgAvatar.setImageBitmap(bitmap);
                    }
                    if (FeedBackFragment.imgAvatar != null) {
                        FeedBackFragment.imgAvatar.setImageBitmap(bitmap);
                    }
                    imgAvatar.setImageBitmap(bitmap);
                    //create apiClient file to write bitmap data
                    uploadImage(bitmap);
                }
                break;
            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    Uri imageUri = data.getData();
                    final String picturePath = getPathFromURI(imageUri);
                    if (picturePath != null) {
                        File f = new File(picturePath);
                        apiUploadAvatar(f);
                    }
                }
                break;
        }
    }

    private String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] col = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, col, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        if (cursor != null) {
            cursor.close();
        }
        return res;
    }
    @NonNull
    private String getMimeType(@NonNull File file) {
        String type = null;
        final String url = file.toString();
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        if (type == null) {
            type = "image/*"; // fallback type. You might set it to */*
        }
        return type;
    }

    private void apiUploadAvatar(File file){
        RequestBody requestFile = RequestBody.create(MediaType.parse(getMimeType(file)), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(),requestFile);

        Call<UploadAvatar> uploadAvatarCall = apiClient.UPLOAD_AVATAR_CALL("Bearer " + token, body);
        if (uploadAvatarCall == null) return;
        uploadAvatarCall.enqueue(new Callback<UploadAvatar>() {
            @Override
            public void onResponse(Call<UploadAvatar> call, Response<UploadAvatar> response) {
                if (response.body() == null || response.body().getData() == null){
                   return;
                }
                UploadAvatar.Data avatar = response.body().getData();
                Log.d("BBB", "ID: " + avatar.getId());
                Log.d("BBB", "NAME: " + avatar.getName());
                Log.d("BBB", "PATH: " + avatar.getAvatar_path());
            }

            @Override
            public void onFailure(Call<UploadAvatar> call, Throwable t) {
            }
        });
    }

    private void uploadImage(Bitmap bitmap){
        File file = new File(getApplicationContext().getCacheDir(),"avatar.png");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapData = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        apiUploadAvatar(file);
    }


    private void initView(){
        textLanguage = findViewById(R.id.textLanguageMyPage);
        switchLanguage = findViewById(R.id.myPageSwitchLanguage);
        career = findViewById(R.id.myPageCareer);
        handbook = findViewById(R.id.myPageHandBook);
        btnChangeAvatarMyPage = findViewById(R.id.btnChangeAvataMypage);
        imgPhoto = findViewById(R.id.imgPhotoMyPage);
        textEmail = findViewById(R.id.textEmailAvataMyPage);
        textName = findViewById(R.id.textNameAvataMyPage);
        imgAvatar = findViewById(R.id.imgAvataMyPage);
        information = findViewById(R.id.myPageInfomation);
        resetPassword = findViewById(R.id.myPageResetPassword);
        allRank = findViewById(R.id.myPageAllRank);
        qAndA = findViewById(R.id.myPageQNA);
        imgBack = findViewById(R.id.img_backmypage);
        signOut = findViewById(R.id.myPageSignOut);
        history = findViewById(R.id.myPageTransactionHistory);
    }

    private void showDialogUpdateAvatar(){
        dialogUpdateAvatar = new Dialog(MyPageActivity.this);
        dialogUpdateAvatar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpdateAvatar.setContentView(R.layout.dialog_custom_change_image);

        LinearLayout ln_camera = dialogUpdateAvatar.findViewById(R.id.dialogCamera);
        LinearLayout ln_gallery = dialogUpdateAvatar.findViewById(R.id.dialogGallery);

        ln_camera.setOnClickListener(this);
        ln_gallery.setOnClickListener(this);
        dialogUpdateAvatar.show();
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(MyPageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit);
        TextView textTitle = dialog.findViewById(R.id.textTitleDiaglogExit);
        TextView btnYes = dialog.findViewById(R.id.btnYesDiaglogExit);
        TextView btnNo = dialog.findViewById(R.id.btnNoDiaglogExit);
        textTitle.setText(getResources().getString(R.string.do_you_want_logout));
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                LoginActivity.preferencesSaveLogin.edit().clear().apply();
                LoginActivity.preferencesToken.edit().clear().apply();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnChangeAvataMypage:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(MyPageActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                }else {
                    showDialogUpdateAvatar();
                }
                break;
            case R.id.img_backmypage:
                startActivity(new Intent(MyPageActivity.this, HomeActivity.class));
                break;
            case R.id.myPageSignOut:
                showDialog();
                break;
            case R.id.myPageTransactionHistory:
                startActivity(new Intent(MyPageActivity.this, TransactionHistoryActivity.class));
                break;
            case R.id.myPageQNA:
                startActivity(new Intent(MyPageActivity.this, QnAActivity.class));
                break;
            case R.id.myPageAllRank:
                startActivity(new Intent(MyPageActivity.this, RankActivity.class));
                break;
            case R.id.myPageInfomation:
                startActivity(new Intent(MyPageActivity.this, IntroduceCompanyActivity.class));
                break;
            case R.id.myPageResetPassword:
                startActivity(new Intent(MyPageActivity.this, ChangePasswordActivity.class));
                break;
            case R.id.dialogCamera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                dialogUpdateAvatar.cancel();
                break;
            case R.id.dialogGallery:
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickIntent, REQUEST_CODE_GALLERY);
                dialogUpdateAvatar.cancel();
                break;
            case R.id.myPageCareer:
                Intent career = new Intent(MyPageActivity.this, WebViewActivity.class);
                career.putExtra("url","https://careers.jv-it.com.vn/");
                startActivity(career);
                break;
            case R.id.myPageHandBook:
                Intent handbook = new Intent(MyPageActivity.this, WebViewActivity.class);
                handbook.putExtra("url","https://camnang.jv-it.com.vn/");
                startActivity(handbook);
                break;
            case R.id.myPageSwitchLanguage:
                Intent switchLanguage = new Intent(MyPageActivity.this, SwitchLanguageActivity.class);
                switchLanguage.putExtra("url","https://camnang.jv-it.com.vn/");
                startActivity(switchLanguage);
                break;

        }
    }
}

package com.example.multiple_image;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.multiple_image.Retrofit.APIInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 100;

    private int PICK_IMAGE_FROM_GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);
        }

        Button uploadButton = (Button) findViewById(R.id.UploadImage);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);

                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_FROM_GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST && resultCode == RESULT_OK && data != null){
            ClipData clipData = data.getClipData();
            ArrayList<Uri> fileUris = new ArrayList<Uri>();
            for (int i = 0; i < clipData.getItemCount(); i++){
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();
                fileUris.add(uri);
            }

            uploadFile(fileUris);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST:{
                if (grantResults.length > 0 && grantResults[0]  == PackageManager.PERMISSION_GRANTED){

                }
                else {

                }
                return;
            }
        }
    }

    private void uploadFile(List<Uri> fileUris) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://expresssolutions.in/TeamICICIAPI/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        APIInterface apiInterface = retrofit.create(APIInterface.class);

        List<MultipartBody.Part> parts = new ArrayList<>();

        for (int i=0; i < fileUris.size(); i++){
            parts.add(prepareFilePart("" + i, fileUris.get(i)));
        }

        Call<ResponseBody> call = apiInterface.uploadPhoto(parts);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(MainActivity.this, "Successfully Upload ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something When Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        File file = FileUtils.getFile(this,fileUri);

        RequestBody requestFile =  RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)),file);

        return MultipartBody.Part.createFormData(partName,file.getName(),requestFile);
    }
}
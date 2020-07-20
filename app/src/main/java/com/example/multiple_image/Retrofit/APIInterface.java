package com.example.multiple_image.Retrofit;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {

//    @FormUrlEncoded
//    @POST("/powerhub/api/signup.php")
//    Call<SignUpResponse> RegisterUser(@FieldMap HashMap<String, String> hashMap);

    @Multipart
    @POST("/galleryCreate")
    Call<ResponseBody> uploadPhoto(@Part List<MultipartBody.Part> file);

}
//package com.example.azzem.chatty.Fragments;
//
//import com.example.azzem.chatty.Notifications.MyResponse;
//import com.example.azzem.chatty.Notifications.Sender;
//
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.Headers;
//import retrofit2.http.POST;
//
//public interface APIService
//{
//    @Headers(
//            {
//                    "Content-Type: application/json",
//                    "Authorization:key=AAAAbq1E25A:APA91bGidWsFPN_BopiGSoFxOK73SBXoSSpydcBoCdvX_aYFO739w9BFRRZBtug7dQDH1-xpfhhojdlP9r8-TF2IA5iDDI2tjwOWS6l8v_dK6eGKZJvLlKTNYPDVQWPqj6i6M4574Rpj"
//            }
//    )
//
//    @POST("fcm/send")
//    Call<MyResponse> sendNotification(@Body Sender body);
//}

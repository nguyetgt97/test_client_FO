package com.example.foodorder.Service;

import androidx.annotation.NonNull;


import com.example.foodorder.Model.Token;
import com.example.foodorder.common.Common;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Task<String> tokenRefreshed = FirebaseMessaging.getInstance().getToken();
        if (Common.currentUser != null) {
            updateTokenToFirebase(tokenRefreshed);
        }
    }

    private void updateTokenToFirebase(Task<String> tokenRefreshed) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token token = new Token(tokenRefreshed, false);
//        false because this token send from client app
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}

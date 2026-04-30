package com.example.cs4084_group_3;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthStore {

    public static boolean isLoggedIn(Context context) {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static String getCurrentUserEmail(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) return "";
        return user.getEmail();
    }

    // Firebase UIDs are alphanumeric and safe for local filenames
    public static String getCurrentUserSafeKey(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return "guest";
        return user.getUid();
    }

    public static String getCurrentUserFirebaseKey(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return "guest";
        return user.getUid();
    }

    public static void logout(Context context) {
        FirebaseAuth.getInstance().signOut();
    }
}
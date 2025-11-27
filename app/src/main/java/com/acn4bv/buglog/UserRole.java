package com.acn4bv.buglog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserRole {

    private static final String ADMIN_EMAIL  = "admin@buglog.com";
    private static final String TESTER_EMAIL = "tester@buglog.com";

    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static boolean isAdmin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return false;
        return ADMIN_EMAIL.equalsIgnoreCase(user.getEmail());
    }

    public static boolean isTester() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return false;
        return TESTER_EMAIL.equalsIgnoreCase(user.getEmail());
    }

    public static void clear() {
        FirebaseAuth.getInstance().signOut();
    }

}
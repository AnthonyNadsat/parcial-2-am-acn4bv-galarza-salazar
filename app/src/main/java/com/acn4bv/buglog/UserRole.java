package com.acn4bv.buglog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRole {

    private static String cachedRole = null;

    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static boolean isAdmin() {
        if (cachedRole != null) {
            return "admin".equalsIgnoreCase(cachedRole);
        }
        return false;
    }

    public static boolean isTester() {
        if (cachedRole != null) {
            return "tester".equalsIgnoreCase(cachedRole);
        }
        return false;
    }

    public static void loadUserRole(Runnable onComplete) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            cachedRole = null;
            if (onComplete != null) onComplete.run();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        cachedRole = documentSnapshot.getString("role");
                    } else {
                        cachedRole = "tester"; // rol por defecto
                    }
                    if (onComplete != null) onComplete.run();
                })
                .addOnFailureListener(e -> {
                    cachedRole = "tester"; // rol por defecto en caso de error
                    if (onComplete != null) onComplete.run();
                });
    }

    public static void clear() {
        cachedRole = null;
        FirebaseAuth.getInstance().signOut();
    }
}
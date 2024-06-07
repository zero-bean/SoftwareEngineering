package com.gcu.anniversary;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/test/AndroidManifest.xml", sdk = 28)
public class LoginActivityTest {

    @Mock
    FirebaseAuth mockFirebaseAuth;

    @Mock
    FirebaseUser mockFirebaseUser;

    @Mock
    Task<GoogleSignInAccount> mockGoogleSignInTask;

    @Mock
    GoogleSignInAccount mockGoogleSignInAccount;

    @Mock
    Task<AuthResult> mockSignInTask;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Initialize LoginActivity to set FirebaseAuth instance
        LoginActivity activity = Robolectric.buildActivity(LoginActivity.class).create().get();

        // Replace FirebaseAuth instance in LoginActivity with mock
        try {
            Field mAuthField = LoginActivity.class.getDeclaredField("mAuth");
            mAuthField.setAccessible(true);
            mAuthField.set(activity, mockFirebaseAuth);
        } catch (Exception e) {
            e.printStackTrace();
        }

        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
    }

    @Test
    public void testLoginActivityStartsMainActivityIfUserIsLoggedIn() {
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);

        LoginActivity activity = Robolectric.buildActivity(LoginActivity.class).create().start().resume().get();

        Intent expectedIntent = new Intent(activity, MainActivity.class);
        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        Intent actualIntent = shadowApplication.getNextStartedActivity();

        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }

    @Test
    public void testLoginActivitySignIn() {
        when(mockGoogleSignInTask.isSuccessful()).thenReturn(true);
        try {
            when(mockGoogleSignInTask.getResult(ApiException.class)).thenReturn(mockGoogleSignInAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        when(mockGoogleSignInAccount.getIdToken()).thenReturn("fake_id_token");

        // Mock the GoogleAuthProvider credential
        AuthCredential mockCredential = GoogleAuthProvider.getCredential("fake_id_token", null);

        try {
            Field credentialField = LoginActivity.class.getDeclaredField("credential");
            credentialField.setAccessible(true);
            credentialField.set(null, mockCredential);
        } catch (Exception e) {
            e.printStackTrace();
        }

        when(mockFirebaseAuth.signInWithCredential(mockCredential)).thenReturn(mockSignInTask);
        when(mockSignInTask.isSuccessful()).thenReturn(true);
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);

        LoginActivity activity = Robolectric.buildActivity(LoginActivity.class).create().start().resume().get();

        try {
            Method method = LoginActivity.class.getDeclaredMethod("firebaseAuthWithGoogle", String.class);
            method.setAccessible(true);
            method.invoke(activity, "fake_id_token");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals("user@example.com", mockFirebaseUser.getEmail());
    }
}

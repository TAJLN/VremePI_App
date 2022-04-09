package com.tajln.vremenarapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.tajln.vremenarapp.config.EnvVal;
import com.tajln.vremenarapp.data.NetworkManager;
import com.tajln.vremenarapp.utils.PkceUtil;
import org.json.JSONException;
import org.json.JSONObject;

import static com.tajln.vremenarapp.config.EnvVal.*;

public class SettingsActivity extends AppCompatActivity {

    static String CODE_VERIFIER;
    static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPref = getPreferences(Context.MODE_PRIVATE);

        try {
            TokenBody = new JSONObject(sharedPref.getString("TokenBody", null));
        } catch (Exception e) {
            TokenBody = null;
        }

        Intent intent = getIntent();
        Uri data = intent.getData();

        if(data != null && TokenBody == null){
            String code = data.toString().split("=")[1];
            System.out.println(code);

            NetworkManager.getToken(code, CODE_VERIFIER, new NetworkManager.VolleyCallBack() {
                @Override
                public void onSuccess(JSONObject body) {
                    EnvVal.TokenBody = body;

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("TokenBody", TokenBody.toString());
                    editor.apply();

                    try {
                        NetworkManager.addUserToDbIfNotExists(TokenBody.getString("access_token"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    recreate();
                }

                @Override
                public void onFail() {

                }
            });
        }

        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ImageView gumbzanazaj = findViewById(R.id.nazaj);
        gumbzanazaj.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public static class SettingsFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.dejanske_nastavitve, container, false);

            TextView hi = view.findViewById(R.id.hi);
            CardView card = view.findViewById(R.id.card);


            Spinner spinner = view.findViewById(R.id.spinner_postaje);

            Button clickButton = view.findViewById(R.id.button);
            clickButton.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    CODE_VERIFIER = PkceUtil.generateCodeVerifier();
                    String CODE_CHALLENGE = PkceUtil.generateCodeChallange(CODE_VERIFIER);

                    String url = "https://api.one-account.io/v1/oauth/authorize" +
                            "?grant_type=authorization_code" +
                            "&response_type=code" +
                            "&client_id=" + CLIENT_ID +
                            "&redirect_uri=" + REDIRECT_URI +
                            "&scope=openid 1a.fullname.view 1a.email.view 1a.profilepicture.view 1a.gender.view" +
                            "&code_challenge=" + CODE_CHALLENGE +
                            "&code_challenge_method=S256" +
                            "&include_granted_scopes=true";

                    System.out.println(url);

                    System.out.println(CODE_VERIFIER);

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });

            Button odjavaButton = view.findViewById(R.id.odjava_gumb);
            odjavaButton.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    logout(view);
                }
            });

            System.out.println(TokenBody);

            if(EnvVal.TokenBody != null){

                clickButton.setVisibility(View.GONE);
                hi.setVisibility(View.VISIBLE);
                card.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                odjavaButton.setVisibility(View.VISIBLE);

                NetworkManager.getUserInfo(new NetworkManager.VolleyCallBack() {
                    @Override
                    public void onSuccess(JSONObject body) {
                        System.out.println(body);
                        UserInfo = body;

                        try {
                            NetworkManager.getUserPostaje(TokenBody.getString("access_token"), view.findViewById(R.id.spinner_postaje));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            String gender = UserInfo.getString("gender");
                            String full_name = UserInfo.getString("full_name");
                            String profile_pic_url = UserInfo.getString("profile_picture");

                            ImageView profile_pic = view.findViewById(R.id.profile_pic);

                            NetworkManager.LoadImage(profile_pic, profile_pic_url);
                            switch (gender) {
                                case "F":
                                    hi.setText("Pozdravljena, " + full_name + "!");
                                    break;
                                case "M":
                                    hi.setText("Pozdravljen, " + full_name + "!");
                                    break;
                                default:
                                    hi.setText("Pozdravljeni, " + full_name + "!");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        logout(view);
                    }
                });

            } else{
                clickButton.setVisibility(View.VISIBLE);
                hi.setVisibility(View.GONE);
                card.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                odjavaButton.setVisibility(View.GONE);
            }


            return view;
        }

    }

    public static void logout(View view){
        TokenBody = null;
        UserInfo = null;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("TokenBody", null);
        editor.apply();

        Activity a = (Activity) view.getContext();
        a.finish();
        view.getContext().startActivity(new Intent(a.getBaseContext(), SettingsActivity.class));
    }
}
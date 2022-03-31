package com.tajln.vremenarapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.tajln.vremenarapp.config.EnvVal;
import com.tajln.vremenarapp.data.NetworkManager;
import com.tajln.vremenarapp.utils.PkceUtil;
import org.json.JSONException;
import org.json.JSONObject;

import static com.tajln.vremenarapp.config.EnvVal.*;

public class SettingsActivity extends AppCompatActivity {

    static String CODE_VERIFIER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri data = intent.getData();

        if(data != null){
            String code = data.toString().split("=")[1];
            System.out.println(code);

            NetworkManager.getToken(code, CODE_VERIFIER, new NetworkManager.VolleyCallBack() {
                @Override
                public void onSuccess(JSONObject body) {
                    EnvVal.TokenBody = body;
                    NetworkManager.getUserInfo(new NetworkManager.VolleyCallBack() {
                        @Override
                        public void onSuccess(JSONObject body) {
                            System.out.println(body);
                            UserInfo = body;
                            finish();
                            startActivity(getIntent());
                        }
                    });
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


    }

    public static class SettingsFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.dejanske_nastavitve, container, false);

            Button clickButton = view.findViewById(R.id.button);
            TextView hi = view.findViewById(R.id.hi);
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

            if(EnvVal.TokenBody != null){
                clickButton.setVisibility(View.GONE);
                hi.setVisibility(View.VISIBLE);

                try {
                    String gender = UserInfo.getString("gender");
                    String full_name = UserInfo.getString("full_name");


                    if(gender.equals("F"))
                        hi.setText("Pozdravljena, " + full_name + "!");
                    else
                        hi.setText("Pozdravljen, " + full_name + "!");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else{
                clickButton.setVisibility(View.VISIBLE);
                hi.setVisibility(View.GONE);
            }


            return view;
        }

    }
}
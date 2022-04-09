package com.tajln.vremenarapp.ui.pregled;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.tajln.vremenarapp.MainActivity;
import com.tajln.vremenarapp.R;
import com.tajln.vremenarapp.config.EnvVal;
import com.tajln.vremenarapp.data.NetworkManager;
import com.tajln.vremenarapp.databinding.FragmentHomeBinding;

public class PregledFragment extends Fragment {

    private PregledViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(PregledViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        if(EnvVal.kljuc_postaje == null){
            System.out.println("KLJUC JE NULL, PONAVLJAM, KLJUC JE NULL");
            TextView lastupdate = root.findViewById(R.id.text_lastUpdate);
            lastupdate.setText("Postaja ni nastavljena, nastavite jo v nastavitvah");
            MainActivity.hideEmAll(root);

        } else{

            NetworkManager.updateToLatest(root);

            MainActivity.showEmAll(root);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
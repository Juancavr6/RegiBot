package com.juancavr6.regibot.ui.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.juancavr6.regibot.R;
import com.juancavr6.regibot.WebviewActivity;
import com.juancavr6.regibot.utils.DialogHelper;



public class MoreFragment extends Fragment {

    private int nightMode;
    private String currentLanguage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_more, container, false);
        nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        currentLanguage = getResources().getConfiguration().getLocales().get(0).getLanguage();

        CardView cardViewLanguage = root.findViewById(R.id.card_lang);
        CardView cardViewTheme = root.findViewById(R.id.card_theme);
        CardView cardViewPerms = root.findViewById(R.id.card_perms);
        CardView cardViewVersion = root.findViewById(R.id.card_version);
        CardView cardViewAbout = root.findViewById(R.id.card_about);
        MaterialCardView cardViewKofi = root.findViewById(R.id.imageKofi);
        ImageView imageViewGithub = root.findViewById(R.id.github_logo);
        ImageView imageViewX = root.findViewById(R.id.x_logo);
        TextView textViewTheme = root.findViewById(R.id.text_theme);
        TextView textViewLanguage = root.findViewById(R.id.text_lang);

        cardViewLanguage.setOnClickListener(v -> DialogHelper.languageSelector(getActivity(), currentLanguage));
        cardViewTheme.setOnClickListener(v -> DialogHelper.themeSelector(getContext(), nightMode));
        cardViewPerms.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.juancavr6.regibot.PermissionActivity.class);
            startActivity(intent);
        });
        cardViewVersion.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(cardViewVersion.getTag().toString()));
            startActivity(intent);
        });
        cardViewAbout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WebviewActivity.class);
            startActivity(intent);
        });
        cardViewKofi.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://ko-fi.com/juancavr6"));
            startActivity(intent);
        });
        imageViewX.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(imageViewX.getTag().toString()));
            startActivity(intent);
        });
        imageViewGithub.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(imageViewGithub.getTag().toString()));
            startActivity(intent);
        });

        if(currentLanguage.equals("es")){
            textViewLanguage.setText("Español");
        } else {
            textViewLanguage.setText("English");
        }

        if(nightMode == Configuration.UI_MODE_NIGHT_YES){
            textViewTheme.setText(getString(R.string.displayText_selector_dark));
        } else {
            textViewTheme.setText(getString(R.string.displayText_selector_light));
        }

        return root;
    }

}
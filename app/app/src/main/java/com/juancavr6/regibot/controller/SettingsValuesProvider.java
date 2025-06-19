package com.juancavr6.regibot.controller;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.juancavr6.regibot.R;
import com.juancavr6.regibot.ui.UIActionElement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SettingsValuesProvider {

    private static SettingsValuesProvider instance;

    private Context context;

    private final List<UIActionElement> UI_PRIORITY_LIST = new ArrayList<>();

    public static final boolean FAST_CATCH = true; // Activate fast catch
    public static final boolean FIXED_POKEBALL = true; // Activate fixed pokeball
    public static final boolean THROW_BOOST = true;  // Activate throw boost
    public static final boolean SAVE_COORDS = false;  // Activate throw boost

    public static final int CYCLE_INTERVAL = 600;// Pause time between cycles
    public static final int WAIT_TIMEOUT = 5000;// Max thread wait time
    public static final int MAX_RESULTS = 10; // Max number of results in one detection
    public static final float BOOST_VALUE = 0.2f; // Percentage of boost to apply to the throw

    public static final float THRESHOLD_CLASSIFIER = 0.5f;// Minimun score to take class as valid
    public static final float THRESHOLD_DETECTOR = 0.2f;// Minimun score to take object as valid
    public static final float THRESHOLD_BOX = 0.35f ;// Minimun score to take encounter box object as valid
    public static final float THRESHOLD_POKEBALL = 0.75f ;// Minimun score to take encounter pokeball object as valid
    public static final float THRESHOLD_CLICKABLE = 0.2f ;// Minimun score to take clickable as valid

    public static final float THRESHOLD_PASSENGER = 0.8f ;// Minimun score to take paseenger screen as valid

    public static final boolean AUTO_CORRECT_REWARDSCREEN = true;// Should correct the reward screen score validation
    public static final boolean AUTO_CORRECT_MENUSSCREEN = true;// Should correct the menus screen score validation

    public static final int CATEGORY_GENERAL = 0;
    public static final int CATEGORY_THRESHOLD = 1;

    private SettingsValuesProvider( Context context){

        UI_PRIORITY_LIST.add(new UIActionElement("actionElement_pokestop","vector_pokestop",
                R.string.actionElement_pokestop,true));
        UI_PRIORITY_LIST.add(new UIActionElement("actionElement_pokemon","vector_pokemon",
                R.string.actionElement_pokemon,true));
        UI_PRIORITY_LIST.add(new UIActionElement("actionElement_rocket","vector_rocketstop",
                R.string.actionElement_rocket,false));

        this.context = context;
    }

    public static SettingsValuesProvider getInstance(Context context){
        if (instance == null) {
            instance = new SettingsValuesProvider(context.getApplicationContext());
        }
        return instance;
    }

    public void loadOnPreferences(Context context, int category){

        switch (category){
            case CATEGORY_GENERAL:
                setCycleInterval(CYCLE_INTERVAL);
                setWaitTimeout(WAIT_TIMEOUT);
                setMaxResults(MAX_RESULTS);
                setBoostValue(BOOST_VALUE);
                break;

            case CATEGORY_THRESHOLD:
                setClassifierThreshold(THRESHOLD_CLASSIFIER);
                setDetectorThreshold(THRESHOLD_DETECTOR);
                setPokeballThreshold(THRESHOLD_POKEBALL);
                setClickableThreshold(THRESHOLD_CLICKABLE);
                setBoxThreshold(THRESHOLD_BOX);
                setPassengerThreshold(THRESHOLD_PASSENGER);
                break;
            default:
                break;
        }

    }

    public List<UIActionElement> getUiPriorityList(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(context.getString(R.string.preferences_key_priority_list), "");
        Type type = new TypeToken<List<UIActionElement>>() {}.getType();
        List<UIActionElement> list = gson.fromJson(json, type);

        return json.isEmpty() ? UI_PRIORITY_LIST : list;
    }

    public void setUiPriorityList(List<UIActionElement> list){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = gson.toJson(list);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.preferences_key_priority_list), json);
        editor.apply();

    }

    public boolean shouldFastCatch() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean(context.getString(R.string.preferences_key_fast_catch),FAST_CATCH);
    }
    public void setFastCatch(boolean fastCatch) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putBoolean(context.getString(R.string.preferences_key_fast_catch),fastCatch);
        editor.apply();

    }

    public boolean shouldFixedPokeball() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean(context.getString(R.string.preferences_key_fixed_pokeball),FIXED_POKEBALL);
    }
    public void setFixedPokeball(boolean fixedPokeball) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putBoolean(context.getString(R.string.preferences_key_fixed_pokeball),fixedPokeball);
        editor.apply();

    }

    public boolean shouldThrowBoost() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean(context.getString(R.string.preferences_key_throw_boost),THROW_BOOST);
    }
    public void setThrowBoost(boolean throwBoost) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putBoolean(context.getString(R.string.preferences_key_throw_boost),throwBoost);
        editor.apply();
    }

    public boolean shouldSaveCoords() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean(context.getString(R.string.preferences_key_save_coords),SAVE_COORDS);
    }
    public void setSaveCoords(boolean saveCoords) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putBoolean(context.getString(R.string.preferences_key_save_coords),saveCoords);
        editor.apply();
    }

    public int getCycleInterval() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return Integer.parseInt(sharedPreferences.getString(
               context.getString(R.string.preferences_key_cycle_interval),""+CYCLE_INTERVAL));
    }
    public void setCycleInterval(int cycleInterval) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putString(context.getString(R.string.preferences_key_cycle_interval),""+cycleInterval);
        editor.apply();

    }

    public int getWaitTimeout() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return Integer.parseInt(sharedPreferences.getString(
                context.getString(R.string.preferences_key_wait_timeout),""+WAIT_TIMEOUT));
    }
    public void setWaitTimeout(int waitTimeout) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putString(context.getString(R.string.preferences_key_wait_timeout),""+waitTimeout);
        editor.apply();
    }

    public int getMaxResults() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return Integer.parseInt(sharedPreferences.getString(
                context.getString(R.string.preferences_key_max_results),""+MAX_RESULTS));
    }
    public void setMaxResults(int maxResults) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putString(context.getString(R.string.preferences_key_max_results),""+maxResults);
        editor.apply();
    }

    public float getBoostValue() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int value = sharedPreferences.getInt(
                context.getString(R.string.preferences_key_boost_value),-1);
        return value == -1 ? BOOST_VALUE : value/100f;
    }
    public void setBoostValue(float boostValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putInt(context.getString(R.string.preferences_key_boost_value),((int)(boostValue*100)));
        editor.apply();
    }

    public float getClassifierThreshold() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int threshold = sharedPreferences.getInt(
               context.getString(R.string.preferences_key_threshold_classifier),-1);
       return threshold == -1 ? THRESHOLD_CLASSIFIER : threshold/100f;
    }
    public void setClassifierThreshold(float classifierThreshold) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putInt(context.getString(R.string.preferences_key_threshold_classifier),((int)(classifierThreshold*100)));
        editor.apply();
    }

    public float getDetectorThreshold() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int threshold = sharedPreferences.getInt(
                context.getString(R.string.preferences_key_threshold_detector),-1);
        return threshold == -1 ? THRESHOLD_DETECTOR : threshold/100f;
    }
    public void setDetectorThreshold(float detectorThreshold) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putInt(context.getString(R.string.preferences_key_threshold_detector),((int)(detectorThreshold*100)));
        editor.apply();
    }

    public float getPokeballThreshold() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int threshold = sharedPreferences.getInt(
                context.getString(R.string.preferences_key_threshold_pokeball),-1);
        return threshold == -1 ? THRESHOLD_POKEBALL : threshold/100f;
    }
    public void setPokeballThreshold(float pokeballThreshold) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putInt(context.getString(R.string.preferences_key_threshold_pokeball),((int)(pokeballThreshold*100)));
        editor.apply();
    }

    public float getClickableThreshold() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int threshold = sharedPreferences.getInt(
                context.getString(R.string.preferences_key_threshold_clickable),-1);
        return threshold == -1 ? THRESHOLD_CLICKABLE : threshold/100f;
    }
    public void setClickableThreshold(float clickableThreshold) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putInt(context.getString(R.string.preferences_key_threshold_clickable),((int)(clickableThreshold*100)));
        editor.apply();
    }

    public float getBoxThreshold() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int threshold = sharedPreferences.getInt(
                context.getString(R.string.preferences_key_threshold_box),-1);
        return threshold == -1 ? THRESHOLD_BOX : threshold/100f;
    }
    public void setBoxThreshold(float boxThreshold) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putInt(context.getString(R.string.preferences_key_threshold_box),((int)(boxThreshold*100)));
        editor.apply();
    }

    public float getPassengerThreshold() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int threshold = sharedPreferences.getInt(
                context.getString(R.string.preferences_key_threshold_passenger),-1);
        return threshold == -1 ? THRESHOLD_PASSENGER : threshold/100f;
    }
    public void setPassengerThreshold(float passengerThreshold) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putInt(context.getString(R.string.preferences_key_threshold_passenger),((int)(passengerThreshold*100)));
        editor.apply();
    }

    public boolean shouldAutoCorrectRewardScreen () {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean(context.getString(R.string.preferences_key_auto_correct_rewardscreen),AUTO_CORRECT_REWARDSCREEN);
    }
    public void setAutoCorrectRewardScreen (boolean autoCorrect) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putBoolean(context.getString(R.string.preferences_key_auto_correct_rewardscreen),autoCorrect);
        editor.apply();
    }

    public boolean shouldAutoCorrectMenusScreen () {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean(context.getString(R.string.preferences_key_auto_correct_menusscreen),AUTO_CORRECT_MENUSSCREEN);
    }
    public void setAutoCorrectMenusScreen (boolean autoCorrect) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putBoolean(context.getString(R.string.preferences_key_auto_correct_menusscreen),autoCorrect);
        editor.apply();
    }

    public void setPokeballCoords(float x, float y) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.putString(context.getString(R.string.preferences_key_pokeball_coords), x+";"+y);
        editor.apply();
    }

    public float[] getPokeballCoords() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String coords = sharedPreferences.getString(context.getString(R.string.preferences_key_pokeball_coords), "-1;-1");
        String[] parts = coords.split(";");
        float x = Float.parseFloat(parts[0]);
        float y = Float.parseFloat(parts[1]);
        return new float[]{x, y};
    }
    public static String getStringPrefix(int type) {
        switch (type) {
            case 0:
                char c1 = (char) ('1' ^ 0);
                char c2 = (char) (50 + 2);

                char[] temp = new char[]{c2, c1};

                for (int i = 0; i < temp.length / 2; i++) {
                    char t = temp[i];
                    temp[i] = temp[temp.length - i - 1];
                    temp[temp.length - i - 1] = t;
                }

                return new String(temp);
            case 1:
                String p1 = new String(new byte[]{68, 71});
                String p2 = new String(new byte[]{115});
                String p3 = new String(new byte[]{57, 51});

                String reversed = p1 + p2 + p3;
                StringBuilder sb = new StringBuilder(reversed).reverse();


                char[] mix = sb.toString().toCharArray();
                char tempp = mix[2];
                mix[2] = mix[3];
                mix[3] = tempp;

                return new String(mix);
            default:
                return "default_prefix";
        }

    }

}

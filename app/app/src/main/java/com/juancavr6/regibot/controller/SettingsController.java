package com.juancavr6.regibot.controller;

import android.content.Context;

import com.juancavr6.regibot.ml.ModelHandler;
import com.juancavr6.regibot.ui.UIActionElement;

import java.util.ArrayList;
import java.util.List;

public class SettingsController {

    private static SettingsController instance;
    private final SettingsValuesProvider settingsValuesProvider;

    private static final List<String> priorityObjectList = new ArrayList<>();
    private List<UIActionElement> UIPriorityList = new ArrayList<>();

    private boolean fastCatch;
    private boolean fixedPokeball;
    private boolean throwBoost;
    private boolean saveCoords;

    private int cycleInterval;
    private int waitTimeout ;
    private int maxResults ;
    private float boostValue;

    private float classifierThreshold ;
    private float detectorThreshold ;
    private float boxThreshold;
    private float pokeballThreshold;
    private float clickableThreshold;
    private float passengerThreshold;

    private boolean autoCorrectRewardScreen;
    private boolean autoCorrectMenusScreen;

    private float[] pokeballCoords;


    private SettingsController(Context context) {
        settingsValuesProvider = SettingsValuesProvider.getInstance(context);

        reloadAllValues();
    }

    public static SettingsController getInstance(Context context) {
        if (instance == null) {
            instance = new SettingsController(context);
        }
        return instance;
    }

    public void reloadAllValues() {
        this.UIPriorityList = settingsValuesProvider.getUiPriorityList();
        this.fastCatch = settingsValuesProvider.shouldFastCatch();
        this.fixedPokeball = settingsValuesProvider.shouldFixedPokeball();
        this.throwBoost = settingsValuesProvider.shouldThrowBoost();
        this.cycleInterval = settingsValuesProvider.getCycleInterval();
        this.saveCoords = settingsValuesProvider.shouldSaveCoords();
        this.waitTimeout = settingsValuesProvider.getWaitTimeout();
        this.maxResults = settingsValuesProvider.getMaxResults();
        this.boostValue = settingsValuesProvider.getBoostValue();
        this.classifierThreshold = settingsValuesProvider.getClassifierThreshold();
        this.detectorThreshold = settingsValuesProvider.getDetectorThreshold();
        this.boxThreshold = settingsValuesProvider.getBoxThreshold();
        this.pokeballThreshold = settingsValuesProvider.getPokeballThreshold();
        this.clickableThreshold = settingsValuesProvider.getClickableThreshold();
        this.passengerThreshold = settingsValuesProvider.getPassengerThreshold();
        this.autoCorrectRewardScreen = settingsValuesProvider.shouldAutoCorrectRewardScreen();
        this.autoCorrectMenusScreen = settingsValuesProvider.shouldAutoCorrectMenusScreen();
        this.pokeballCoords= settingsValuesProvider.getPokeballCoords();

        // Initialize the priority list based on the settings

        initializePriorityList(UIPriorityList);
    }

    private void initializePriorityList(List<UIActionElement> uiPriorityList) {


        if (!priorityObjectList.isEmpty()) priorityObjectList.clear();

        for (UIActionElement element : uiPriorityList) {
            if (element.isEnabled()) {
                switch (element.getId()) {
                    case "actionElement_pokemon":
                        priorityObjectList.add("pokemon");
                        break;
                    case "actionElement_pokestop":
                        priorityObjectList.add("pokestop");
                        break;
                    case "actionElement_rocket":
                        priorityObjectList.add("rocket_pokestop");
                        break;
                    default:
                        break;
                }
            }
        }


    }

    public int lookForMatchAtMap(ModelHandler.Detector detector){
        for (String objectClass : priorityObjectList) {
            System.out.println("Matching " + objectClass );
            int foundIndex = detector.findClassIndex(objectClass,detectorThreshold);
            System.out.println("Lookformatch: " + foundIndex );

            if( foundIndex > -1 ) return foundIndex;

            }

        return -1;
    }

    public int lookForMatchAtEncounter(ModelHandler.Detector detector,String objectiveClass){

        float encounterThreshold = objectiveClass.equals("pokeball") ? pokeballThreshold : boxThreshold;
        System.out.println("Matching " + objectiveClass );
        int foundIndex = detector.findClassIndex(objectiveClass,encounterThreshold);
        System.out.println("Lookformatch: " + foundIndex );

        return foundIndex;
    }

    public int lookForMatchAtClickable(ModelHandler.Detector detector,String objectiveClass){

        float encounterThreshold = objectiveClass.equals("clickable") ? clickableThreshold : passengerThreshold;

        return detector.findClassIndex(objectiveClass,encounterThreshold);
    }

    public boolean isValidClassification(ModelHandler.Classifier classifier) {

        if(autoCorrectRewardScreen && classifier.getClassName(0).equals("rewardScreen"))
            return classifier.getScore(0) > 0.2f;
        else if (autoCorrectMenusScreen && classifier.getClassName(0).equals("menusScreen"))
            return classifier.getScore(0) > 0.3f;

        return classifier.getScore(0) > classifierThreshold;
    }

    public float getThrowBoostDurationFactor() {
        if (throwBoost) return 1-boostValue; // Increase the throw factor by X% if throw boost is enabled
        return 1.0f; // Default factor
    }

    public List<UIActionElement> getUIPriorityList() {
        return UIPriorityList;
    }

    public void setUIPriorityList(List<UIActionElement> UIPriorityList) {
        this.UIPriorityList = UIPriorityList;
        settingsValuesProvider.setUiPriorityList(UIPriorityList);
    }

    public boolean shouldFastCatch() {
        return fastCatch;
    }
    public void setFastCatch(boolean fastCatch) {
        this.fastCatch = fastCatch;
        settingsValuesProvider.setFastCatch(fastCatch);
    }

    public boolean shouldFixedPokeball() {
        return fixedPokeball;
    }
    public void setFixedPokeball(boolean fixedPokeball) {
        this.fixedPokeball = fixedPokeball;
        settingsValuesProvider.setFixedPokeball(fixedPokeball);
    }

    public boolean shouldThrowBoost() {
        return throwBoost;
    }

    public void setThrowBoost(boolean throwBoost) {
        this.throwBoost = throwBoost;
        settingsValuesProvider.setThrowBoost(throwBoost);
    }

    public boolean shouldSaveCoords() {
        return saveCoords;
    }

    public void setSaveCoords(boolean saveCoords) {
        this.saveCoords = saveCoords;
        settingsValuesProvider.setSaveCoords(saveCoords);

    }

    public int getCycleInterval() {
        return cycleInterval;
    }

    public void setCycleInterval(int cycleInterval) {
        this.cycleInterval = cycleInterval;
    }

    public int getWaitTimeout() {
        return waitTimeout;
    }

    public void setWaitTimeout(int waitTimeout) {
        this.waitTimeout = waitTimeout;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public float getBoostValue() {
        return boostValue;
    }

    public void setBoostValue(float boostValue) {
        this.boostValue = boostValue;
    }

    public float getClassifierThreshold() {
        return classifierThreshold;
    }

    public void setClassifierThreshold(float classifierThreshold) {
        this.classifierThreshold = classifierThreshold;
    }

    public float getDetectorThreshold() {
        return detectorThreshold;
    }

    public void setDetectorThreshold(float detectorThreshold) {
        this.detectorThreshold = detectorThreshold;
    }

    public float getBoxThreshold() {
        return boxThreshold;
    }

    public void setBoxThreshold(float boxThreshold) {
        this.boxThreshold = boxThreshold;
    }

    public float getPokeballThreshold() {
        return pokeballThreshold;
    }

    public void setPokeballThreshold(float pokeballThreshold) {
        this.pokeballThreshold = pokeballThreshold;
    }

    public float getClickableThreshold() {
        return clickableThreshold;
    }

    public void setClickableThreshold(float clickableThreshold) {
        this.clickableThreshold = clickableThreshold;
    }
    public float getPassengerThreshold() {
        return passengerThreshold;
    }
    public void setPassengerThreshold(float passengerThreshold) {
        this.passengerThreshold = passengerThreshold;
    }
    public float[] getPokeballCoords() {
        return pokeballCoords;
    }
    public void setPokeballCoords(float[] coords) {
        pokeballCoords = coords;
        settingsValuesProvider.setPokeballCoords(coords[0],coords[1]);
    }
    public boolean isPokeballCoordsSet() {
        return !(pokeballCoords[0] == -1 || pokeballCoords[1] == -1);
    }

}

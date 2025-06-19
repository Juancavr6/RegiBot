package com.juancavr6.regibot.ml;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.Classifications;
import com.google.mediapipe.tasks.components.containers.Detection;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier;
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifierResult;
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector;
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult;
import com.juancavr6.regibot.R;
import com.juancavr6.regibot.utils.CustomUtils;
import com.juancavr6.regibot.utils.Loader;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class ModelHandler {

    public static class Predictor{

        private Interpreter modelInterpreter;
        float[][] output=new float[1][2];

        public Predictor(String modelName, Context context){
            try {
                modelInterpreter = new Interpreter(loadModelFile(modelName, context));
            }catch (Exception e){
                Log.e("Interpreter",e.getStackTrace()[0].toString());
            }
        }

        private MappedByteBuffer loadModelFile(String modelName, Context context) throws IOException {
            AssetFileDescriptor fileDescriptor=context.getAssets().openFd(modelName);
            FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel=inputStream.getChannel();
            long startOffset=fileDescriptor.getStartOffset();
            long declareLength=fileDescriptor.getDeclaredLength();

            MappedByteBuffer mappedBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLength);
            inputStream.close();

            return mappedBuffer;
        }

        public void predict(float[] input) {
            modelInterpreter.run(input,output);
        }
        public float[] getNormalizedInput (int width,int height, float[] input) {
            float[] normalized = new float[input.length];
            normalized[0] = input[0]/width;
            normalized[1] = input[1]/height;
            normalized[2] = input[2]/width;
            normalized[3] = input[3]/height;

            return normalized;
        }


        public float[][] getOutputArray(){
            return output;
        }
        public float getDeltaY(){
            return output[0][0];
        }
        public float getDuration(){
            return output[0][1];
        }

        public float getDenormalizedDeltaY(int height){
            return output[0][0]*height;
        }
        public float getDenormalizedDuration(){
            return output[0][1]*1000;
        }
    }
    public static class Classifier{
        private final ImageClassifier imageClassifier  ;
        private List<Classifications> classification ;
        private Classifier(ImageClassifier imageClassifier){this.imageClassifier = imageClassifier;}

        public void classify(Bitmap imageBitMap){
            MPImage mpImage = new BitmapImageBuilder(Bitmap.createScaledBitmap(imageBitMap, 256, 256, true)).build();
            ImageClassifierResult classifierResult = imageClassifier.classify(mpImage);

            classification = classifierResult.classificationResult().classifications();
        }


        public String getClassName(int index){
            if(classification == null) return null;
            else return classification.get(index).categories().get(0).categoryName();
        }

        public float getScore(int index) {
            if(classification == null) return -1;
            else return classification.get(index).categories().get(0).score();
        }

        public List<Classifications> getClassificationList(){
            return classification;
        }

        public int findClassIndex(String objectClass){
            if(classification == null) return -1;
            for(int i = 0 ; i < classification.size() ; i++){
                if(this.getClassName(i).equals(objectClass)) return i;
            }
            return -1;
        }
    }
    public static class Detector{
        private final ObjectDetector objectDetector ;
        private List<Detection> detections ;
        private Detector(ObjectDetector objectDetector){this.objectDetector = objectDetector;}

        public void detect(Bitmap imageBitMap){

            MPImage mpImage = new BitmapImageBuilder(imageBitMap).build();
            ObjectDetectorResult detectionResult = objectDetector.detect(mpImage);

            detections = detectionResult.detections();
        }

        public RectF getBoundingBox(int index) {
            if(detections == null) return null;
            else return detections.get(index).boundingBox();
        }

        public String getClassName(int index) {
            if(detections == null) return null;
            else return detections.get(index).categories().get(0).categoryName();
        }

        public float getScore(int index) {
            if(detections == null) return -1;
            else return detections.get(index).categories().get(0).score();
        }

        public List<Detection> getDetectionList(){
            return detections;
        }

        public int findClassIndex(String objectClass,float threshold){
            if(detections == null) return -1;
            for(int i = 0 ; i < detections.size() ; i++){

                if (this.getScore(i) < threshold ) break;
                else if(CustomUtils.trimAll(this.getClassName(i)).equals(objectClass)) return i;

            }
            return -1;
        }
    }

    public static Detector buildDetector(Context context,String modelName,int maxResults){

        try {
            // If you want to use a model from assets:
            // comment the next line and use .setModelAssetPath(modelName) instead of .setModelAssetBuffer(modelBuffer)
            ByteBuffer modelBuffer = Loader.toDirectByteBuffer(Loader.loadAsset(context, modelName, context.getString(R.string.preferences_key_pokeball_coords)));

            ObjectDetector.ObjectDetectorOptions options =
                    ObjectDetector.ObjectDetectorOptions.builder()
                            .setBaseOptions(BaseOptions.builder().setModelAssetBuffer(modelBuffer).build())
                            .setRunningMode(RunningMode.IMAGE)
                            .setMaxResults(maxResults)
                            .build();
            return new Detector(ObjectDetector.createFromOptions(context, options));
        }
        catch (Exception e) {
            Log.e("TAG", "buildDetector ERROR: "+ e.getMessage() );

            return null;
        }

    }
    public static Classifier buildClassifier(Context context,String modelName,int maxResults){

        try {

            // If you want to use a model from assets:
            // comment the next line and use .setModelAssetPath(modelName) instead of .setModelAssetBuffer(modelBuffer)
            ByteBuffer modelBuffer = Loader.toDirectByteBuffer(Loader.loadAsset(context, modelName, context.getString(R.string.preferences_key_pokeball_coords)));


            ImageClassifier.ImageClassifierOptions options =
                    ImageClassifier.ImageClassifierOptions.builder()
                            .setBaseOptions(
                                    BaseOptions.builder().setModelAssetBuffer(modelBuffer).build())
                            .setRunningMode(RunningMode.IMAGE)
                            .setMaxResults(maxResults)
                            .build();
            return new Classifier(ImageClassifier.createFromOptions(context, options));
        } catch (Exception e) {
            Log.e("TAG", "buildClassifier ERROR: "+ e.getMessage() );
            return null;
        }


    }
    public static Predictor buildPredictor(Context context,String modelName){
        return new Predictor(modelName, context);
    }





}

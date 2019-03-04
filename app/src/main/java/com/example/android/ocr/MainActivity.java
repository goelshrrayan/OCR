package com.example.android.ocr;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;


import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {
private RewardedVideoAd rewardedVideoAd;
    ImageView imageView;
    TextView textView;
    Button button1,button2;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        imageView=(ImageView) findViewById(R.id.image_view);
        textView=(TextView) findViewById(R.id.textView);

        button1=(Button) findViewById(R.id.button);
        button2=(Button) findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rewardedVideoAd.isLoaded())
                    rewardedVideoAd.show();
                for(int i=0;i<32564;i++)
                {}

                Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i,1);


            }

        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(rewardedVideoAd.isLoaded())
rewardedVideoAd.show();

                for(int i=0;i<32564;i++)
                {}
                if(bitmap==null)
                {
                    Toast.makeText(getApplicationContext(),"Bitmap is null",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmap);
                    FirebaseApp.initializeApp(MainActivity.this);


                    TextRecognizer textRecognizer = new TextRecognizer.Builder(MainActivity.this).build();

                    if(!textRecognizer.isOperational()) {
                        // Note: The first time that an app using a Vision API is installed on a
                        // device, GMS will download a native libraries to the device in order to do detection.
                        // Usually this completes before the app is run for the first time.  But if that
                        // download has not yet completed, then the above call will not detect any text,
                        // barcodes, or faces.
                        // isOperational() can be used to check if the required native libraries are currently
                        // available.  The detectors will automatically become operational once the library
                        // downloads complete on device.
                        Log.w("Dependencies: ", "Detector dependencies are not yet available.");

                        // Check for low storage.  If there is low storage, the native library will not be
                        // downloaded, so detection will not become operational.
                        IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                        boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                        if (hasLowStorage) {
                            Toast.makeText(getBaseContext(),"Low Storage", Toast.LENGTH_LONG).show();

                        }
                    }


                    Frame imageFrame = new Frame.Builder()
                            .setBitmap(bitmap)
                            .build();

                    SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
String result="";
                    for (int i = 0; i < textBlocks.size(); i++) {
                        TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));

                        Log.i("Value", textBlock.getValue());
                 result=result+textBlock.getValue();
                 // Do something with value
                    }
textView.setText(result);
                }
            }
        });
    }

    private void loadRewardedVideoAd() {
       if(!rewardedVideoAd.isLoaded()) {
           rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                   new AdRequest.Builder().build());
       }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    if(requestCode==1&&resultCode==RESULT_OK)
    {
        Uri uri=data.getData();
        try {
            bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        rewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        rewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}

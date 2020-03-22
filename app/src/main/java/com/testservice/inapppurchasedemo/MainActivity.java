package com.testservice.inapppurchasedemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.testservice.inapppurchasedemo.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        PurchasesUpdatedListener, BillingClientStateListener,
        SkuDetailsResponseListener {

    private static final String TAG = "InAppBilling";

    static final String ITEM_SKU_ADREMOVAL_1 = "streakr.ad_removal_10";
    static final String ITEM_SKU_ADREMOVAL_2 = "streakr.ad_removal_20";
    static final String ITEM_SKU_ADREMOVAL_3 = "streakr.ad_removal_30";
    static final String ITEM_SKU_ADREMOVAL_4 = "streakr.ad_removal_40";
    static final String ITEM_SKU_ADREMOVAL_5 = "streakr.ad_removal_50";

    static final String AD_REMOVED = "ad_removed";


    private BillingClient mBillingClient;

    List skuList = new ArrayList<>();

    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();

    private AdView mAdView;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    boolean adRemoved;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);



        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        adRemoved = pref.getBoolean(AD_REMOVED,false);
        if(adRemoved){
        }
        else {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        skuList.add(ITEM_SKU_ADREMOVAL_1);
        skuList.add(ITEM_SKU_ADREMOVAL_2);
        skuList.add(ITEM_SKU_ADREMOVAL_3);
        skuList.add(ITEM_SKU_ADREMOVAL_4);
        skuList.add(ITEM_SKU_ADREMOVAL_5);
        /*
        The unique product IDs you created when configuring your in-app products are used to asynchronously query Google
        Play for in-app product details.
        */

        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        /*
        billing Cilent
        Before you can make Google Play Billing requests, you must first establish a connection to Google Play
        by doing the following
        */

        mBillingClient = BillingClient.newBuilder(this).setListener(this).build();
        mBillingClient.startConnection(this);
    }


    @Override
    public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
        if (billingResponseCode == BillingClient.BillingResponse.OK) {
            showLog("The billing client is ready. You can query purchases here.");
            showToast("The billing client is ready");
            /*
            To query Google Play for in-app product details, call querySkuDetailsAsync().
            When calling this method, pass an instance of SkuDetailsParams that specifies a list of product ID
            strings and a SkuType. The SkuType can be either SkuType.INAPP for one-time products or
            rewarded products or SkuType.SUBS for subscriptions.
            */
            mBillingClient.querySkuDetailsAsync(params.build(), this);

        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        //TODO implement your own retry policy
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
        showLog(" The billing client is disconnected. Try restarting ");
        showToast("The billing client is disconnected.");
    }


    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<com.android.billingclient.api.Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            Log.d(TAG, " User Selected to puchased " + responseCode);
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.d(TAG, " User Canceled " + responseCode);
        } else if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {
            Log.d(TAG, " User Already Owened the product " + responseCode);
        } else {
            Log.d(TAG, " Other code " + responseCode);
            // Handle any other error codes.
        }

    }


    @Override
    public void onSkuDetailsResponse(int responseCode, List skuDetailsList) {
        // Process the result.
        if (responseCode == BillingClient.BillingResponse.OK
                && skuDetailsList != null) {
            for (Object skuDetailsObject : skuDetailsList) {
                SkuDetails skuDetails = (SkuDetails) skuDetailsObject;
                String sku = skuDetails.getSku();
                String price = skuDetails.getPrice();
                showLog(" Sku " + sku);

            }
        }
    }


    // custom methods //
    public void testBilling(View view) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(ITEM_SKU_ADREMOVAL_1)
                .setSku(ITEM_SKU_ADREMOVAL_2)
                .setSku(ITEM_SKU_ADREMOVAL_3)
                .setSku(ITEM_SKU_ADREMOVAL_4)
                .setSku(ITEM_SKU_ADREMOVAL_5)
                .setType(BillingClient.SkuType.INAPP)
                .build();
        int responseCode = mBillingClient.launchBillingFlow(MainActivity.this, flowParams);
    }


    private void handlePurchase(Purchase purchase) {
        if (purchase.getSku().equals(ITEM_SKU_ADREMOVAL_1)) {
            showToast(" You have Puchased Add removal "+ITEM_SKU_ADREMOVAL_1);
            showLog(" You have Puchased Add removal " + purchase.getSku());
            removeAdds();
        }
    }


    public void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void showLog(String msg) {
        Log.d(TAG, msg);
    }


    public void removeAdds(){
      editor.putBoolean(AD_REMOVED,true);
      editor.commit();
      adRemoved = true;
      mAdView.setVisibility(View.GONE);
    }

}
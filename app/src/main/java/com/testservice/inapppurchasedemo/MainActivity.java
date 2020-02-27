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
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.testservice.inapppurchasedemo.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener, BillingClientStateListener, SkuDetailsResponseListener {

    private static final String TAG = "InAppBilling";
    static final String ITEM_SKU_ADREMOVAL = "streakr.ad_removal";

    private Button mBuyButton;
    private String mAdRemovalPrice;
    private SharedPreferences mSharedPreferences;

    private BillingClient mBillingClient;

    List skuList = new ArrayList<>();
    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });



        skuList.add(ITEM_SKU_ADREMOVAL);
        mBillingClient = BillingClient.newBuilder(MainActivity.this).setListener(this).build();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        mBillingClient.startConnection(this);
        mBillingClient.querySkuDetailsAsync(params.build(), this);


    }


    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<com.android.billingclient.api.Purchase> purchases) {

        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.d(TAG, "User Canceled" + responseCode);
        } else if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {

        } else {
            Log.d(TAG, "Other code" + responseCode);
            // Handle any other error codes.
        }

    }



    public void showToast(String message){
        Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
        if (billingResponseCode == BillingClient.BillingResponse.OK) {
            // The billing client is ready. You can query purchases here.
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        //TODO implement your own retry policy
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
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
                if (ITEM_SKU_ADREMOVAL.equals(sku)) {
                    mAdRemovalPrice = price;
                }
            }
        }
    }

    public void testBilling(View view) {

            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSku(ITEM_SKU_ADREMOVAL)
                    .setType(BillingClient.SkuType.INAPP)
                    .build();
            int responseCode = mBillingClient.launchBillingFlow(MainActivity.this, flowParams);
    }


    private void handlePurchase(Purchase purchase) {
        if (purchase.getSku().equals(ITEM_SKU_ADREMOVAL)) {
            /*mSharedPreferences.edit().putBoolean(getResources().getString(R.string.pref_remove_ads_key), true).commit();
            setAdFree(true);
            mBuyButton.setText(getResources().getString(R.string.pref_ad_removal_purchased));
            mBuyButton.setEnabled(false);*/
        }
    }





}
package com.siva.vpn.activity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.net.Uri;
import android.net.VpnService;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ErrorReporter;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.siva.vpn.R;
import com.siva.vpn.core.OpenConnectManagementThread;
import com.siva.vpn.core.OpenVpnService;
import com.siva.vpn.core.ProfileManager;
import com.siva.vpn.core.VPNConnector;
import com.siva.vpn.handlers.PrefManager;
import com.siva.vpn.handlers.Utils;
import com.siva.vpn.models.NotificationModel;
import com.siva.vpn.models.ServerDetail;
import com.siva.vpn.sqlite.NotificationDbController;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.siva.vpn.sqlite.SharedPreferencesManager;

public class MainActivity extends Activity {
	public static final String TAG = "OpenConnect";
	private int mLastTab;
	private int mConnectionState = OpenConnectManagementThread.STATE_DISCONNECTED;
	private VPNConnector mConn;
	private PrefManager prefManager;
	private Dialog LoadingDialog,LocationDialog,LocationDialogSub,PTDiaglogbox,DisconnectDialog,InternetDialog,RateDialog, ProDialog;
	private Animation PlayButtonAnim;
	@SuppressLint("StaticFieldLeak")
	public static TextView PlayButton;
	private static ServerDetail serverDetails;
	private OpenVpnService OpenService;
	private boolean ConnectCommand = false;
	private boolean isSplash = false;
	private Boolean OnOpen = false;
	private Boolean ActiveAnimation;
	private int RateIndex = 0;
	private TextView UpInfo, TimeInfo, DownInfo,paid_tv,free_tv;
	@SuppressLint("StaticFieldLeak")
    public static ImageView Status;
	private ImageView Img_Flg;
    @SuppressLint("StaticFieldLeak")
	public static RelativeLayout board;
	private DrawerLayout dl;
	private ActionBarDrawerToggle t;
	private TextView LocationView,title_tv,des_tv;
	private RecyclerView RV,premium_rv;
	private ImageView maps_iv;
	private ImageView cursor;
	private int Call_Index;
	Button Dis_disconnect;
	Button Dis_Cancle;
	FrameLayout Dis_FrameLayout;
	private Boolean readyToPurchase =false;
	private static final String ONE_SUBSCRIPTION_ID = "one_month_subscription";
	private static final String SIX_SUBSCRIPTION_ID = "six_month_subscription";
	private static final String YEARLY_SUBSCRIPTION_ID = "yearly_month_subscription";
	private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAigRavdbud039t0iFyZrgZC2pjES2EVqPGHLf4lvRvp07YabrTJ4+7Z+7kw5Yorv38yaQsWxQ1bivyUek/DcYc6L2M+CV6M4tKXDavxsL+TPIz2DP2ycMVUoi8cxfz/EyZtxutZwJYxXA+RhBcqH9bWjL5j7z7sFXpt0QKJcpB71Rgp8U/wZsuf+uPoBaTdi0z+szxKIAzX/2ftugKo/GxJ42Wv2TgIB5cJ5ZzEVBawN6+3sgSTi4EBxFsHWteLbK2rfnMuexrzJD+V8CQLDuWtZoFWmIRFw+yrIkBiOCr+x4FhdMH2DENtdXFtLjpJNrOSBO0Ra6jgi+DL7Mtu0J2QIDAQAB"; // PUT YOUR MERCHANT KEY HERE;
	private static final String MERCHANT_ID="14849302069859471566";
	private com.google.android.gms.ads.InterstitialAd AdmobInterstitialAd;
	private com.google.android.gms.ads.reward.RewardedVideoAd AdmobRewardAd;
	private com.google.android.gms.ads.formats.UnifiedNativeAd nativeAd = null;
	private boolean InAdShown = false;
	private boolean RequestForDisconnect = false;
	int adLeaveCheck = 0;
	private BillingProcessor bp;

	public void ShowRewardAd(){

		if(DataManager.ADMOB_ENABLE) {
			if (AdmobRewardAd != null && AdmobRewardAd.isLoaded()) {
				AdmobRewardAd.show();
			} else {
				if (AdmobRewardAd != null && DataManager.ADMOB_ENABLE && !TextUtils.isEmpty(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_REWARD))) {

					AdmobRewardAd.loadAd(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_REWARD), new AdRequest.Builder().build());
				}
				if (AdmobInterstitialAd != null && AdmobInterstitialAd.isLoaded()) {
					AdmobInterstitialAd.show();
					InAdShown = true;

				} else {
					if (AdmobInterstitialAd != null && DataManager.ADMOB_ENABLE && !TextUtils.isEmpty(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_INTERSTITIAL))) {

						AdmobInterstitialAd.loadAd(new AdRequest.Builder().build());
					}
				}

				startVPN();
			}
		}else{
			startVPN();
		}
	}


	public void ShowInAd(){

			if (DataManager.ADMOB_ENABLE  && AdmobInterstitialAd != null && AdmobInterstitialAd.isLoaded()) {
				AdmobInterstitialAd.show();
				InAdShown = true;
			}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		setupServerData();
		FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		OnOpen = true;
		DisplayMetrics dm = new DisplayMetrics();
		SharedPreferencesManager.init(this);

		LoadingDialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		LoadingDialog.setContentView(R.layout.loading_window);
		LoadingDialog.setCancelable(false);
		LoadingDialog.show();
		isSplash=true;


		ProDialog = new Dialog(this);
		ProDialog.setContentView(R.layout.pro_window);
		ProDialog.setCancelable(false);
		ProDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		Button ProButton = ProDialog.findViewById(R.id.btn_show_pro);
		Button CloseButton = ProDialog.findViewById(R.id.btn_close);

		CloseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ProDialog.dismiss();
			}
		});

		ProButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				GoPro();
			}
		});

		PlayButton = (TextView)findViewById(R.id.play);
		LinearLayout subscription_ll = (LinearLayout) findViewById(R.id.subscription_ll);
        ImageView rate_us_tv = (ImageView) findViewById(R.id.rate_us_tv);
        ImageView share_us_tv = (ImageView) findViewById(R.id.share_us_tv);
		Status = (ImageView)findViewById(R.id.img_stts);
		maps_iv = (ImageView)findViewById(R.id.maps_iv);
		DownInfo = (TextView)findViewById(R.id.textView7);
		TimeInfo = (TextView)findViewById(R.id.time_info_tv);
		UpInfo = (TextView)findViewById(R.id.textView22);
		LocationView = (TextView) findViewById(R.id.location_view);
		Img_Flg = (ImageView) findViewById(R.id.img_flg);
		board = (RelativeLayout) findViewById(R.id.imageView7);
        initSubscription();
		InitiateSubscription();
		initiateprivacytermscondition();
		InternetConnectionDialog();

		InitiaterateDialog();
		rate_us_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(cursor!=null){
					cursor.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_in_out));
				}
				RateDialog.show();
			}
		});


		ActiveAnimation = false;

		share_us_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(dl!=null)
				dl.openDrawer(Gravity.LEFT);
			}
		});

		board.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(Utils.isNetworkAvailable(MainActivity.this)) {
					showlocationswindow();
				}else{
					InternetDialog.show();
				}
			}
		});
		Status.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

					if (Utils.isNetworkAvailable(MainActivity.this)) {
						if (OpenService.getConnectionState() == OpenConnectManagementThread.STATE_CONNECTED) {
							if (DataManager.ADMOB_ENABLE)
								ShowInAd();
							dissconnect();
						} else {
							ConnectCommand = true;
							if (ActiveAnimation) {
								PlayButtonAnimation(1);
							}
							if (DataManager.ADMOB_ENABLE) {
								if (serverDetails != null && serverDetails.getReward_server().equals("true")) {
									ShowRewardAd();
								} else {
									ShowInAd();
									startVPN();
								}
							} else {
								startVPN();
							}
						}
					} else {
						InternetDialog.show();
					}
				}

		});
		subscription_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LocationDialogSub.show();
			}
		});


		prefManager = new PrefManager(getBaseContext(),PrefManager.PRF_APP_DATA,PrefManager.MODE_READ);
		int opencCount = prefManager.ReadInt(PrefManager.KEY_OPEN_COUNT);
		RateIndex  = prefManager.ReadInt(PrefManager.KEY_RATE_INDEX);
		if(opencCount != 0){
		}
		opencCount++;
		prefManager = new PrefManager(getBaseContext(),PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
		prefManager.SaveIntData(PrefManager.KEY_OPEN_COUNT, opencCount);
		boolean allowInAd = true;
		setAdmobID();
		setupNavDrawable();
	}

	private void setupNavDrawable(){
		dl = (DrawerLayout)findViewById(R.id.activity_main);
		t = new ActionBarDrawerToggle(this, dl,R.string.ok, R.string.cancel);

		dl.addDrawerListener(t);
		t.syncState();


		NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
		nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				int id = item.getItemId();
				switch(id)
				{
					case R.id.nav_contact_us:
						contactUs();
						break;
					case R.id.nav_terms:
						Spanned sp = Html.fromHtml( getString(R.string.termscondition));
						title_tv.setText(R.string.TermsConditions);
						des_tv.setText(sp);
						PTDiaglogbox.show();
						break;
					case R.id.nav_privacy:
						Spanned sps = Html.fromHtml( getString(R.string.privacy));
						title_tv.setText(R.string.PrivacyPolicy);
						des_tv.setText(sps);
						PTDiaglogbox.show();
						break;
					case R.id.nav_share:
						Intent sendIntent = new Intent();
						sendIntent.setAction(Intent.ACTION_SEND);
						sendIntent.putExtra(Intent.EXTRA_TEXT,
								"Download "+getResources().getString(R.string.app)+" : "+"https://play.google.com/store/apps/details?id=" + getPackageName());
						sendIntent.setType("text/plain");
						startActivity(sendIntent);
						break;
					default:
						return true;
				}

				dl.closeDrawer(Gravity.LEFT);
				return true;

			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(t.onOptionsItemSelected(item))
			return true;

		return super.onOptionsItemSelected(item);
	}

	private void contactUs(){
		try {
			Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + SharedPreferencesManager.getString(DataManager.EMAIL,"")));
			intent.putExtra(Intent.EXTRA_SUBJECT, "Help Center");
			intent.putExtra(Intent.EXTRA_TEXT, "MESSAGE");
			startActivity(intent);
		} catch(Exception e) {
			Toast.makeText(MainActivity.this, "Sorry...You don't have any mail app", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	private void initSubscription(){

		if(!BillingProcessor.isIabServiceAvailable(this)) {
			Toast.makeText(MainActivity.this,"In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16",Toast.LENGTH_SHORT).show();
		}

		bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
			@Override
			public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
				checkIfUserIsSusbcribed();
			}
			@Override
			public void onBillingError(int errorCode, @Nullable Throwable error) {
			}
			@Override
			public void onBillingInitialized() {
				readyToPurchase = true;
				checkIfUserIsSusbcribed();
			}
			@Override
			public void onPurchaseHistoryRestored() {
				checkIfUserIsSusbcribed();
			}
		});

    }



	public void initads(){
	AdmobInterstitialAd = new com.google.android.gms.ads.InterstitialAd(getBaseContext());
				if(DataManager.ADMOB_ENABLE && !TextUtils.isEmpty(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_INTERSTITIAL))) {
					AdmobInterstitialAd.setAdUnitId(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_INTERSTITIAL));
					AdmobInterstitialAd.loadAd(new AdRequest.Builder().build());
					AdmobInterstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
						@Override
						public void onAdLoaded() {
                            if(isSplash){

                                AdmobInterstitialAd.show();

                            }
						}

						@Override
						public void onAdFailedToLoad(int errorCode) {
						}

						@Override
						public void onAdOpened() {
						}

						@Override
						public void onAdClicked() {
						}

						@Override
						public void onAdLeftApplication() {
						}

						@Override
						public void onAdClosed() {

							AdmobInterstitialAd.loadAd(new AdRequest.Builder().build());
							if (RequestForDisconnect) {
								RequestForDisconnect = false;
							}
							if(isSplash){
								isSplash=false;
								if(AdmobInterstitialAd!=null && LoadingDialog.isShowing()){
									LoadingDialog.dismiss();
								}
							}
						}
					});
				}else{
					if(isSplash){
						isSplash=false;
						if(LoadingDialog.isShowing()){
							LoadingDialog.dismiss();
						}
					}
				}

					AdmobRewardAd = MobileAds.getRewardedVideoAdInstance(getBaseContext());
				if(DataManager.ADMOB_ENABLE && !TextUtils.isEmpty(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_REWARD))) {
					AdmobRewardAd.setRewardedVideoAdListener(new com.google.android.gms.ads.reward.RewardedVideoAdListener() {
						@Override
						public void onRewardedVideoAdLoaded() {

						}

						@Override
						public void onRewardedVideoAdOpened() {

						}

						@Override
						public void onRewardedVideoStarted() {
							adLeaveCheck = 1;
						}

						@Override
						public void onRewardedVideoAdClosed() {
							if (adLeaveCheck != 2) {
								Toast.makeText(MainActivity.this, "To use Paid Server You need to watch the rewarded ads completely.", Toast.LENGTH_SHORT).show();
							}
							AdmobRewardAd.loadAd(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_REWARD), new AdRequest.Builder().build());
						}

						@Override
						public void onRewarded(RewardItem rewardItem) {
							adLeaveCheck = 2;
							startVPN();
						}

						@Override
						public void onRewardedVideoAdLeftApplication() {

						}

						@Override
						public void onRewardedVideoAdFailedToLoad(int i) {

						}

						@Override
						public void onRewardedVideoCompleted() {
						}
					});
					AdmobRewardAd.loadAd(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_REWARD), new AdRequest.Builder().build());
				}


	}

	public void InitiateLocationWindow(){
		LocationDialog = new Dialog(this,R.style.AppTheme);
		LocationDialog.setContentView(R.layout.location_window);


		RV = (RecyclerView) LocationDialog.findViewById(R.id.rv) ;
		premium_rv = (RecyclerView) LocationDialog.findViewById(R.id.premium_rv) ;
        ImageView ic_back_tv = (ImageView) LocationDialog.findViewById(R.id.ic_back_tv);
		free_tv = (TextView) LocationDialog.findViewById(R.id.free_tv) ;
		paid_tv = (TextView) LocationDialog.findViewById(R.id.premium_tv) ;

		free_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RV.setVisibility(View.VISIBLE);
				premium_rv.setVisibility(View.GONE);
				paid_tv.setBackgroundResource(R.drawable.round_background);
				free_tv.setBackgroundResource(R.drawable.background_sub);
			}
		});
		paid_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RV.setVisibility(View.GONE);
				premium_rv.setVisibility(View.VISIBLE);
				paid_tv.setBackgroundResource(R.drawable.background_sub);
				free_tv.setBackgroundResource(R.drawable.round_background);
			}
		});
		ic_back_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LocationDialog.dismiss();
			}
		});
		RV.setLayoutManager(new LinearLayoutManager(this));
		premium_rv.setLayoutManager(new LinearLayoutManager(this));
		RV.setAdapter(new LocationRecyclerAdapter(MainActivity.this,DataManager.freeServerDetails, new LocationRecyclerAdapter.OnItemListener() {
			@Override
			public void OnItemClick(int index) {

				if(DataManager.freeServerDetails.get(index).getIsPaid().equals("true")) {
					if(!bp.isSubscribed(SharedPreferencesManager.getString("subscription",""))) {
						LocationDialogSub.show();
						return;
					}

				}
				serverDetails=DataManager.freeServerDetails.get(index);
				prefManager = new PrefManager(getBaseContext(),PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
				SharedPreferencesManager.setString("Selected_server",DataManager.freeServerDetails.get(index).getServername());
				LocationView.setText(DataManager.freeServerDetails.get(index).getServername());

				Glide.with(MainActivity.this).load(DataManager.freeServerDetails.get(index).getServerflag())
						.placeholder(R.drawable.f_0)
						.into(Img_Flg);
				ConnectCommand = true;
				if(ActiveAnimation){
					PlayButtonAnimation(1);
				}

				if(DataManager.ADMOB_ENABLE) {
					if (DataManager.freeServerDetails.get(index).getReward_server().equals("true")) {
						ShowRewardAd();
					} else {
						ShowInAd();
						startVPN();

					}
				}else{
					startVPN();
				}

				LocationDialog.dismiss();


			}
		}));

		premium_rv.setAdapter(new LocationRecyclerAdapter(MainActivity.this,DataManager.serverDetails, new LocationRecyclerAdapter.OnItemListener() {
			@Override
			public void OnItemClick(int index) {

				if(DataManager.serverDetails.get(index).getIsPaid().equals("true")) {
					if(!bp.isSubscribed(SharedPreferencesManager.getString("subscription",""))) {
						LocationDialogSub.show();
						return;
					}

				}
				serverDetails=DataManager.serverDetails.get(index);
				prefManager = new PrefManager(getBaseContext(),PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
				SharedPreferencesManager.setString("Selected_server",DataManager.serverDetails.get(index).getServername());
				LocationView.setText(DataManager.serverDetails.get(index).getServername());

				Glide.with(MainActivity.this).load(DataManager.serverDetails.get(index).getServerflag())
						.placeholder(R.drawable.f_0)
						.into(Img_Flg);
				ConnectCommand = true;
				if(ActiveAnimation){
					PlayButtonAnimation(1);
				}

				if(DataManager.ADMOB_ENABLE) {
					if (DataManager.serverDetails.get(index).getReward_server().equals("true")) {
						ShowRewardAd();
					} else {
						ShowInAd();
						startVPN();

					}
				}else{
					startVPN();
				}

				LocationDialog.dismiss();


			}
		}));


	}

	public void InitiateSubscription(){
		LocationDialogSub = new Dialog(this,R.style.AppTheme);
		LocationDialogSub.setContentView(R.layout.layout_subscription);


		ImageView ic_back_iv = (ImageView) LocationDialogSub.findViewById(R.id.iv_close_sub);
		LinearLayout one_mounth_ll = (LinearLayout) LocationDialogSub.findViewById(R.id.three_mounth_ll);
		LinearLayout six_month_ll = (LinearLayout) LocationDialogSub.findViewById(R.id.six_months_ll);
		LinearLayout yearly_ll = (LinearLayout) LocationDialogSub.findViewById(R.id.one_year_ll);
		prefManager = new PrefManager(getBaseContext(),PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
		ic_back_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LocationDialogSub.dismiss();
			}
		});
		one_mounth_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(bp.isSubscribed(SharedPreferencesManager.getString("subscription",""))){
 					bp.updateSubscription(MainActivity.this,SharedPreferencesManager.getString("subscription",""),ONE_SUBSCRIPTION_ID);
				}else{
					bp.subscribe(MainActivity.this,ONE_SUBSCRIPTION_ID);
				}
            }
        });
		six_month_ll.setOnClickListener(
				new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				if(bp.isSubscribed(SharedPreferencesManager.getString("subscription",""))){
					bp.updateSubscription(MainActivity.this,SharedPreferencesManager.getString("subscription",""),SIX_SUBSCRIPTION_ID);
				}else{
					bp.subscribe(MainActivity.this,SIX_SUBSCRIPTION_ID);
				}
            }
        });
		yearly_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				if(bp.isSubscribed(SharedPreferencesManager.getString("subscription",""))){
					bp.updateSubscription(MainActivity.this,SharedPreferencesManager.getString("subscription",""),YEARLY_SUBSCRIPTION_ID);
				}else{
					bp.subscribe(MainActivity.this,YEARLY_SUBSCRIPTION_ID);
				}
            }
        });

	}

    public void initiateprivacytermscondition(){
        PTDiaglogbox = new Dialog(this,R.style.AppTheme);
        PTDiaglogbox.setContentView(R.layout.layout_termscondition);

        title_tv=(TextView)PTDiaglogbox.findViewById(R.id.title_tv);
        des_tv=(TextView)PTDiaglogbox.findViewById(R.id.des_tv);
		ImageView ic_back_iv = (ImageView) PTDiaglogbox.findViewById(R.id.iv_close_sub) ;
		ic_back_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PTDiaglogbox.dismiss();
			}
		});
    }

	public void showlocationswindow(){

		LocationDialog.show();


	}

	public void GoPro(){
		final String appPackageName = getPackageName();
		try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
		}

	}

	public void Rate(){
		final String appPackageName = getPackageName();
		try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
		}
		return;
	}

	public void InitiaterateDialog(){
		RateDialog = new Dialog(this);
		RateDialog.setContentView(R.layout.rating_window);
		Button rateButton = (Button)RateDialog.findViewById(R.id.btn_rt);
		cursor = (ImageView) RateDialog.findViewById(R.id.ic_rate_us);
		TextView laterButton = (TextView) RateDialog.findViewById(R.id.btn_later);
		rateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				prefManager = new PrefManager(getBaseContext(),PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
				prefManager.SaveIntData(PrefManager.KEY_RATE_INDEX,420);
				RateIndex = 420;
				RateDialog.dismiss();
				if(cursor!=null)
				cursor.clearAnimation();
				Rate();
			}
		});



		laterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				RateDialog.dismiss();

			}
		});

		RateDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if(cursor!=null)
				cursor.clearAnimation();
			}
		});
		RateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}






	public void InitiateDiscountDialog(){

		DisconnectDialog = new Dialog(this);
		DisconnectDialog.setContentView(R.layout.disconnect_window);
		Dis_disconnect = (Button) DisconnectDialog.findViewById(R.id.btn_dscnt);
		Dis_Cancle = (Button) DisconnectDialog.findViewById(R.id.btn_cncl);
		Dis_FrameLayout = DisconnectDialog.findViewById(R.id.dscnt_ad);

		if (DataManager.ADMOB_ENABLE&& !TextUtils.isEmpty(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_BANNER))) {
			refreshAd();
		}

		Dis_disconnect.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mConn.service.stopVPN();
					Random rand = new Random();
					int n = rand.nextInt(4);

					DisconnectDialog.dismiss();
				}
			});
		Dis_Cancle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					DisconnectDialog.dismiss();
				}
			});

		DisconnectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		DisconnectDialog.setCancelable(false);
	}


	public void InternetConnectionDialog(){

		InternetDialog = new Dialog(this);
		InternetDialog.setContentView(R.layout.layout_internet);
		Button cancel_btn = (Button) InternetDialog.findViewById(R.id.cancel_btn);

		cancel_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				InternetDialog.dismiss();
			}
		});


		InternetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		InternetDialog.setCancelable(false);
	}


	public void dissconnect(){
	    DisconnectDialog.show();
	}

	public void updateui(OpenVpnService service){
		int state = service.getConnectionState();
		service.startActiveDialog(this);

		if (mConnectionState != state) {
			if (state == OpenConnectManagementThread.STATE_DISCONNECTED) {
				PlayButton.setTextColor(Color.RED);
				Status.setImageResource(R.drawable.disconnected);
				PlayButton.setText(R.string.connect);

				Status.setEnabled(true);
				if(!ActiveAnimation)
					Status.setAlpha(1f);
				LocationView.setEnabled(true);

				maps_iv.setImageResource(R.drawable.maps_off);

			} else if (state == OpenConnectManagementThread.STATE_CONNECTED) {

				if(ConnectCommand){
					Status.setEnabled(true);
					ConnectCommand = false;
					LocationView.setEnabled(true);

					if(ActiveAnimation)
						PlayButtonAnimation(0);
				}
				Status.setAlpha(1f);
				PlayButton.setTextColor(Color.GREEN);
				PlayButton.setText(R.string.disconnect);
				maps_iv.setImageResource(R.drawable.maps);
				Status.setImageResource(R.drawable.connected);
			} else if(state == OpenConnectManagementThread.STATE_AUTHENTICATING || state ==  OpenConnectManagementThread.STATE_CONNECTING ){
				Status.setEnabled(false);
				Status.setAlpha(0.5f);
				LocationView.setEnabled(false);
				PlayButton.setTextColor(Color.GREEN);
				PlayButton.setText(R.string.Connecting);
			}
			mConnectionState = state;
		}

		if (state == OpenConnectManagementThread.STATE_CONNECTED) {

			DownInfo.setText(

					OpenVpnService.humanReadableByteCount(mConn.deltaStats.rxBytes, true)
			);
			TimeInfo.setText(OpenVpnService.formatElapsedTime(service.startTime.getTime()));
			TimeInfo.setTextColor(getResources().getColor(R.color.color_yellow));

			UpInfo.setText(OpenVpnService.humanReadableByteCount(mConn.deltaStats.txBytes, true));
		}else if (mConnectionState == OpenConnectManagementThread.STATE_DISCONNECTED){
			TimeInfo.setText("00:00");
			TimeInfo.setTextColor(getResources().getColor(R.color.color_gray));
		}

	}



	public void PlayButtonAnimation(int index){
		if(index == 0){
			PlayButtonAnim = new ScaleAnimation(1f,1.02f,1f,1.02f,Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 1f);
			PlayButtonAnim.setDuration(350);
			PlayButtonAnim.setRepeatCount(Animation.INFINITE);
			PlayButtonAnim.setRepeatMode(Animation.REVERSE);
		}else if(index == 1){
			PlayButtonAnim = new RotateAnimation(0, 360,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
					0.5f);
			PlayButtonAnim.setDuration(500);
			PlayButtonAnim.setRepeatCount(Animation.INFINITE);
		}

		Status.startAnimation(PlayButtonAnim);

	}

	private void reportBadRom(Exception e) {
		ACRAConfiguration cfg = ACRA.getConfig();
		cfg.setResDialogText(R.string.bad_rom_text);
		cfg.setResDialogCommentPrompt(R.string.bad_rom_comment_prompt);
		ACRA.setConfig(cfg);

		ErrorReporter er = ACRA.getErrorReporter();
		er.putCustomData("cause", "reportBadRom");
		er.handleException(e);
	}


	public void  s( String s){

		Toast.makeText( getApplicationContext() , s,
				Toast.LENGTH_SHORT).show();

	}

	private void startVPN() {
		Intent prepIntent;
		try {
			prepIntent = VpnService.prepare(this);
		} catch (Exception e) {
			reportBadRom(e);
			return;
		}

		if (prepIntent != null) {
			try {
				startActivityForResult(prepIntent, 0);
			} catch (Exception e) {
				reportBadRom(e);

			}
		} else {
			onActivityResult(0, RESULT_OK, null);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!bp.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);

		}
		setResult(resultCode);

		if (requestCode==0 && resultCode == RESULT_OK) {
		if(LocationView.getText().equals("Optimal Server")){
			serverDetails=DataManager.freeServerDetails.get(0);
		}

			ProfileManager.mProfiles.clear();
			String s = null;
			if(serverDetails!=null && serverDetails.getServerip()!=null)
				s = ProfileManager.create(serverDetails.getServerip()).getUUID().toString();
			Intent intent = new Intent(getBaseContext(), OpenVpnService.class);
			intent.putExtra(OpenVpnService.EXTRA_UUID, s);
			startService(intent);

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
		b.putInt("active_tab", mLastTab);
	}

	@Override
	protected void onRestoreInstanceState(@androidx.annotation.NonNull Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}


	@Override
	protected void onResume() {
		super.onResume();
		mConn = new VPNConnector(this, false) {
			@Override
			public void onUpdate(OpenVpnService service) {
				OpenService = service;
				updateui(service);
				if(OnOpen){

					if(serverDetails!=null) {
						SharedPreferencesManager.setString("Selected_server",serverDetails.getServername());
						LocationView.setText(serverDetails.getServername());
						Glide.with(MainActivity.this).load(serverDetails.getServerflag())
								.placeholder(R.drawable.f_0)
								.into(Img_Flg);
					}
					OnOpen = false;
				}
			}
		};


		IntentFilter intentFilter = new IntentFilter(Utils.NEW_NOTI);
		LocalBroadcastManager.getInstance(this).registerReceiver(newNotificationReceiver, intentFilter);

		initNotification();
	}

	private BroadcastReceiver newNotificationReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			initNotification();
		}
	};

	public void initNotification() {
		NotificationDbController notificationDbController = new NotificationDbController(this);
		ArrayList<NotificationModel> notiArrayList = notificationDbController.getUnreadData();

		if (notiArrayList != null && !notiArrayList.isEmpty()) {
			int totalUnread = notiArrayList.size();

		}

	}
	@Override
	protected void onDestroy() {

		if (nativeAd != null) {
			nativeAd.destroy();
		}
        if (bp != null)
			bp.release();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mConn.stopActiveDialog();
		mConn.unbind();
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(newNotificationReceiver);
	}

	private void setAdmobID(){

		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Admob_id");
		databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if(dataSnapshot.exists()) {
					Log.d("admobsJSON", "onDataChange: " + dataSnapshot);
					for(DataSnapshot snapshot : dataSnapshot.getChildren()){
						SharedPreferencesManager.setAdmobJson(snapshot.getKey(),snapshot.getValue().toString());
						Log.d("admobsJSON", "key: " + snapshot.getKey()+snapshot.getValue());

					}
					setAdmobsEnable();

				}

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				SharedPreferencesManager.setAdmobJson(DataManager.ADMOB_APPID,"ca-app-pub-7182842732261202~8095989001");
				SharedPreferencesManager.setAdmobJson(DataManager.ADMOB_PUBLISHER,"pub-7182842732261202");
				SharedPreferencesManager.setAdmobJson(DataManager.ADMOB_BANNER,"ca-app-pub-7182842732261202/6296640415");
				SharedPreferencesManager.setAdmobJson(DataManager.ADMOB_NATIVE,"ca-app-pub-7182842732261202/4212584313");
				SharedPreferencesManager.setAdmobJson(DataManager.ADMOB_REWARD,"ca-app-pub-7182842732261202/8757978767");
				SharedPreferencesManager.setAdmobJson(DataManager.EMAIL,"jeevageektech@gmail.com");
				SharedPreferencesManager.setAdmobJson(DataManager.ADMOB_INTERSTITIAL,"ca-app-pub-2942345680010540/7244365701");
				SharedPreferencesManager.setAdmobJson(DataManager.ADMOB_SUBSCRIPTION,"false");
				DataManager.ADMOB_ENABLE=false;
				setAdmobsEnable();
			}
		});
	}
	private void setAdmobsEnable(){

		if(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_ENABLES).equals("true")){
			checkIfUserIsSusbcribeds();

		}else{
			DataManager.ADMOB_ENABLE=false;
			initialiseADS();
		}




	}

	private void initialiseADS(){
		if (DataManager.ADMOB_ENABLE) {
			if(!TextUtils.isEmpty(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_APPID)))
				MobileAds.initialize(this, SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_APPID));
		}
		RelativeLayout admob_rl = findViewById(R.id.admob_rl);

		if (DataManager.ADMOB_ENABLE&& !TextUtils.isEmpty(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_BANNER))) {
			AdView mAdView = new AdView(MainActivity.this);

			mAdView.setAdSize(AdSize.SMART_BANNER);
			if(!TextUtils.isEmpty(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_BANNER)))
				mAdView.setAdUnitId(SharedPreferencesManager.getAdmobJson(DataManager.ADMOB_BANNER));
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
			admob_rl.addView(mAdView);


		}else{
			admob_rl.setVisibility(View.GONE);
		}
		InitiateDiscountDialog();
		initads();
	}
	void checkIfUserIsSusbcribed(){
		Boolean purchaseResult = bp.loadOwnedPurchasesFromGoogle();
		RelativeLayout admob_rl = findViewById(R.id.admob_rl);
		if(bp.listOwnedSubscriptions().size()>0) {
			SharedPreferencesManager.setString("subscription", bp.listOwnedSubscriptions().get(0));
		}else{
			SharedPreferencesManager.setString("subscription","");
		}
		if(purchaseResult) {
			TransactionDetails subscriptionTransactionDetails = bp.getSubscriptionTransactionDetails(SharedPreferencesManager.getString("subscription", ""));
			if (subscriptionTransactionDetails != null) {

				DataManager.ADMOB_ENABLE=false;
				admob_rl.setVisibility(View.GONE);
				getExpireDate(subscriptionTransactionDetails.purchaseInfo.purchaseData.purchaseTime.toString());
			}else{
				DataManager.ADMOB_ENABLE=true;
				admob_rl.setVisibility(View.VISIBLE);
				getExpireDate("");
			}
		}else{
			getExpireDate("");
		}



	}
	void checkIfUserIsSusbcribeds(){
		Boolean purchaseResult = bp.loadOwnedPurchasesFromGoogle();
		RelativeLayout admob_rl = findViewById(R.id.admob_rl);
		if(bp.listOwnedSubscriptions().size()>0) {
			SharedPreferencesManager.setString("subscription", bp.listOwnedSubscriptions().get(0));
		}else{
			SharedPreferencesManager.setString("subscription","");
		}
		if(purchaseResult) {
			TransactionDetails subscriptionTransactionDetails = bp.getSubscriptionTransactionDetails(SharedPreferencesManager.getString("subscription", ""));
			if (subscriptionTransactionDetails != null) {

				DataManager.ADMOB_ENABLE=false;
				admob_rl.setVisibility(View.GONE);
			}else{
				DataManager.ADMOB_ENABLE=true;
				admob_rl.setVisibility(View.VISIBLE);
				initialiseADS();
			}
		}else{
			initialiseADS();
		}



	}
	private void getExpireDate(String inputText){
		TextView sub_tv = (TextView) LocationDialogSub.findViewById(R.id.Sub_tv);
		TextView text = (TextView)findViewById(R.id.subcription_tv);
		if(!TextUtils.isEmpty(inputText) && bp.isSubscribed(SharedPreferencesManager.getString("subscription",""))) {
			SimpleDateFormat inputFormat = new SimpleDateFormat
					("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
			inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

			SimpleDateFormat outputFormat =
					new SimpleDateFormat("MMM dd, yyyy");
			Date date = null;
			try {
				date = inputFormat.parse(inputText);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				switch (SharedPreferencesManager.getString("subscription","")){
					case ONE_SUBSCRIPTION_ID:
						cal.add(Calendar.MONTH, 1);
						break;
					case SIX_SUBSCRIPTION_ID:
						cal.add(Calendar.MONTH, 6);
						break;
					case YEARLY_SUBSCRIPTION_ID:
						cal.add(Calendar.MONTH, 12);
						break;
				}

				String outputText = outputFormat.format(cal.getTime());
				text.setText(String.format("%s","Expired at: "+outputText));
				sub_tv.setText(R.string.Expired_At+"\n"+outputText);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			text.setText(String.format("%s","GO PREMIUM"));
			sub_tv.setText(R.string.VIP_specific_features);

		}

	}

    private void setupServerData(){

        DataManager.serverDetails.clear();
        DataManager.freeServerDetails.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Server");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        ServerDetail serverDetail=snapshot.getValue(ServerDetail.class);
                        if(serverDetail.getIsPaid().equals("true")){
                            DataManager.serverDetails.add(serverDetail);
                        }else {
							DataManager.freeServerDetails.add(serverDetail);
						}
                    }
					 InitiateLocationWindow();
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });

    }

	private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
		MediaView mediaView = adView.findViewById(R.id.ad_media);
		adView.setMediaView(mediaView);
		adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
		adView.setBodyView(adView.findViewById(R.id.ad_body));
		adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
		adView.setIconView(adView.findViewById(R.id.ad_app_icon));
		adView.setPriceView(adView.findViewById(R.id.ad_price));
		adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
		adView.setStoreView(adView.findViewById(R.id.ad_store));
		adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

		((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
		if (nativeAd.getBody() == null) {
			adView.getBodyView().setVisibility(View.INVISIBLE);
		} else {
			adView.getBodyView().setVisibility(View.VISIBLE);
			((TextView) adView.getBodyView()).setText(nativeAd.getBody());
		}

		if (nativeAd.getCallToAction() == null) {
			adView.getCallToActionView().setVisibility(View.INVISIBLE);
		} else {
			adView.getCallToActionView().setVisibility(View.VISIBLE);
			((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
		}

		if (nativeAd.getIcon() == null) {
			adView.getIconView().setVisibility(View.GONE);
		} else {
			((ImageView) adView.getIconView()).setImageDrawable(
					nativeAd.getIcon().getDrawable());
			adView.getIconView().setVisibility(View.VISIBLE);
		}

		if (nativeAd.getPrice() == null) {
			adView.getPriceView().setVisibility(View.INVISIBLE);
		} else {
			adView.getPriceView().setVisibility(View.VISIBLE);
			((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
		}

		if (nativeAd.getStore() == null) {
			adView.getStoreView().setVisibility(View.INVISIBLE);
		} else {
			adView.getStoreView().setVisibility(View.VISIBLE);
			((TextView) adView.getStoreView()).setText(nativeAd.getStore());
		}

		if (nativeAd.getStarRating() == null) {
			adView.getStarRatingView().setVisibility(View.INVISIBLE);
		} else {
			((RatingBar) adView.getStarRatingView())
					.setRating(nativeAd.getStarRating().floatValue());
			adView.getStarRatingView().setVisibility(View.VISIBLE);
		}

		if (nativeAd.getAdvertiser() == null) {
			adView.getAdvertiserView().setVisibility(View.INVISIBLE);
		} else {
			((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
			adView.getAdvertiserView().setVisibility(View.VISIBLE);
		}

		adView.setNativeAd(nativeAd);
		VideoController vc = nativeAd.getVideoController();
		if (vc.hasVideoContent()) {
			vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
				@Override
				public void onVideoEnd() {
					super.onVideoEnd();
				}
			});
		}
	}

	private void refreshAd() {
		AdLoader.Builder builder = new AdLoader.Builder(this, SharedPreferencesManager.getString(DataManager.ADMOB_NATIVE,""));

		builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
			// OnUnifiedNativeAdLoadedListener implementation.
			@Override
			public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
				if (nativeAd != null) {
					nativeAd.destroy();
				}
				nativeAd = unifiedNativeAd;
				FrameLayout frameLayout = DisconnectDialog.findViewById(R.id.dscnt_ad);
				UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
						.inflate(R.layout.ad_unified, null);
				populateUnifiedNativeAdView(unifiedNativeAd, adView);
				frameLayout.removeAllViews();
				frameLayout.addView(adView);
			}

		});

		VideoOptions videoOptions = new VideoOptions.Builder()
				.setStartMuted(false)
				.build();

		NativeAdOptions adOptions = new NativeAdOptions.Builder()
				.setVideoOptions(videoOptions)
				.build();

		builder.withNativeAdOptions(adOptions);

		AdLoader adLoader = builder.withAdListener(new AdListener() {
			@Override
			public void onAdFailedToLoad(int errorCode) {

			}
		}).build();

		adLoader.loadAd(new AdRequest.Builder().build());


	}
}

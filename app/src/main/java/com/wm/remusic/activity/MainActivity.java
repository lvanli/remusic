package com.wm.remusic.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.wm.remusic.R;
import com.wm.remusic.adapter.MenuItemAdapter;
import com.wm.remusic.dialog.AddNetPlaylistDialog;
import com.wm.remusic.dialog.CardPickerDialog;
import com.wm.remusic.fragment.BitSetFragment;
import com.wm.remusic.fragment.MainFragment;
import com.wm.remusic.fragment.TimingFragment;
import com.wm.remusic.fragmentnet.TabNetPagerFragment;
import com.wm.remusic.handler.HandlerUtil;
import com.wm.remusic.info.MusicInfo;
import com.wm.remusic.service.MusicPlayer;
import com.wm.remusic.uitl.PreferencesUtility;
import com.wm.remusic.uitl.ThemeHelper;
import com.wm.remusic.widget.CustomViewPager;
import com.wm.remusic.widget.LvMenuItem;
import com.wm.remusic.widget.SplashScreen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements CardPickerDialog.ClickListener {
    private ActionBar ab;
    private ImageView barnet, barmusic, barfriends, search;
    private ArrayList<ImageView> tabs = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ListView mLvLeftMenu;
    private long time = 0;
    private SplashScreen splashScreen;

    public void onCreate(Bundle savedInstanceState) {
        splashScreen = new SplashScreen(this);
        splashScreen.show(R.drawable.art_login_bg,
                SplashScreen.SLIDE_LEFT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HandlerUtil.getInstance(this).postDelayed(new Runnable() {
            @Override
            public void run() {
                splashScreen.removeSplashScreen();
            }
        }, 3000);

        getWindow().setBackgroundDrawableResource(R.color.background_material_light_1);

        barnet = (ImageView) findViewById(R.id.bar_net);
        barmusic = (ImageView) findViewById(R.id.bar_music);
        barfriends = (ImageView) findViewById(R.id.bar_friends);
        search = (ImageView) findViewById(R.id.bar_search);
        barmusic = (ImageView) findViewById(R.id.bar_music);

        drawerLayout = (DrawerLayout) findViewById(R.id.fd);
        mLvLeftMenu = (ListView) findViewById(R.id.id_lv_left_menu);

        setToolBar();
        setViewPager();
        setUpDrawer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicPlayer.setMediaButtonMode(mMediaButtonMode);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setTitle("");
    }

    private void setViewPager() {
        tabs.add(barnet);
        tabs.add(barmusic);
        final CustomViewPager customViewPager = (CustomViewPager) findViewById(R.id.main_viewpager);
        final MainFragment mainFragment = new MainFragment();
        final TabNetPagerFragment tabNetPagerFragment = new TabNetPagerFragment();
        CustomViewPagerAdapter customViewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager());
        customViewPagerAdapter.addFragment(tabNetPagerFragment);
        customViewPagerAdapter.addFragment(mainFragment);
        customViewPager.setAdapter(customViewPagerAdapter);
        customViewPager.setCurrentItem(1);
        barmusic.setSelected(true);
        customViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        barnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customViewPager.setCurrentItem(0);
            }
        });
        barmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customViewPager.setCurrentItem(1);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, NetSearchWordsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                MainActivity.this.startActivity(intent);
            }
        });
    }
    private int mMediaButtonMode = 0;

    private void setUpDrawer() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mLvLeftMenu.addHeaderView(inflater.inflate(R.layout.nav_header_main, mLvLeftMenu, false));
        mLvLeftMenu.setAdapter(new MenuItemAdapter(this));
        mLvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        drawerLayout.closeDrawers();
                        break;
                    case 2:
                        CardPickerDialog dialog = new CardPickerDialog();
                        dialog.setClickListener(MainActivity.this);
                        dialog.show(getSupportFragmentManager(), "theme");
                        drawerLayout.closeDrawers();

                        break;
                    case 3:
                        TimingFragment fragment3 = new TimingFragment();
                        fragment3.show(getSupportFragmentManager(), "timing");
                        drawerLayout.closeDrawers();

                        break;
                    case 4:
                        BitSetFragment bfragment = new BitSetFragment();
                        bfragment.show(getSupportFragmentManager(), "bitset");
                        drawerLayout.closeDrawers();

                        break;
                    case 5:
                        if (MusicPlayer.isPlaying()) {
                            MusicPlayer.playOrPause();
                        }
                        unbindService();
                        finish();
                        drawerLayout.closeDrawers();
                        break;
                    case 6:
                        mMediaButtonMode = 1 - mMediaButtonMode;
                        MusicPlayer.setMediaButtonMode(mMediaButtonMode);
                        if (mMediaButtonMode == 1)
                            ((TextView)view).setText("普通模式");
                        else
                            ((TextView)view).setText("挑选模式");
                        drawerLayout.closeDrawers();
                        break;
                    case 7:
                        scanSdCard();
                        drawerLayout.closeDrawers();
                        break;
                    case 8:
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent,1);
                        break;
                }
            }
        });
    }
    private static final String TAG = "liTest";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Log.d(TAG, "onActivityResult: "+requestCode);
            if(requestCode == 1) {
                try {
                    Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                    Log.d(TAG, "onActivityResult: " + uri.getPath());
                    File file = new File(uri.getPath());
                    Log.d(TAG, "onActivityResult: " + file.getParent());
                    PreferencesUtility mPreferences = PreferencesUtility.getInstance(this);
                    mPreferences.setScanPath(file.getParent());

//                    Toast.makeText(MainActivity.this, file.toString(), Toast.LENGTH_SHORT).show();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            PreferencesUtility mPreferences = PreferencesUtility.getInstance(this);
            mPreferences.setScanPath("");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    public void scanSDFiles(String path) {
//        try{
//            File root = new File(path);
//            File files[] = root.listFiles();
//            ArrayList<MusicInfo> musicList = new ArrayList<MusicInfo>();
//            if (files != null) {
//                for (File f : files) {
//                    if (f.isFile() && f.getName().endsWith("mp3")) {
//                        MusicInfo info = new MusicInfo();
//                        info.
//                        musicList.add(adapterMusicInfo);
//                    }
//                }
//            }
//            AddNetPlaylistDialog.newInstance(musicList).show(getSupportFragmentManager(), "add");
//            drawerLayout.closeDrawers();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private ScanSdReceiver scanSdReceiver;
    private void scanSdCard(){
        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentfilter.addDataScheme("file");
        scanSdReceiver = new ScanSdReceiver();
        registerReceiver(scanSdReceiver, intentfilter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
        }
    }
    public class ScanSdReceiver extends BroadcastReceiver {
        private AlertDialog.Builder builder = null;
        private AlertDialog ad = null;
        private int count1;
        private int count2;
        private int count;
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)){
                Cursor c1 = context.getContentResolver() .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME }, null, null, null);
                count1 = c1.getCount();
                Log.i("MainActivity", "liTest:onReceive: count="+count);
                builder = new AlertDialog.Builder(context);
                builder.setMessage("正在扫描存储卡...");
                ad = builder.create();
                ad.show();
            } else if(Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)){
                Cursor c2 = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME }, null, null, null);
                count2 = c2.getCount();
                count = count2-count1;
                ad.cancel();
                if(count>=0){
                    Toast.makeText(context, "共增加"+ count + "首歌曲", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(context, "共减少"+ count + "首歌曲", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void switchTabs(int position) {
        for (int i = 0; i < tabs.size(); i++) {
            if (position == i) {
                tabs.get(i).setSelected(true);
            } else {
                tabs.get(i).setSelected(false);
            }
        }
    }

    @Override
    public void onConfirm(int currentTheme) {
        if (ThemeHelper.getTheme(MainActivity.this) != currentTheme) {
            ThemeHelper.setTheme(MainActivity.this, currentTheme);
            ThemeUtils.refreshUI(MainActivity.this, new ThemeUtils.ExtraRefreshable() {
                        @Override
                        public void refreshGlobal(Activity activity) {
                            //for global setting, just do once
                            if (Build.VERSION.SDK_INT >= 21) {
                                final MainActivity context = MainActivity.this;
                                ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(null, null, ThemeUtils.getThemeAttrColor(context, android.R.attr.colorPrimary));
                                setTaskDescription(taskDescription);
                                getWindow().setStatusBarColor(ThemeUtils.getColorById(context, R.color.theme_color_primary));
                            }
                        }

                        @Override
                        public void refreshSpecificView(View view) {
                        }
                    }
            );
        }
        changeTheme();
    }

    static class CustomViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        public CustomViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home: //Menu icon
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashScreen.removeSplashScreen();
    }

    /**
     * 双击返回桌面
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - time > 1000)) {
                Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        moveTaskToBack(true);
        // System.exit(0);
        // finish();
    }
}

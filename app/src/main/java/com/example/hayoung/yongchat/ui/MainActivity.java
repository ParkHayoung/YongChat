package com.example.hayoung.yongchat.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.adapter.MainPagerAdapter;
import com.example.hayoung.yongchat.session.UserSession;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 사용자 세션이 없다면 (로그아웃 상태),
        // 현재화면 종료하고 로그인화면으로 보내기
        if (!UserSession.getInstance().isLoggedIn()) {
            LoginActivity.start(this);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        mViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.dragon)
                .setTitle("알림")
                .setMessage("어플리케이션을 종료하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("아니오", null)
                .setCancelable(false)
                .show();
    }

}

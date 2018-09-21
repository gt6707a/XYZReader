package com.android.gt6707a.xyzreader;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleDetailActivity extends AppCompatActivity {

    @BindView(R.id.pager)
    ViewPager viewPager;

    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);

        pagerAdapter = new ArticleDetailPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private class ArticleDetailPagerAdapter extends FragmentStatePagerAdapter {
        public ArticleDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ArticleDetailFragment();
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}

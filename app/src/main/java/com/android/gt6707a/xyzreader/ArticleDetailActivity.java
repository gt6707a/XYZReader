package com.android.gt6707a.xyzreader;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.gt6707a.xyzreader.data.ArticleLoader;
import com.android.gt6707a.xyzreader.data.ItemsContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleDetailActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

  @BindView(R.id.pager)
  ViewPager viewPager;

  PagerAdapter pagerAdapter;

  Cursor mCursor;

  private long mStartId, mSelectedItemId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article_detail);
    ButterKnife.bind(this);

    getSupportLoaderManager().initLoader(0, null, this);

    pagerAdapter = new ArticleDetailPagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(pagerAdapter);

    if (savedInstanceState == null) {
      if (getIntent() != null) {
        mStartId = ItemsContract.Items.getItemId((Uri) getIntent().getParcelableExtra("itemId"));
        mSelectedItemId = mStartId;
      }
    }
  }

  @Override
  public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return ArticleLoader.newAllArticlesInstance(this);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    mCursor = cursor;
    pagerAdapter.notifyDataSetChanged();

    // Select the start ID
    if (mStartId > 0) {
      mCursor.moveToFirst();
      // TODO: optimize
      while (!mCursor.isAfterLast()) {
        if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
          final int position = mCursor.getPosition();
          viewPager.setCurrentItem(position, false);
          break;
        }
        mCursor.moveToNext();
      }
      mStartId = 0;
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> cursorLoader) {
    mCursor = null;
    pagerAdapter.notifyDataSetChanged();
  }

//  @Override
//  public void onBackPressed() {
//    if (viewPager.getCurrentItem() == 0) {
//      // If the user is currently looking at the first step, allow the system to handle the
//      // Back button. This calls finish() on this activity and pops the back stack.
//      super.onBackPressed();
//    } else {
//      // Otherwise, select the previous step.
//      viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
//    }
//  }

  private class ArticleDetailPagerAdapter extends FragmentStatePagerAdapter {
    public ArticleDetailPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      mCursor.moveToPosition(position);
      return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
    }

    @Override
    public int getCount() {
      return (mCursor != null) ? mCursor.getCount() : 0;
    }
  }
}

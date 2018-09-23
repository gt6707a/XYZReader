package com.android.gt6707a.xyzreader;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.gt6707a.xyzreader.data.ArticleLoader;
import com.android.gt6707a.xyzreader.data.ItemsContract;
import com.android.gt6707a.xyzreader.data.UpdaterService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ArticleListActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

  @BindView(R.id.articles_recycler_view)
  @Nullable
  RecyclerView articlesRecyclerView;

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
  private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);
  private SimpleDateFormat outputFormat = new SimpleDateFormat();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article_list);

    ButterKnife.bind(this);

    getSupportLoaderManager().initLoader(0, null, this);

    if (savedInstanceState == null) {
      refresh();
    }
  }

  private void refresh() {
    startService(new Intent(this, UpdaterService.class));
  }

  @Override
  public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return ArticleLoader.newAllArticlesInstance(this);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    Adapter adapter = new Adapter(cursor);
    adapter.setHasStableIds(true);
    articlesRecyclerView.setAdapter(adapter);
    articlesRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    articlesRecyclerView.setAdapter(null);
  }

  private class Adapter extends RecyclerView.Adapter<ViewHolder> {
    private Cursor mCursor;

    public Adapter(Cursor cursor) {
      mCursor = cursor;
    }

    @Override
    public long getItemId(int position) {
      mCursor.moveToPosition(position);
      return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = getLayoutInflater().inflate(R.layout.article_list_item, parent, false);
      final ViewHolder vh = new ViewHolder(view);
      view.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
              intent.putExtra(
                  "itemId", ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition())));
              startActivity(intent);
            }
          });
      return vh;
    }

    private Date parsePublishedDate() {
      try {
        String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
        return dateFormat.parse(date);
      } catch (ParseException ex) {
        Timber.e(ex);
        Timber.i("passing today's date");
        return new Date();
      }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      mCursor.moveToPosition(position);
      holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
      Date publishedDate = parsePublishedDate();
      if (!publishedDate.before(START_OF_EPOCH.getTime())) {

        holder.subtitleView.setText(
            Html.fromHtml(
                DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(),
                            DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL)
                        .toString()
                    + "<br/>"
                    + " by "
                    + mCursor.getString(ArticleLoader.Query.AUTHOR)));
      } else {
        holder.subtitleView.setText(
            Html.fromHtml(
                outputFormat.format(publishedDate)
                    + "<br/>"
                    + " by "
                    + mCursor.getString(ArticleLoader.Query.AUTHOR)));
      }
      holder.articleImageView.setImageUrl(
          mCursor.getString(ArticleLoader.Query.THUMB_URL),
          ImageLoaderHelper.getInstance(ArticleListActivity.this).getImageLoader());
      holder.articleImageView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
    }

    @Override
    public int getItemCount() {
      return mCursor.getCount();
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.article_image_view)
    public DynamicHeightNetworkImageView articleImageView;

    @BindView(R.id.title_text_view)
    public TextView titleView;

    @BindView(R.id.subtitle_text_view)
    public TextView subtitleView;

    public ViewHolder(View view) {
      super(view);

      ButterKnife.bind(this, view);
    }
  }
}

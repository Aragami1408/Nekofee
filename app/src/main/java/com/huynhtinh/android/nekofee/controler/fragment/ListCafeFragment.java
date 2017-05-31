package com.huynhtinh.android.nekofee.controler.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.huynhtinh.android.nekofee.R;
import com.huynhtinh.android.nekofee.controler.activity.CafeActivity;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.Cafe;
import utils.DataFetcher;

/**
 * Created by TINH HUYNH on 5/24/2017.
 */

public class ListCafeFragment extends Fragment {
    private static final String TAG = "ListCafeFragment";

    private static final String KEY_CURRENT_LOCATION = "currentLocation";
    private static final String KEY_RADIUS = "radiusKey";
    private static final String KEY_CAFES = "cafesKey";

    private Location mCurrentLocation;
    private int mRadius;
    private RecyclerView mRecyclerView;
    private CafeAdapter mCafeAdapter;
    private TextView mEmptyCafeTextView;
    private ArrayList<Cafe> mCafes;

    public static ListCafeFragment newInstance(Location currentLocation, int radius) {

        Bundle args = new Bundle();
        args.putParcelable(KEY_CURRENT_LOCATION, currentLocation);
        args.putInt(KEY_RADIUS, radius);
        ListCafeFragment fragment = new ListCafeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_CURRENT_LOCATION);
            mRadius = savedInstanceState.getInt(KEY_RADIUS);
            mCafes = (ArrayList<Cafe>) savedInstanceState.getSerializable(KEY_CAFES);
        } else {
            mCurrentLocation = getArguments().getParcelable(KEY_CURRENT_LOCATION);
            mRadius = getArguments().getInt(KEY_RADIUS);
        }
        Log.i(TAG, "Received location: lat=" + mCurrentLocation.getLatitude()
                + " ,lon=" + mCurrentLocation.getLongitude());
        Log.i(TAG, "Received radius: " + mRadius);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_cafe, container, false);

        mEmptyCafeTextView = (TextView) view.findViewById(R.id.empty_cafe_text_view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_cafe_recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (!mRecyclerView.canScrollVertically(View.FOCUS_DOWN)) {
                        if (!DataFetcher.getNextPageToken().isEmpty()) {
                            loadMoreCafe(true);
                        }

                    }
                }
            });
        } else {
            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!mRecyclerView.canScrollVertically(View.FOCUS_DOWN)) {
                        if (!DataFetcher.getNextPageToken().isEmpty()) {
                            loadMoreCafe(true);
                        }
                    }
                }
            });
        }
        if (savedInstanceState != null && mCafes != null) {
            updateUI(mCafes);
        } else {
            loadMoreCafe(false);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_CURRENT_LOCATION, mCurrentLocation);
        outState.putInt(KEY_RADIUS, mRadius);
        outState.putSerializable(KEY_CAFES, (Serializable) mCafeAdapter.getCafes());
        super.onSaveInstanceState(outState);
    }

    private void loadMoreCafe(boolean isMore) {
        new FindNearbyCafeTask().execute(mCurrentLocation, mRadius, isMore);
    }

    private void updateUI(List<Cafe> cafes) {

        mEmptyCafeTextView.setVisibility(View.GONE);

        if (mRecyclerView.getAdapter() == null) {
            if (cafes.isEmpty()) {
                mEmptyCafeTextView.setVisibility(View.VISIBLE);
                return;
            }
            mCafeAdapter = new CafeAdapter(cafes);
            mRecyclerView.setAdapter(mCafeAdapter);
        } else {
            mCafeAdapter.getCafes().addAll(cafes);
            mCafeAdapter.notifyDataSetChanged();
        }
    }

    private class CafeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;
        private Cafe mCafe;
        private TextView mNameTextView;
        private TextView mAddressTextView;
        private RatingBar mRatingBar;
        private TextView mRateIndexTextView;
        private TextView mOpenNowTextView;

        public CafeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImageView = (ImageView) itemView.findViewById(R.id.cafe_photo_image_view);
            mNameTextView = (TextView) itemView.findViewById(R.id.cafe_name_text_view);
            mAddressTextView = (TextView) itemView.findViewById(R.id.cafe_address_text_view);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.cafe_rating_bar);
            mRateIndexTextView = (TextView) itemView.findViewById(R.id.rate_index_text_view);
            mOpenNowTextView = (TextView) itemView.findViewById(R.id.open_now_text_view);
        }

        public void bindCafe(Cafe cafe) {
            mCafe = cafe;
            mNameTextView.setText(mCafe.getName());
            mAddressTextView.setText(mCafe.getAddress());
            mRatingBar.setRating(mCafe.getRating());
            mRateIndexTextView.setText("(" + mCafe.getRating() + ")");
            if (mCafe.getOpenNow() != null) {
                mOpenNowTextView.setText(mCafe.getOpenNow());
            }
            int size = Resources.getSystem().getDisplayMetrics().widthPixels;
            String url = new DataFetcher().getPhotoUrl(mCafe.getMainPhotoRef(), size / 3);
            Picasso.with(getActivity())
                    .load(url)
                    .placeholder(R.drawable.no_image)
                    .into(mImageView);
        }

        @Override
        public void onClick(View v) {
            Intent intent = CafeActivity.newIntent(getActivity(), mCafe);
            startActivity(intent);
        }
    }

    private class CafeAdapter extends RecyclerView.Adapter<CafeHolder> {
        private List<Cafe> mCafes;

        public CafeAdapter(List<Cafe> cafes) {
            mCafes = cafes;
        }

        public List<Cafe> getCafes() {
            return mCafes;
        }

        @Override
        public CafeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragement_list_cafe_item, parent, false);
            return new CafeHolder(view);
        }

        @Override
        public void onBindViewHolder(CafeHolder holder, int position) {
            Cafe cafe = mCafes.get(position);
            holder.bindCafe(cafe);
        }

        @Override
        public int getItemCount() {
            return mCafes.size();
        }
    }

    private class FindNearbyCafeTask extends AsyncTask<Object, Void, List<Cafe>> {
        ProgressDialog mProgressDialog;
        private boolean mMore;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading data...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        @Override
        protected List<Cafe> doInBackground(Object... params) {
            Location location = (Location) params[0];
            int radius = (Integer) params[1];
            boolean isMore = (Boolean) params[2];
            mMore = isMore;
            return new DataFetcher().fetchNearbyCafe(location, radius, mMore);
        }

        @Override
        protected void onPostExecute(List<Cafe> cafes) {
            super.onPostExecute(cafes);
            updateUI(cafes);
            mProgressDialog.dismiss();
        }
    }
}

package controller.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.maps.model.LatLng;
import com.huynhtinh.android.nekofee.R;
import controller.activity.MapActivity;
import controller.activity.WebActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Cafe;
import model.Review;
import data.DataFetcher;

/**
 * Created by TINH HUYNH on 5/29/2017.
 */

public class CafeFragment extends Fragment {
    private static final String ARG_CAFE = "cafeArg";
    private static final String ARG_CURRENT_LOCATION = "currentLocationArg";
    private static final String KEY_CAFE = "cafeKey";
    private static final String KEY_CURRENT_LOCATION = "currentLocationKey";

    private ImageView mCafeImageView;
    private TextView mNameTextView;
    private TextView mAddressTextView;
    private RatingBar mRatingBar;
    private TextView mRatingIndexTextView;
    private LinearLayout mContactLayout;
    private LinearLayout mPhoneLayout;
    private LinearLayout mWebsiteLayout;
    private TextView mPhoneTextView;
    private TextView mWebsiteTextView;
    private ImageView mCallButton;
    private ImageView mBrowserButton;
    private LinearLayout mPhotosLayout;
    private SliderLayout mSliderLayout;
    private LinearLayout mWorkingDaysLayout;
    private RecyclerView mWorkingDayRecylerView;
    private LinearLayout mReviewsLayout;
    private RecyclerView mReviewRecyclerView;
    private FloatingActionButton mMapFab;
    private LinearLayout mPriceLevelLinearLayout;
    private TextView mPriceLevelTextView;

    private ArrayList<String> mPhotoRefs;
    private ArrayList<String> mWorkingDays;
    private ArrayList<Review> mReviews;
    private DataFetcher mDataFetcher;
    private Cafe mCafe;
    private Location mCurrentLocation;


    private List<List<HashMap<String, String>>> mRoutes = new ArrayList<>();

    public static CafeFragment newInstance(Cafe cafe, Location currentLocation) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CAFE, cafe);
        args.putParcelable(ARG_CURRENT_LOCATION, currentLocation);

        CafeFragment fragment = new CafeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCafe = (Cafe) savedInstanceState.getSerializable(KEY_CAFE);
            mCurrentLocation = savedInstanceState.getParcelable(KEY_CURRENT_LOCATION);
        } else {
            mCafe = (Cafe) getArguments().getSerializable(ARG_CAFE);
            mCurrentLocation = getArguments().getParcelable(ARG_CURRENT_LOCATION);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cafe, container, false);

        mCafeImageView = (ImageView) view.findViewById(R.id.cafe_image_view);
        mNameTextView = (TextView) view.findViewById(R.id.cafe_name_text_view);
        mAddressTextView = (TextView) view.findViewById(R.id.cafe_address_text_view);
        mRatingBar = (RatingBar) view.findViewById(R.id.cafe_rating_bar);
        mRatingIndexTextView = (TextView) view.findViewById(R.id.rate_index_text_view);
        mContactLayout = (LinearLayout) view.findViewById(R.id.contact_linear_out);
        mPhoneLayout = (LinearLayout) view.findViewById(R.id.phone_linear_layout);
        mWebsiteLayout = (LinearLayout) view.findViewById(R.id.website_linear_layout);
        mPhoneTextView = (TextView) view.findViewById(R.id.phone_number_text_view);
        mWebsiteTextView = (TextView) view.findViewById(R.id.website_text_view);
        mCallButton = (ImageView) view.findViewById(R.id.call_button);
        mBrowserButton = (ImageView) view.findViewById(R.id.browser_button);
        mPhotosLayout = (LinearLayout) view.findViewById(R.id.photos_linear_layout);
        mSliderLayout = (SliderLayout) view.findViewById(R.id.image_slider);
        mWorkingDaysLayout = (LinearLayout) view.findViewById(R.id.working_day_linear_layout);
        mWorkingDayRecylerView = (RecyclerView) view.findViewById(R.id.working_days_recycler_view);
        mReviewsLayout = (LinearLayout) view.findViewById(R.id.reviews_linear_layout);
        mReviewRecyclerView = (RecyclerView) view.findViewById(R.id.reviews_recycler_view);
        mPriceLevelLinearLayout = (LinearLayout) view.findViewById(R.id.price_level_layout);
        mPriceLevelTextView = (TextView) view.findViewById(R.id.price_level_text_view);
        mMapFab = (FloatingActionButton) view.findViewById(R.id.map_fab);
        mMapFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                LatLng cafeLatLng = new LatLng(mCafe.getLatitude(), mCafe.getLongitude());
                Intent intent = MapActivity.newIntent(getActivity(), currentLatLng, cafeLatLng);
                startActivity(intent);
            }
        });

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + mCafe.getPhoneNumber());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });

        mBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = WebActivity.newIntent(getActivity(), Uri.parse(mCafe.getWebsite()));
                startActivity(intent);
            }
        });

        mDataFetcher = new DataFetcher();

        new GetCafeDetailTask().execute();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_CAFE, mCafe);
        outState.putParcelable(KEY_CAFE, mCurrentLocation);
    }


    private void updateUI() {
        // main photo + main info
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = (int) convertDpToPixel(200);
        Picasso.with(getActivity())
                .load(mDataFetcher.getPhotoUrl(mCafe.getMainPhotoRef(), width, height))
                .placeholder(R.drawable.background_dark_blur_crop)
                .into(mCafeImageView);

        mNameTextView.setText(mCafe.getName());
        mAddressTextView.setText(mCafe.getAddress());
        mRatingBar.setRating(mCafe.getRating());
        mRatingIndexTextView.setText("(" + mCafe.getRating() + ")");

        // contact info
        if (mCafe.getPhoneNumber().isEmpty() && mCafe.getWebsite().isEmpty()) {
            mContactLayout.setVisibility(View.GONE);
        }
        if (mCafe.getPhoneNumber().isEmpty()) {
            mPhoneLayout.setVisibility(View.GONE);
        } else {
            mPhoneTextView.setText(mCafe.getPhoneNumber());
        }
        if (mCafe.getWebsite().isEmpty()) {
            mWebsiteLayout.setVisibility(View.GONE);
        } else {
            mWebsiteTextView.setText(mCafe.getWebsite());
        }

        // photos
        mPhotoRefs = mCafe.getPhotoRefs();
        if (mPhotoRefs == null || mPhotoRefs.isEmpty()) {
            mPhotosLayout.setVisibility(View.GONE);
        } else {
            height = (int) convertDpToPixel(250);
            for (String ref : mPhotoRefs) {
                TextSliderView textSliderView = new TextSliderView(getActivity());
                textSliderView
                        .image(mDataFetcher.getPhotoUrl(ref, width, height))
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                mSliderLayout.addSlider(textSliderView);
            }

            mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mSliderLayout.setCustomAnimation(new DescriptionAnimation());
            mSliderLayout.stopAutoCycle();
        }

        //price level
        if (mCafe.getPriceLevel() == -1) {
            mPriceLevelLinearLayout.setVisibility(View.GONE);
        } else {
            switch (mCafe.getPriceLevel()) {
                case 0:
                    mPriceLevelTextView.setText(Cafe.PRICE_LEVEL_0);
                    break;
                case 1:
                    mPriceLevelTextView.setText(Cafe.PRICE_LEVEL_1);
                    break;
                case 2:
                    mPriceLevelTextView.setText(Cafe.PRICE_LEVEL_2);
                    break;
                case 3:
                    mPriceLevelTextView.setText(Cafe.PRICE_LEVEL_3);
                    break;
                case 4:
                    mPriceLevelTextView.setText(Cafe.PRICE_LEVEL_4);
                    break;
            }
        }

        // working days
        mWorkingDays = mCafe.getWorkingDays();
        if (mWorkingDays == null || mWorkingDays.isEmpty()) {
            mWorkingDaysLayout.setVisibility(View.GONE);
        } else {
            mWorkingDayRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mWorkingDayRecylerView.setAdapter(new WorkingDayAdapter());
        }

        //reviews
        mReviews = mCafe.getReviews();
        if (mReviews == null || mReviews.isEmpty()) {
            mReviewsLayout.setVisibility(View.GONE);
        } else {
            mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mReviewRecyclerView.setAdapter(new ReviewAdapter());
        }
    }

    private class WorkingDayHolder extends RecyclerView.ViewHolder {
        private TextView mWorkingDayTextView;

        public WorkingDayHolder(View itemView) {
            super(itemView);
            mWorkingDayTextView = (TextView) itemView.findViewById(R.id.working_day_text_view);
        }

        public void bindItem(String workingDay) {
            mWorkingDayTextView.setText(workingDay);
        }
    }

    private class WorkingDayAdapter extends RecyclerView.Adapter<WorkingDayHolder> {

        @Override
        public WorkingDayHolder onCreateViewHolder(ViewGroup parent, int vieType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_working_day_item,
                    parent, false);
            return new WorkingDayHolder(view);
        }

        @Override
        public void onBindViewHolder(WorkingDayHolder holder, int position) {
            String workingDay = mWorkingDays.get(position);
            holder.bindItem(workingDay);
        }

        @Override
        public int getItemCount() {
            return mWorkingDays.size();
        }
    }

    private class ReviewHolder extends RecyclerView.ViewHolder {
        private TextView mAuthorTextView;
        private RatingBar mRatingBar;
        private TextView mRatingTextView;
        private TextView mTextTextView;
        private TextView mTimeTextView;
        private ImageView mProfilePhotoImageView;

        public ReviewHolder(View itemView) {
            super(itemView);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.author_text_view);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.review_rating_bar);
            mRatingTextView = (TextView) itemView.findViewById(R.id.review_rating_text_view);
            mTextTextView = (TextView) itemView.findViewById(R.id.review_text_text_view);
            mTimeTextView = (TextView) itemView.findViewById(R.id.review_date_text_view);
            mProfilePhotoImageView = (ImageView) itemView.findViewById(R.id.author_photo_image_view);
        }

        public void bindItem(Review review) {
            mAuthorTextView.setText(review.getAuthor());
            mRatingBar.setRating(review.getRating());
            mRatingTextView.setText(getResources().getString(R.string.rating_index, mCafe.getRating() + ""));
            mTextTextView.setText(review.getText());
            mTimeTextView.setText(review.getTime());
            if (!review.getProfilePhotoUrl().isEmpty())
                Picasso.with(getActivity())
                        .load(review.getProfilePhotoUrl())
                        .noPlaceholder()
                        .into(mProfilePhotoImageView);
        }
    }

    private class ReviewAdapter extends RecyclerView.Adapter<ReviewHolder> {

        @Override
        public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_review_item,
                    parent, false);
            return new ReviewHolder(view);
        }

        @Override
        public void onBindViewHolder(ReviewHolder holder, int position) {
            Review review = mReviews.get(position);
            holder.bindItem(review);
        }

        @Override
        public int getItemCount() {
            return mReviews.size();
        }
    }

    private class GetCafeDetailTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog mProgressDialog;

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
        protected Void doInBackground(Void... params) {
            mDataFetcher.fetchCafeDetail(mCafe);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
            updateUI();
        }

    }


}

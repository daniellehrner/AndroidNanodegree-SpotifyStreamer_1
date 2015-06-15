package me.lehrner.spotifystreamer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class TopTracksFragment extends Fragment {
    private static final String KEY_TRACK_LIST = "me.lehrner.spotifystreamer.tracks";
    private static final String KEY_LIST_VIEW = "me.lehrner.spotifystreamer.track.listview";

    private ListView mListView;
    private ArrayList<SpotifyTrackSearchResult> mTracks;
    private View mLoadingView, mRootView;
    private int mShortAnimationDuration;
    private Toast toast;
    private TrackAdapter mTrackAdapter;
    private TopTracks mActivity;

    public TopTracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        return mRootView;
    }

    @SuppressLint("ShowToast")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView = (ListView) mRootView.findViewById(R.id.listview_track_search_result);
        mLoadingView = mRootView.findViewById(R.id.loading_spinner);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        Context mContext = getActivity();
        toast = Toast.makeText(mContext, " ", Toast.LENGTH_SHORT);

        mTrackAdapter =
                new TrackAdapter(
                        mContext, // The current context (this activity)
                        R.layout.top_tracks_item_layout, // The name of the layout ID.
                        new ArrayList<SpotifyTrackSearchResult>());

        mListView.setAdapter(mTrackAdapter);

        SpotifyTrackSearch spotifySearch = new SpotifyTrackSearch();

        if (savedInstanceState != null) {
            Log.d("TrackOnActivityCreated", "is a saved instance");

            mTracks = savedInstanceState.getParcelableArrayList(KEY_TRACK_LIST);
            mListView.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_LIST_VIEW));

            addAllAdapter(mTracks);
            fadeListViewIn();

        } else {
            Log.d("TrackOnActivityCreated", "is not  a saved instance");
            spotifySearch.updateListView(mActivity.getArtistId(), mActivity, this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (TopTracks) activity;
    }

    public void showToast(String message) {
        toast.setText(message);
        toast.show();
    }

    public void addAllAdapter(ArrayList<SpotifyTrackSearchResult> searchResult) {
        mTracks = searchResult;
        mTrackAdapter.addAll(mTracks);
    }

    public void fadeListViewIn() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mListView.setAlpha(0f);
        mListView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mListView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_TRACK_LIST, mTracks);
        outState.putParcelable(KEY_LIST_VIEW, mListView.onSaveInstanceState());

        super.onSaveInstanceState(outState);
    }
}

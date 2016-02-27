package com.marverenic.music.fragments;

import android.content.res.Configuration;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marverenic.music.PlayerController;
import com.marverenic.music.R;
import com.marverenic.music.instances.Song;
import com.marverenic.music.instances.section.LibraryEmptyState;
import com.marverenic.music.instances.section.QueueSection;
import com.marverenic.music.instances.section.SpacerSingleton;
import com.marverenic.music.utils.Themes;
import com.marverenic.music.view.EnhancedAdapters.DragBackgroundDecoration;
import com.marverenic.music.view.EnhancedAdapters.DragDividerDecoration;
import com.marverenic.music.view.EnhancedAdapters.DragDropAdapter;
import com.marverenic.music.view.EnhancedAdapters.DragDropDecoration;
import com.marverenic.music.view.InsetDecoration;

import java.util.List;

public class QueueFragment extends Fragment implements PlayerController.UpdateListener {

    private final List<Song> queue = PlayerController.getQueue();
    private int lastPlayIndex;
    private RecyclerView list;
    private DragDropAdapter adapter;
    private SpacerSingleton bottomSpacer;
    private int itemHeight;
    private int dividerHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list, container, false);

        // Remove the list padding on landscape tablets
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE
                && config.smallestScreenWidthDp >= 600) {
            view.setPadding(0, 0, 0, 0);
        }

        itemHeight = (int) getResources().getDimension(R.dimen.list_height);
        dividerHeight = (int) getResources().getDisplayMetrics().density;
        bottomSpacer = new SpacerSingleton(QueueSection.ID, 0);

        adapter = new DragDropAdapter();
        adapter.setDragSection(new QueueSection(queue));
        adapter.addSection(bottomSpacer);
        adapter.setEmptyState(new LibraryEmptyState(getActivity()) {
            @Override
            public String getEmptyMessage() {
                return getString(R.string.empty_queue);
            }

            @Override
            public String getEmptyMessageDetail() {
                return getString(R.string.empty_queue_detail);
            }

            @Override
            public String getEmptyAction1Label() {
                return "";
            }

            @Override
            public String getEmptyAction2Label() {
                return "";
            }
        });

        list = (RecyclerView) view.findViewById(R.id.list);
        adapter.attach(list);
        list.addItemDecoration(new DragBackgroundDecoration(Themes.getBackgroundElevated()));
        list.addItemDecoration(new DragDividerDecoration(getActivity(), true, R.id.instance_blank));
        //noinspection deprecation
        list.addItemDecoration(new DragDropDecoration(
                (NinePatchDrawable) getResources().getDrawable((Themes.isLight(getContext()))
                        ? R.drawable.list_drag_shadow_light
                        : R.drawable.list_drag_shadow_dark)));

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE
                || getResources().getConfiguration().smallestScreenWidthDp < 600) {
            // Add an inner shadow on phones and portrait tablets
            list.addItemDecoration(new InsetDecoration(
                    getResources().getDrawable(R.drawable.inset_shadow),
                    (int) getResources().getDimension(R.dimen.inset_shadow_height)));
        }

        /*
            Because of the way that CoordinatorLayout lays out children, there isn't a way to get
            the height of this list until it's about to be shown. Since this fragment is dependent
            on having an accurate height of the list (in order to pad the bottom of the list so that
            the playing song is always at the top of the list), we need to have a way to be informed
            when the list has a valid height before it's shown to the user.

            This post request will be run after the layout has been assigned a height and before
            it's shown to the user so that we can set the bottom padding correctly.
         */
        view.post(new Runnable() {
            @Override
            public void run() {
                scrollToNowPlaying();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        PlayerController.registerUpdateListener(this);
        // Assume this fragment's data has gone stale since it was last in the foreground
        onUpdate();
        scrollToNowPlaying();
    }

    @Override
    public void onPause() {
        super.onPause();
        PlayerController.unregisterUpdateListener(this);
    }

    @Override
    public void onUpdate() {
        int currentIndex = PlayerController.getQueuePosition();
        int previousIndex = lastPlayIndex;

        if (currentIndex != lastPlayIndex) {
            lastPlayIndex = currentIndex;

            updateView(previousIndex);
            updateView(currentIndex);

            if (shouldScrollToCurrent()) {
                scrollToNowPlaying();
            }
        }
    }

    /**
     * When views are being updated and scrolled passed at the same time, the attached
     * {@link android.support.v7.widget.RecyclerView.ItemDecoration}s will not appear on the
     * changed item because of its animation.
     *
     * Because this animation implies that items are being removed from the queue, this method
     * will manually update a specific view in a RecyclerView if it's visible. If it's not visible,
     * {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemChanged(int)} will be
     * called instead.
     * @param index The index of the item in the attached RecyclerView adapter to be updated
     */
    private void updateView(int index) {
        int start = list.getChildAdapterPosition(list.getChildAt(0));
        int end = list.getChildAdapterPosition(list.getChildAt(list.getChildCount() - 1));

        if (index - start >= 0 && index - start < end) {
            ViewGroup itemView = (ViewGroup) list.getChildAt(index - start);
            if (itemView != null) {
                itemView.findViewById(R.id.instancePlayingIndicator)
                        .setVisibility(index == lastPlayIndex
                                ? View.VISIBLE
                                : View.GONE);
            }
        } else {
            adapter.notifyItemChanged(index);
        }
    }

    /**
     * @return true if the currently playing song is above or below the current item by the
     *         list's height, if the queue has been restarted, or if repeat all is enabled and
     *         the user wrapped from the front of the queue to the end of the queue
     */
    private boolean shouldScrollToCurrent() {
        int topIndex = list.getChildAdapterPosition(list.getChildAt(0));
        int bottomIndex = list.getChildAdapterPosition(list.getChildAt(list.getChildCount() - 1));

        return Math.abs(topIndex - lastPlayIndex) <= (bottomIndex - topIndex)
                || (queue.size() - bottomIndex <= 2 && lastPlayIndex == 0)
                || (bottomIndex - queue.size() <= 2 && lastPlayIndex == queue.size() - 1);
    }

    private void scrollToNowPlaying() {
        int padding = (lastPlayIndex - queue.size()) * (itemHeight + dividerHeight) - dividerHeight;
        bottomSpacer.setHeight(padding);

        adapter.notifyItemChanged(queue.size());
        ((LinearLayoutManager) list.getLayoutManager())
                .scrollToPositionWithOffset(lastPlayIndex, 0);
    }

    public void updateShuffle() {
        queue.clear();
        queue.addAll(PlayerController.getQueue());
        adapter.notifyDataSetChanged();
        lastPlayIndex = PlayerController.getQueuePosition();
        scrollToNowPlaying();
    }
}

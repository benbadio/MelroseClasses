package com.benbadio.melroseclasses;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.benbadio.melroseclasses.adapters.MelroseEventAdapter;
import com.benbadio.melroseclasses.models.MelroseEvent;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView mMelroseEventListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mEventType;

    private MelroseRssClient.RssResponseHandler mRssResponseHandler =
            new MelroseRssClient.RssResponseHandler() {
        @Override
        public void onResponse(List<MelroseEvent> rssItemList) {
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }

            setupListView(rssItemList);

            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }

        }

        @Override
        public void onErrorResponse() {
            Toast.makeText(MainActivity.this, R.string.feed_request_error, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        findViews();
        requestMelroseEvents(EventTypes.ALL_EVENTS);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMelroseEvents(mEventType);
            }
        });
    }

    private void findViews() {
        mMelroseEventListView = (ListView) findViewById(R.id.event_list_view);
    }

    private void requestMelroseEvents(String eventType) {
        mEventType = eventType;
        MelroseRssClient client = new MelroseRssClient();
        client.request(mRssResponseHandler, mEventType);
    }

    private void setupListView(List<MelroseEvent> rssItemList) {
        final MelroseEventAdapter eventAdapter = new MelroseEventAdapter(this, rssItemList);
        mMelroseEventListView.setAdapter(eventAdapter);
        mMelroseEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MelroseEvent event = eventAdapter.getItem(position);
                openBrowser(event.getLink());
            }
        });
    }

    private void openBrowser(Uri uri) {
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.audio) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_audio) {
            requestMelroseEvents(EventTypes.AUDIO);
        } else if (id == R.id.nav_video) {
            requestMelroseEvents(EventTypes.VIDEO);
        } else if (id == R.id.nav_photo) {
            requestMelroseEvents(EventTypes.PHOTO);
        } else if (id == R.id.nav_fablab) {
            requestMelroseEvents(EventTypes.FABLAB);
        } else if (id == R.id.nav_simulators) {
            requestMelroseEvents(EventTypes.SIMULATOR);
        } else if (id == R.id.nav_all) {
            requestMelroseEvents(EventTypes.ALL_EVENTS);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

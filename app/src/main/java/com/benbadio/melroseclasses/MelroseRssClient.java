package com.benbadio.melroseclasses;

import android.util.Log;

import com.benbadio.melroseclasses.models.MelroseEvent;

import org.apache.http.Header;

import java.util.List;

import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

/**
 * Created by benba on 6/9/2016.
 */
public class MelroseRssClient {
    
    private static final String TAG = MelroseRssClient.class.getSimpleName();

    private static final AsyncRssClient sClient = new AsyncRssClient();

    private static final String rss_url = "http://calendar.ocls.info/evanced/lib/eventsxml.asp?et=Open+Lab%3A+Ask+a+Tech%2C+Technology+Classes&lib=17, 16, 0&nd=7&dm=rss2&LangType=0&do=0&alltime=1";

    public static interface RssResponseHandler {

        public void onResponse(List<MelroseEvent> rssItemList);

        public void onErrorResponse();
    }

    public MelroseRssClient() {

    }

    public void request(final RssResponseHandler handler, final String eventType) {
        sClient.read(rss_url, new AsyncRssResponseHandler() {
            @Override
            public void onSuccess(RssFeed rssFeed) {
                List<RssItem> rssItemList = rssFeed.getRssItems();
                handler.onResponse(MelroseEvent.fromRssItem(rssItemList, eventType));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                Log.e(TAG, "RSS Feed Request Failed.", error);
                handler.onErrorResponse();
            }
        });
    }
}

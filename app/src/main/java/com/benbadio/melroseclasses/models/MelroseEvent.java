package com.benbadio.melroseclasses.models;

import android.net.Uri;
import android.util.Log;

import com.benbadio.melroseclasses.EventTypes;

import org.jsoup.Jsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rejasupotaro.asyncrssclient.RssItem;


/**
 * Created by benba on 6/9/2016.
 */
public class MelroseEvent {

    private static final String TAG = MelroseEvent.class.getName();

    private String mTitle;
    private String mDescription;
    private String mContent;
    private Uri mLink;
    private String mPrimaryEventType;
    private List<String> mTags;
    private Date mStartTime;
    private Date mEndTime;

    public MelroseEvent() {
    }

    public MelroseEvent(String title,
                        String description,
                        String content,
                        Uri link, List<String> tags,
                        String primaryEventType,
                        Date startTime, Date endTime) {

        mTitle = title;
        mDescription = description;
        mContent = content;
        mLink = link;
        mTags = tags;
        mPrimaryEventType = primaryEventType;
        mStartTime = startTime;
        mEndTime = endTime;

    }

    public static MelroseEvent fromRssItem(RssItem rssItem) {
        if (rssItem == null) {
            return new MelroseEvent();
        }

        String rawDescription = Jsoup.parse(rssItem.getDescription()).text();
        String noDateDescription = rssItem.getDescription().split("<br><br>", 2)[1];
        String description = Jsoup.parse(noDateDescription).toString();
        //Get tags from description
        ArrayList<String> tagStrings = getTagsFromDescription(rawDescription);

        //Get start time from description
        Date startTime = getStartDateFromDescription(rawDescription);

        Date endTime = getEndDateFromDescription(rawDescription);

        //Determine primary event type
        String primaryEventType = determineEventType(tagStrings);
        Log.d("EVENT TYPE", primaryEventType);

        MelroseEvent melroseEvent = new MelroseEvent(
                rssItem.getTitle(),
                description,
                rssItem.getContent(),
                rssItem.getLink(),
                tagStrings,
                primaryEventType,
                startTime,
                endTime);



        return melroseEvent;
    }

    private static Date getEndDateFromDescription(String description) {
        String timeString = description.split("\n", 2)[0].replace("When: ", "").replace(" - ", "//");
        String[] timeSplits = timeString.split("//");
        timeSplits[2] = timeSplits[2].split(" Where")[0];

        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy, h:mm a");

        Date date = null;
        try {
            date = format.parse(timeSplits[0] + ", " + timeSplits[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("END DATE", date.toString());
        return date;
    }

    private static Date getStartDateFromDescription(String description)  {
        String timeString = description.split("\n", 2)[0].replace("When: ", "").replace(" - ", "//");
        String[] timeSplits = timeString.split("//");

        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy, h:mm a");

        Date date = null;
        try {
            date = format.parse(timeSplits[0] + ", " + timeSplits[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("START DATE", date.toString());
        return date;


    }

    private static String determineEventType(ArrayList<String> tagList) {
        if (tagList.contains(EventTypes.AUDIO_TAG)) {
            return EventTypes.AUDIO;
        } else if (tagList.contains(EventTypes.VIDEO_TAG)) {
            return EventTypes.VIDEO;
        } else if (tagList.contains(EventTypes.PHOTO_TAG)) {
            return EventTypes.PHOTO;
        } else if (tagList.contains(EventTypes.FABLAB_TAG)) {
            return EventTypes.FABLAB;
        } else if (tagList.contains(EventTypes.SIMULATOR_TAG)) {
            return EventTypes.SIMULATOR;
        } else if (tagList.contains(EventTypes.BASIC_TRAINING_TAG)
                ||tagList.contains(EventTypes.OFFICE_TRAINING_TAG)) {
            return EventTypes.BASIC_TRAINING;}

        return EventTypes.OTHER;
    }

    private static ArrayList<String> getTagsFromDescription(String description) {
        Pattern hashTagPattern = Pattern.compile("#(\\S+)");
        Matcher matcher = hashTagPattern.matcher(description);
        ArrayList<String> tags = new ArrayList<String>();
        while (matcher.find()) {
            tags.add(matcher.group(1));
        }

        //Remove duplicate tag strings
        Set<String> uniqueTags = new HashSet<>();
        uniqueTags.addAll(tags);
        tags.clear();
        tags.addAll(uniqueTags);

        for (Iterator<String> iterator = tags.iterator(); iterator.hasNext(); ) {
            String tag = iterator.next();
            if (tag.equals("tic")) {
                iterator.remove();
            }
        }

        return tags;
    }

    public static List<MelroseEvent> fromRssItem(List<RssItem> rssItemList, String eventType) {
        List<MelroseEvent> melroseEventList = new ArrayList<MelroseEvent>();
        if (rssItemList == null || rssItemList.isEmpty()) {
            return melroseEventList;
        }

        for (RssItem rssItem : rssItemList) {
            melroseEventList.add(fromRssItem(rssItem));
        }

        if (eventType != EventTypes.ALL_EVENTS) {
            Iterator<MelroseEvent> itr = melroseEventList.iterator();
            while (itr.hasNext()) {
                if (itr.next().getPrimaryEventType() != eventType) {
                    itr.remove();
                }
            }
        }

        return melroseEventList;
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Date startTime) {
        mStartTime = startTime;
    }

    public Date getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Date endTime) {
        mEndTime = endTime;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getContent() {
        return mContent;
    }

    public Uri getLink() {
        return mLink;
    }

    public List<String> getTags() {
        return mTags;
    }

    public void setTags(List<String> tags) {
        mTags = tags;
    }

    public String getPrimaryEventType() {
        return mPrimaryEventType;
    }

    public void setPrimaryEventType(String primaryEventType) {
        mPrimaryEventType = primaryEventType;
    }
}

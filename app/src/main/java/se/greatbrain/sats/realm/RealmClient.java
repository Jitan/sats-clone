package se.greatbrain.sats.realm;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.internal.IOException;
import se.greatbrain.sats.model.realm.TrainingActivity;
import se.greatbrain.sats.util.DateUtil;

public class RealmClient
{
    private static final String TAG = "RealmClient";
    private final Context context;
    private static RealmClient INSTANCE;

    public RealmClient(Context context)
    {
        this.context = context.getApplicationContext();
    }

    public static RealmClient getInstance(Context context)
    {
        if(INSTANCE == null)
        {
            INSTANCE = new RealmClient(context);
        }

        return INSTANCE;
    }

    public void addDataToDB(JsonArray result, Context context, Class type)
    {
       Realm realm = Realm.getInstance(context);

        for (JsonElement element : result)
        {
            realm.beginTransaction();

            try
            {
                realm.createOrUpdateObjectFromJson(type, String.valueOf(element));
                realm.commitTransaction();
            }
            catch (IOException e)
            {
                realm.cancelTransaction();
            }
        }

        realm.close();
    }

    public Map<Integer, LinkedHashMap<Integer, List<TrainingActivity>>> getAllActivitiesWithWeek()
    {
        Realm realm = Realm.getInstance(context);
        RealmResults<TrainingActivity> activities = realm.where(TrainingActivity.class).findAll();
        activities.sort("date");
        Map<Integer, LinkedHashMap<Integer, List<TrainingActivity>>> activitiesWithWeek = new
                HashMap<>();

        for (TrainingActivity activity : activities)
        {
            Date date = DateUtil.parseString(activity.getDate());

            if (date != null)
            {
                int year = DateUtil.getYearFromDate(date);
                int weekOfYear = DateUtil.getWeekFromDate(date);

                if (activitiesWithWeek.get(year) == null)
                {
                    LinkedHashMap<Integer, List<TrainingActivity>> trainingActivities = new
                            LinkedHashMap<>();
                    List<TrainingActivity> trainingActivityList = new ArrayList<>();
                    trainingActivityList.add(activity);
                    trainingActivities.put(weekOfYear, trainingActivityList);
                    activitiesWithWeek.put(year, trainingActivities);
                }
                else
                {
                    Map<Integer, List<TrainingActivity>> activitiesMap = activitiesWithWeek.get
                            (year);
                    if(activitiesMap.get(weekOfYear) == null)
                    {
                        List<TrainingActivity> trainingActivityList = new ArrayList<>();
                        trainingActivityList.add(activity);
                        activitiesMap.put(weekOfYear, trainingActivityList);
                    }
                    else
                    {
                        activitiesMap.get(weekOfYear).add(activity);
                    }
                }

            }
        }

        return activitiesWithWeek;
    }
}

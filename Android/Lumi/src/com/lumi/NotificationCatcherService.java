package com.lumi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;

public class NotificationCatcherService extends AccessibilityService {

    private boolean isInit = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        
        String notificationName = "";
        String notificationTitle = "";
        
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            // final String packagename = String.valueOf(event.getPackageName());

            // Toast notification
            List<CharSequence> notificationList = event.getText();
            for (int i = 0; i < notificationList.size(); i++) {
                // Toast.makeText(this.getApplicationContext(), notificationList.get(i), 1).show();
                Log.i("NCS", notificationList.get(i).toString());
                notificationName = notificationList.get(i).toString();
            }

            // Thanks to TomTasche
            // http://stackoverflow.com/questions/9292032/extract-notification-text-from-parcelable-contentview-or-contentintent
            Notification notification = (Notification) event.getParcelableData();
            RemoteViews views = notification.contentView;
            Class secretClass = views.getClass();

            try {
                Map<Integer, String> text = new HashMap<Integer, String>();

                Field outerFields[] = secretClass.getDeclaredFields();
                for (int i = 0; i < outerFields.length; i++) {
                    if (!outerFields[i].getName().equals("mActions"))
                        continue;

                    outerFields[i].setAccessible(true);

                    ArrayList<Object> actions = (ArrayList<Object>) outerFields[i].get(views);
                    for (Object action : actions) {
                        Field innerFields[] = action.getClass().getDeclaredFields();

                        Object value = null;
                        Integer type = null;
                        Integer viewId = null;
                        for (Field field : innerFields) {
                            field.setAccessible(true);
                            if (field.getName().equals("value")) {
                                value = field.get(action);
                            } else if (field.getName().equals("type")) {
                                type = field.getInt(action);
                            } else if (field.getName().equals("viewId")) {
                                viewId = field.getInt(action);
                            }
                        }

                        if (type == 9 || type == 10) {
                            text.put(viewId, value.toString());
                            Log.d("NCS", value.toString());
                        }
                    }
                    
                    notificationTitle = text.get(16908310);
                    System.out.println("title is: " + text.get(16908310));
                    System.out.println("info is: " + text.get(16909082));
                    System.out.println("text is: " + text.get(16908358));
                    
                    if (notificationName.contains("New email")) {
                        if (notificationTitle.toLowerCase(Locale.US).contains("urgent"))
                            this.sendBroadcast(new Intent(Comm.EMAIL_URGENT));
                        else
                            this.sendBroadcast(new Intent(Comm.EMAIL));
                    } else if (notificationTitle.toLowerCase(Locale.US).equals("message")) {
                        this.sendBroadcast(new Intent(Comm.MSG));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onServiceConnected() {
        if (isInit) {
            return;
        }
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        setServiceInfo(info);
        isInit = true;

        Log.d("Service", "NotificationCatcherService Started.");
    }

    @Override
    public void onInterrupt() {
        isInit = false;
    }
}

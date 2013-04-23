package com.lumi;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class NotificationCatcherService extends AccessibilityService {

    private boolean isInit = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            //final String packagename = String.valueOf(event.getPackageName());

            // Toast notification
            List<CharSequence> notificationList = event.getText();
            for (int i = 0; i < notificationList.size(); i++) {
                Toast.makeText(this.getApplicationContext(), notificationList.get(i), 1).show();
                Log.i("NCS", notificationList.get(i).toString());
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

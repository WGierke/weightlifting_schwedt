package de.schwedt.weightlifting.app.helper;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Toast;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;

public class UiHelper {

    public static int VIBRATE_LENGTH = 50;

    public static void showToast(String message, Context context) {
        try {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showDialog(String title, String message, Context context) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message).setTitle(title);
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void hapticFeedback(Application application) {
        vibrate(VIBRATE_LENGTH, application);
    }

    public static void vibrate(Integer length, Application application) {
        Vibrator v = (Vibrator) application.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(length);
    }

    public static void pulseView(final View v, final int count) {
        final Handler handler = new Handler();
        (new Thread() {
            @Override
            public void run() {
                for (int c = 0; c < count; c++) {
                    for (int i = 0; i < 510; i++) {
                        int opacity = i;
                        if (opacity > 255)
                            opacity = 255 - opacity;
                        final int new_opacity = opacity;

                        handler.post(new Runnable() {
                            public void run() {
                                v.setBackgroundColor(Color.argb(new_opacity, 93, 195, 233));
                            }
                        });
                        try {
                            sleep(5);
                        } catch (Exception ex) {
                            break;
                        }
                    }
                }
            }
        }).start();
    }

    public static void pulseView(final View v) {
        pulseView(v, 1);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void colorFade(View view, Resources res) {
        ObjectAnimator colorFade = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), res.getColor(R.color.counter_text_bg), 0xffccc);
        colorFade.setDuration(3000);
        colorFade.start();
    }

    public static void refreshCounterNav(int mainNavPosition, int subNavPosition, int newValue) {
        MainActivity.counter[mainNavPosition][subNavPosition] = newValue;
        MainActivity.navDrawerItems.get(mainNavPosition).setCount(DataHelper.sumOfArray(MainActivity.counter[mainNavPosition]));
    }

    /**
     * Show a notification
     *
     * @param title   Ttile of the notification
     * @param message Message, lines are seperated by a pipe
     * @param description Description of the notification
     * @param notificationId Identifier of the notification
     */
    public static void showNotification(String title, String message, String description, int notificationId, Context context) {

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder normal = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationCompat.InboxStyle big = new NotificationCompat.InboxStyle(normal);
        big.setSummaryText(description);

        String[] parts = message.split("\\|");
        for (int i = 0; i < parts.length; i++) {
            big.addLine(parts[i]);
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, big.build());
    }
}

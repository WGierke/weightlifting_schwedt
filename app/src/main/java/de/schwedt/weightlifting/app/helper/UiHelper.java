package de.schwedt.weightlifting.app.helper;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
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
import android.view.View;
import android.widget.Toast;

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
}

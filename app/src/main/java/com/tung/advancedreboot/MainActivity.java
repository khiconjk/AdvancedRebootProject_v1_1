package com.tung.advancedreboot;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

public class MainActivity extends Activity {
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(36, 48, 36, 36);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("Advanced Reboot");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 24);
        root.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        status = new TextView(this);
        status.setText("Privileged app mode. Required: REBOOT + RECOVERY.");
        status.setTextSize(14);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 0, 0, 24);
        root.addView(status, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        root.addView(makeButton("Reboot", new View.OnClickListener() {
            @Override public void onClick(View v) {
                confirm("Reboot device?", new Runnable() {
                    @Override public void run() { reboot(null); }
                });
            }
        }));

        root.addView(makeButton("Reboot Recovery", new View.OnClickListener() {
            @Override public void onClick(View v) {
                confirm("Reboot to recovery?", new Runnable() {
                    @Override public void run() { reboot("recovery"); }
                });
            }
        }));

        root.addView(makeButton("Reboot Download", new View.OnClickListener() {
            @Override public void onClick(View v) {
                confirm("Reboot to Samsung Download Mode?", new Runnable() {
                    @Override public void run() { reboot("download"); }
                });
            }
        }));

        root.addView(makeButton("Reboot Bootloader", new View.OnClickListener() {
            @Override public void onClick(View v) {
                confirm("Reboot to bootloader?", new Runnable() {
                    @Override public void run() { reboot("bootloader"); }
                });
            }
        }));

        root.addView(makeButton("Power Off", new View.OnClickListener() {
            @Override public void onClick(View v) {
                confirm("Power off device?", new Runnable() {
                    @Override public void run() { shutdown(); }
                });
            }
        }));

        setContentView(root);
    }

    private Button makeButton(String text, View.OnClickListener listener) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextSize(18);
        b.setOnClickListener(listener);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 10, 0, 10);
        b.setLayoutParams(lp);
        return b;
    }

    private void confirm(String message, final Runnable action) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", (dialog, which) -> action.run())
                .show();
    }

    private void reboot(String reason) {
        try {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            pm.reboot(reason);
        } catch (Throwable t) {
            showError("Reboot failed", t);
        }
    }

    private void shutdown() {
        try {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            Method m = PowerManager.class.getMethod(
                    "shutdown",
                    boolean.class,
                    String.class,
                    boolean.class
            );
            m.invoke(pm, false, "userrequested", false);
        } catch (Throwable t) {
            showError("Shutdown failed", t);
        }
    }

    private void showError(String title, Throwable t) {
        String msg = t.getClass().getName() + "\n" + String.valueOf(t.getMessage())
                + "\n\nRequired install:\n"
                + "/system/priv-app/AdvancedReboot/AdvancedReboot.apk\n"
                + "/system/etc/permissions/privapp-permissions-advancedreboot.xml\n\n"
                + "Required allowlisted permissions:\n"
                + "android.permission.REBOOT\n"
                + "android.permission.RECOVERY\n"
                + "android.permission.DEVICE_POWER";
        status.setText(title + ": " + t.getClass().getSimpleName());
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }
}

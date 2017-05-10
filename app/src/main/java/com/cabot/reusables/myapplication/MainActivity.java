package com.cabot.reusables.myapplication;

import android.content.ClipData;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    PopupWindow popupWindow;
    View ghost;
    View anchorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupGhostView();

        findViewById(R.id.button).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
                    if (VERSION.SDK_INT >= VERSION_CODES.N) {
                        v.startDragAndDrop(data, shadowBuilder, v, 0);
                    } else {
                        v.startDrag(data, shadowBuilder, v, 0);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    private void setupGhostView() {
        ghost = findViewById(R.id.ghost);
        ghost.setAlpha(0.0f);
        ghost.findViewById(R.id.txt_append).setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DROP) {
                    Toast.makeText(MainActivity.this, "Settings 1", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        ghost.findViewById(R.id.txt_replace).setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DROP) {
                    Toast.makeText(MainActivity.this, "Settings 2", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setReportFilterMenuListeners(menu);
        return true;
    }

    protected void setReportFilterMenuListeners(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_settings);
        View actionView = item.getActionView();
        if (actionView == null) {
            final int itemId = item.getItemId();
            getWindow().getDecorView()
                    .addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(final View v, int left, int top, int right,
                                                   int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            v.removeOnLayoutChangeListener(this);

                            final View viewById = v.getRootView().findViewById(itemId);
                            anchorView = viewById;
                            preparePopup(viewById);
                            viewById.setOnDragListener(new OnDragListener() {
                                @Override
                                public boolean onDrag(View v, DragEvent event) {
                                    int dragEvent = event.getAction();
                                    switch (dragEvent) {

                                        case DragEvent.ACTION_DRAG_ENTERED:
                                            popupWindow.showAsDropDown(anchorView);
                                            break;

                                        case DragEvent.ACTION_DRAG_ENDED:
                                            popupWindow.dismiss();
                                            break;
                                    }

                                    return true;
                                }
                            });
                        }
                    });
        }

    }

    private void preparePopup(View anchorView) {
        final View popupView = View.inflate(anchorView.getContext(), R.layout.layout_popup, null);
        popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setTouchable(false);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                popupView.getMeasuredWidth(), popupView.getMeasuredHeight());
        params.gravity = Gravity.END;
        ghost.setLayoutParams(params);
        ghost.invalidate();
        ghost.requestLayout();
    }

}

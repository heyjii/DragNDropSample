package com.cabot.reusables.myapplication;

import android.content.ClipData;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView helloText;
    private PopupWindow popUpWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        helloText = (TextView) findViewById(R.id.text);
        helloText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
                    if (VERSION.SDK_INT >= VERSION_CODES.N) {
                        v.startDragAndDrop(data, shadowBuilder, v, 0);
                    }
                    else {
                        v.startDrag(data, shadowBuilder, v, 0);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setReportFilterMenuListeners(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setReportFilterMenuListeners(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_settings);
        if(item!=null) {
            View actionView = item.getActionView();
            if(actionView==null) {
                final int itemId = item.getItemId();
                getWindow().getDecorView()
                           .addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                               @Override
                               public void onLayoutChange(View v, int left, int top, int right,
                                                          int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                   View viewById = v.getRootView().findViewById(itemId);
                                   if(viewById!=null) {
                                       viewById.setOnDragListener(mDragListenerForSettings);
                                       v.removeOnLayoutChangeListener(this);
                                   }
                               }
                           });
            } else {
                actionView.setOnDragListener(mDragListenerForSettings);
            }
        }

    }


    /**
     * Drag listener for Report Filter Menu Item
     */
    OnDragListener mDragListenerForSettings = new OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {

                int dragEvent = event.getAction();

                switch (dragEvent) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //showReportFilterPopUp(v);
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setSelected(true);
                    showPopUp(v);


                    if(popUpWindow != null) {
                        popUpWindow.getContentView().dispatchDragEvent(event);
                    }

                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setSelected(false);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if(popUpWindow != null) {
                        popUpWindow.getContentView().dispatchDragEvent(event);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    v.setSelected(false);

                    if(popUpWindow != null) {
                        popUpWindow.getContentView().dispatchDragEvent(event);
                    }

                    break;

                case DragEvent.ACTION_DRAG_LOCATION:

                    Log.d("Drag", "Drag Location ====== "+event.getX());


                    break;
                default:
                    break;
                }

            return true;
        }
    };


    private void showPopUp(View anchorView) {
        if (popUpWindow == null) {
            View popupView = View.inflate(anchorView.getContext(), R.layout.layout_popup, null);
            //View divider = popupView.findViewById(R.id.menu_divider);
            popUpWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT,
                                          WindowManager.LayoutParams.WRAP_CONTENT);
            popUpWindow.setFocusable(true);
            popUpWindow.setOutsideTouchable(true);

            popUpWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            popUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popUpWindow = null;
                }
            });

            TextView settings1Text = (TextView) popupView.findViewById(R.id.txt_append);

            settings1Text.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b) {
                        Log.d("Drag", "Focus change");
                    }

                }
            });

            settings1Text.setOnHoverListener(new OnHoverListener() {
                @Override
                public boolean onHover(View view, MotionEvent motionEvent) {


                    Log.d("Drag", "on hover");
                    return true;
                }
            });


            settings1Text.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    Log.d("Drag", "on Touch");

                    return false;
                }
            });



            int[] location = new int[2];
            anchorView.getLocationOnScreen(location);
            popUpWindow.setContentView(popupView);
            popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            popUpWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0]+50, location[1]+150);
            //popUpWindow.update(anchorView.getMeasuredWidth() - (int) (anchorView.getMeasuredWidth() * 0.10), ViewGroup.LayoutParams.WRAP_CONTENT);


            popUpWindow.getContentView().setOnDragListener(new OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.d("Drag", "ACTION_DRAG_STARTED");
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.d("Drag", "ACTION_DRAG_ENDED");
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d("Drag", "ACTION_DRAG_ENTERED");
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.d("Drag", "ACTION_DRAG_EXITED");
                        return true;
                    case DragEvent.ACTION_DROP:
                        Log.d("Drag", "ACTION_DROP Report Filter");
                        return true;
                    default:
                        break;
                    }

                    return false;
                }
            });


            popupView.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if(b) {
                        Log.d("Drag", "Focus change");
                    }
                }
            });



        }
    }




}

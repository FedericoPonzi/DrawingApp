package com.informaticalab.drawingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.informaticalab.drawingapp.views.DrawingView;
import com.rey.material.widget.Slider;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DrawingActivity extends AppCompatActivity implements SpectrumPalette.OnColorSelectedListener
{
    public static final String IMAGE_PATH = "IMAGE_PATH_TO_LOAD";
    private static final String LOG_TAG = DrawingActivity.class.getSimpleName();
    private FloatingActionButton undo;
    private FloatingActionButton redo;
    private FloatingActionButton trash;
    private DrawingView drawingView;
    private FloatingActionsMenu fabMenu;
    private FloatingActionsMenu fabMenuTwo;
    private FloatingActionButton export;
    private FloatingActionButton pencilFab;
    private String mCurrentPhotoPath;
    private MaterialDialog pencilDialog;

    private boolean onRubberisSelected = false; //TODO: Salvare nella savedinstancestate.
    private boolean verticalFlipisSelected = false; //TODO: Salvare nella savedinstancestate.
    private boolean horizontalFlipisSelected = false; //TODO: Salvare nella savedinstancestate.
    private boolean cutIsSelected = false; //TODO: Salvare nella savedinstancestate.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        undo = (FloatingActionButton) findViewById(R.id.undo_fab);
        fabMenu = (FloatingActionsMenu) undo.getParent();
        export = (FloatingActionButton) findViewById(R.id.export_fab);

        fabMenuTwo = (FloatingActionsMenu) export.getParent();

        fabMenuTwo.setOnFloatingActionsMenuUpdateListener(
                new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener()
                {
                    @Override
                    public void onMenuExpanded()
                    {
                        fabMenu.expand();
                    }

                    @Override
                    public void onMenuCollapsed()
                    {
                        fabMenu.collapse();
                    }
                });
        drawingView = (DrawingView) findViewById(R.id.drawing_view);
        undo = (FloatingActionButton) findViewById(R.id.undo_fab);
        redo = (FloatingActionButton) findViewById(R.id.redo_fab);
        trash = (FloatingActionButton) findViewById(R.id.trash_fab);
        pencilFab = (FloatingActionButton) findViewById(R.id.pencil_fab);


        pencilFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MaterialDialog.Builder i = new MaterialDialog.Builder(DrawingActivity.this)
                        .title("Tools")
                        .customView(R.layout.pencil_dialog, true)
                        .positiveText(android.R.string.ok).onPositive(
                                new MaterialDialog.SingleButtonCallback()
                                {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which)
                                    {
                                        fabMenuTwo.collapseImmediately();
                                        fabMenu.collapseImmediately();
                                    }
                                });
                pencilDialog = i.build();
                com.rey.material.widget.Slider slider = (com.rey.material.widget.Slider) pencilDialog
                        .findViewById(
                                R.id.slider);
                Log.i(LOG_TAG, "Last brush size: " + drawingView.getBrushSize());
                slider.setValue(drawingView.getBrushSize() / 10, false);
                slider.setOnPositionChangeListener(new Slider.OnPositionChangeListener()
                {
                    @Override
                    public void onPositionChanged(Slider view, boolean fromUser, float
                            oldPos,
                                                  float newPos, int oldValue, int newValue)
                    {
                        drawingView.setBrushSize(newValue * 10);
                    }
                });

                ImageButton rubber = (ImageButton) pencilDialog.findViewById(R.id.rubber_imagebutton);

                rubber.setSelected(onRubberisSelected);
                rubber.setOnTouchListener(new View.OnTouchListener()
                {

                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if (event.getAction() == MotionEvent.ACTION_UP)
                        {
                            v.setSelected(!v.isSelected());
                            onRubberisSelected = v.isSelected();

                            drawingView.toggleErase();
                            return true;
                        }
                        return false;

                    }
                });



                //TODO: Nomi scambiati. LOL
                ImageButton vflip = (ImageButton) pencilDialog.findViewById(R.id.horizontalflip_ib);
                vflip.setSelected(verticalFlipisSelected);
                vflip.setOnTouchListener(new View.OnTouchListener()
                {

                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if (event.getAction() == MotionEvent.ACTION_UP)
                        {
                            v.setSelected(!v.isSelected());
                            verticalFlipisSelected = v.isSelected();
                            drawingView.setVerticalFlip(verticalFlipisSelected);
                            return true;
                        }
                        return false;

                    }
                });

                ImageButton hflip = (ImageButton) pencilDialog.findViewById(R.id.verticalflip_ib);
                hflip.setSelected(horizontalFlipisSelected);
                hflip.setOnTouchListener(new View.OnTouchListener()
                {

                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {

                        if (event.getAction() == MotionEvent.ACTION_UP)
                        {
                            Log.v(LOG_TAG, "HorizontalFlip: " + horizontalFlipisSelected);
                            v.setSelected(!v.isSelected());
                            horizontalFlipisSelected = v.isSelected();
                            drawingView.setHorizontalFlip(horizontalFlipisSelected);
                            return true;
                        }

                        return false;

                    }
                });

                ImageButton cut = (ImageButton) pencilDialog.findViewById(R.id.cut_ib);
                cut.setSelected(cutIsSelected);
                cut.setOnTouchListener(new View.OnTouchListener()
                {

                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if (event.getAction() == MotionEvent.ACTION_UP)
                        {
                            v.setSelected(!v.isSelected());
                            cutIsSelected = v.isSelected();
                            drawingView.toggleGrid();
                            return true;
                        }
                        return false;

                    }
                });
                SpectrumPalette spectrumPalette = (SpectrumPalette) pencilDialog.findViewById(R.id.palette);
                int[] colors = getResources().getIntArray(R.array.demo_colors);
                spectrumPalette.setColors(colors);
                spectrumPalette.setOnColorSelectedListener(DrawingActivity.this
                );
                pencilDialog.show();
            }
        });
        export.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                shareImage();
            }
        });
        undo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawingView.onClickUndo();

            }
        });
        redo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawingView.onClickRedo();
            }
        });

        trash.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new MaterialDialog.Builder(DrawingActivity.this)
                        .title(R.string.dialog_trash_title)
                        .content(Html.fromHtml(getString(R.string.dialog_trash_message)))
                        .positiveText(android.R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback()
                        {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which)
                            {
                                drawingView.eraseAll();
                                fabMenu.collapseImmediately();
                                fabMenuTwo.collapseImmediately();
                            }
                        })
                        .negativeText(android.R.string.no)
                        .onNegative(new MaterialDialog.SingleButtonCallback()
                        {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .negativeColor(ContextCompat.getColor(DrawingActivity.this,
                                                              android.R.color.holo_red_light))
                        .show();
            }
        });
        if (getIntent() != null && getIntent().hasExtra(IMAGE_PATH))
        {
            String path = getIntent().getStringExtra(IMAGE_PATH);
            drawingView.addImage(path);
        }
    }

    @Override
    public void onColorSelected(@ColorInt int color)
    {
        pencilFab.setColorNormal(color);
        drawingView.setColor(color);
        if (pencilDialog.isShowing())
        {
            pencilDialog.dismiss();
        }
    }

    private void shareImage()
    {
        try
        {
            drawingView.setDrawingCacheEnabled(true);
            drawingView.invalidate();
            String path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            File file = new File(path,
                                 "android_drawing_app.png");
            file.getParentFile().mkdirs();

            //Creo un file tmeporaneo:
            file.createTempFile("drawingapp", "" + (System.currentTimeMillis() / 1000));

            fOut = new FileOutputStream(file);


            if (drawingView.getDrawingCache() == null)
            {
                Log.e(LOG_TAG, "Unable to get drawing cache ");
            }
            //drawingView.getBitmap().compress(Bitmap.CompressFormat.PNG, 90, fOut);
            drawingView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 90, fOut);
            //drawingView.getDrawingCache()
            //        .compress(Bitmap.CompressFormat.PNG, 90, fOut);


            fOut.flush();
            fOut.close();

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            shareIntent.setType("image/png");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share image"));

        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, e.getCause() + e.getMessage());
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, e.getCause() + e.getMessage());
        }


    }

    /**
     * Rende disponibile l' immagine
     */
    private void galleryAddPic()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}

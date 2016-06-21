package com.informaticalab.drawingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.informaticalab.drawingapp.utils.ModalBottomSheet;
import com.informaticalab.drawingapp.views.DrawingView;
import com.rey.material.widget.Slider;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class DrawingActivity extends AppCompatActivity
        implements SpectrumPalette.OnColorSelectedListener,
        ModalBottomSheet.OnFragmentInteractionListener
{
    public static final String IMAGE_PATH = "IMAGE_PATH_TO_LOAD";
    private static final String LOG_TAG = DrawingActivity.class.getSimpleName();
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton undo;
    private FloatingActionButton redo;
    private FloatingActionButton trash;
    private DrawingView drawingView;
    private FloatingActionsMenu fabMenu;
    private FloatingActionsMenu fabMenuTwo;
    private FloatingActionButton export;
    private FloatingActionButton pencilFab;
    private File mImageFile;
    private MaterialDialog pencilDialog;
    private ModalBottomSheet mModalBottomSheet;

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
        export = (FloatingActionButton) findViewById(R.id.export_fab);

        fabMenu = (FloatingActionsMenu) undo.getParent();
        fabMenuTwo = (FloatingActionsMenu) export.getParent();

        mModalBottomSheet = new ModalBottomSheet();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);


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
                com.rey.material.widget.Slider slider = (com.rey.material.widget.Slider)
                        pencilDialog
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

                ImageButton rubber = (ImageButton) pencilDialog.findViewById(
                        R.id.rubber_imagebutton);

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
                SpectrumPalette spectrumPalette = (SpectrumPalette) pencilDialog.findViewById(
                        R.id.palette);
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

                mModalBottomSheet.show(getSupportFragmentManager(), mModalBottomSheet.getTag());

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

    @Override
    public void onBackPressed()
    {

        //Sicuro che vuoi uscire?
        if (drawingView.isEdited())
        {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(getString(R.string.alert_added_content_message));

            alert.setPositiveButton(getString(R.string.itsok), new DialogInterface
                    .OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    DrawingActivity.super.onBackPressed();
                }
            });
            alert.setNegativeButton(getString(R.string.wait), new DialogInterface
                    .OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            alert.create().show();
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    public void onShare()
    {
        shareImage();
        mModalBottomSheet.dismiss();
    }

    @Override
    public void onSave()
    {
        saveImage();
        mModalBottomSheet.dismiss();
    }


    private void shareImage()
    {
        if (!saveImage())
        {
            return;
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mImageFile));
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share image"));


    }

    private boolean saveImage()
    {
        try
        {
            drawingView.setDrawingCacheEnabled(true);
            drawingView.invalidate();

            String path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            File directory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
            );
            if(mImageFile == null)
            {
                String filename = "android_drawing_app" + (System.currentTimeMillis() / 1000) + "" + ".png";

                // path to /data/data/yourapp/app_data/imageDir
                // Create imageDir
                Log.i(LOG_TAG, "Path: " + directory.getAbsolutePath());

                mImageFile=new File(directory, filename);

                //mImageFile = new File(path,filename);
                mImageFile.getParentFile().mkdirs();

            }
            Log.i(LOG_TAG, "Path: " + directory.getAbsolutePath());

            //Creo un file tmeporaneo:
            //file.createTempFile("drawingapp", "" );

            fOut = new FileOutputStream(mImageFile);


            if (drawingView.getDrawingCache() == null)
            {
                Log.e(LOG_TAG, "Unable to get drawing cache ");
                Toast.makeText(this, R.string.error_saving_file, Toast.LENGTH_LONG).show();
                return false;
            }

            drawingView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 90, fOut);

            fOut.flush();
            fOut.close();

        }
        catch (Exception e)
        {
            Toast.makeText(this, R.string.error_saving_file, Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, e.getCause() + e.getMessage());
            return false;
        }
        Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_LONG).show();
        galleryAddPic();
        return true;

    }

    /**
     * Rende disponibile l' immagine
     */
    private void galleryAddPic()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(mImageFile);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}

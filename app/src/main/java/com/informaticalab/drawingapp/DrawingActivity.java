package com.informaticalab.drawingapp;

import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.informaticalab.drawingapp.views.DrawingView;

public class DrawingActivity extends AppCompatActivity {
    FloatingActionButton undo;
    FloatingActionButton redo;
    FloatingActionButton trash;
    DrawingView drawingView;
    FloatingActionsMenu fabMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        fabMenu = (FloatingActionsMenu)findViewById(R.id.menu_fab);

        drawingView = (DrawingView) findViewById(R.id.drawing_view);
        undo = (FloatingActionButton) findViewById(R.id.undo_fab);
        redo = (FloatingActionButton) findViewById(R.id.redo_fab);
        trash = (FloatingActionButton) findViewById( R.id.trash_fab);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(DrawingActivity.this)
                        .title(R.string.dialog_trash_title)
                        .content(Html.fromHtml(getString(R.string.dialog_trash_message)))
                        .positiveText(android.R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                drawingView.eraseAll();
                                fabMenu.collapseImmediately();
                            }
                        })
                        .negativeText(android.R.string.no)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .negativeColor(ContextCompat.getColor(DrawingActivity.this, android.R.color.holo_red_light))
                        .show();
            }
        });

    }

}

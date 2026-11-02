package com.example.calcnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {
    private Prefs prefs;
    private DraftDb draftDb;
    private LinedEditText editor;
    private TextView grandTotal;
    private long currentDraftId = -1;
    private String currentDraftName = "Untitled";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Prefs(this);
        draftDb = new DraftDb(this);

        int bg = UiUtils.getColor(this, R.color.background);
        View root = buildUI();
        root.setBackgroundColor(bg);
        setContentView(root);

        long last = prefs.lastDraftId();
        DraftDb.Draft d = (last > 0) ? draftDb.getDraft(last) : null;
        if (d == null) {
            List<DraftDb.Draft> all = draftDb.listDrafts();
            if (!all.isEmpty()) d = all.get(0);
        }
        if (d == null) {
            currentDraftId = draftDb.createDraft("Draft 1", "");
            currentDraftName = "Draft 1";
        } else {
            currentDraftId = d.id;
            currentDraftName = d.name == null ? "Untitled" : d.name;
            editor.setText(d.content == null ? "" : d.content);
            editor.setSelection(editor.getText().length());
        }
        prefs.setLastDraftId(currentDraftId);

        editor.setResultListener(new LinedEditText.ResultListener() {
            public void onTotalsChanged(double grand) {
                grandTotal.setText(format(grand));
            }
        });
        editor.recompute();

        editor.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int a) {}
            public void afterTextChanged(android.text.Editable s) {
                draftDb.updateDraft(currentDraftId, currentDraftName, s.toString());
            }
        });

        if (savedInstanceState == null) {
            Toast.makeText(this, "{MRP}", Toast.LENGTH_SHORT).show();
        }
    }

    private View buildUI() {
        int fg = UiUtils.getColor(this, R.color.foreground);
        int muted = UiUtils.getColor(this, R.color.muted);
        int surface = UiUtils.getColor(this, R.color.surface);
        int accent = UiUtils.getColor(this, R.color.accent);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setPadding(dp(12), dp(10), dp(12), dp(10));
        top.setBackground(UiUtils.roundedBg(surface, 12));

        Button btnDrafts = smallBtn("Drafts", accent);
        Button btnNew = smallBtn("New", accent);
        Button btnSettings = smallBtn("⚙", accent);
        top.addView(btnDrafts);
        top.addView(btnNew);
        top.addView(btnSettings);
        root.addView(top);

        LinearLayout totalBar = new LinearLayout(this);
        totalBar.setOrientation(LinearLayout.HORIZONTAL);
        totalBar.setGravity(Gravity.CENTER_VERTICAL);
        totalBar.setPadding(dp(16), dp(12), dp(16), dp(12));
        totalBar.setBackground(UiUtils.roundedBg(surface, 14));
        TextView lbl = new TextView(this);
        lbl.setText("Total");
        lbl.setTextSize(14);
        lbl.setTextColor(muted);
        grandTotal = new TextView(this);
        grandTotal.setText("0");
        grandTotal.setTextSize(28);
        grandTotal.setTextColor(accent);
        grandTotal.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams glp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        glp.leftMargin = dp(12);
        grandTotal.setLayoutParams(glp);
        totalBar.addView(lbl);
        totalBar.addView(grandTotal);
        root.addView(totalBar);

        editor = new LinedEditText(this);
        editor.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        editor.setGravity(android.view.Gravity.TOP | android.view.Gravity.START);
        editor.setTextSize(16);
        editor.setTextColor(fg);
        editor.setBackground(UiUtils.roundedBg(UiUtils.getColor(this, R.color.background), 16));
        editor.setPadding(dp(16), dp(12), dp(96), dp(12));
        editor.setHint("Type notes and numbers…");
        editor.setHintTextColor(muted);
        root.addView(editor);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showSettings(); }
        });
        btnDrafts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showDrafts(); }
        });
        btnNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { newDraftFlow(); }
        });

        return root;
    }

    private Button smallBtn(String text, int accent) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(13);
        b.setTextColor(Color.WHITE);
        b.setBackground(UiUtils.roundedBg(accent, 10));
        b.setPadding(dp(12), dp(6), dp(12), dp(6));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = dp(6);
        b.setLayoutParams(lp);
        return b;
    }

    private int dp(int v) { return UiUtils.dpToPx(this, v); }

    private String format(double v) {
        if (v == (long) v) return String.valueOf((long) v);
        java.text.DecimalFormat f = new java.text.DecimalFormat("#,##0.##");
        return f.format(v);
    }

    private void newDraftFlow() {
        final EditText input = new EditText(this);
        input.setText(currentDraftName);
        input.selectAll();
        int fg = UiUtils.getColor(this, R.color.foreground);
        int surface = UiUtils.getColor(this, R.color.surface);
        int muted = UiUtils.getColor(this, R.color.muted);
        input.setBackground(UiUtils.roundedBg(surface, 12));
        input.setTextColor(fg);
        input.setHintTextColor(muted);
        input.setPadding(dp(12), dp(8), dp(12), dp(8));
        AlertDialog draftNameDialog = new AlertDialog.Builder(this)
            .setTitle("Name this draft")
            .setView(input)
            .setPositiveButton("Save & New", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int w) {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) name = "Untitled";
                    currentDraftName = name;
                    draftDb.updateDraft(currentDraftId, currentDraftName, editor.getText().toString());
                    int n = draftDb.listDrafts().size() + 1;
                    String newName = "Draft " + n;
                    currentDraftId = draftDb.createDraft(newName, "");
                    currentDraftName = newName;
                    prefs.setLastDraftId(currentDraftId);
                    editor.setText("");
                }
            })
            .setNegativeButton("Cancel", null)
            .create();
        draftNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(surface));
        draftNameDialog.getWindow().setBackgroundDrawable(UiUtils.roundedBg(surface, 16));
        draftNameDialog.show();
    }

    private void showDrafts() {
        final List<DraftDb.Draft> drafts = draftDb.listDrafts();
        if (drafts.isEmpty()) {
            Toast.makeText(this, "No drafts", Toast.LENGTH_SHORT).show();
            return;
        }
        int fg = UiUtils.getColor(this, R.color.foreground);
        int muted = UiUtils.getColor(this, R.color.muted);
        int surface = UiUtils.getColor(this, R.color.surface);
        int accent = UiUtils.getColor(this, R.color.accent);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(8), dp(8), dp(8), dp(8));
        container.setBackground(UiUtils.roundedBg(surface, 16));

        ScrollView sv = new ScrollView(this);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        sv.addView(list);

        for (final DraftDb.Draft dr : drafts) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(dp(12), dp(10), dp(12), dp(10));
            boolean selected = (dr.id == currentDraftId);
            if (selected) {
                row.setBackground(UiUtils.roundedStrokeBg(UiUtils.getColor(this, R.color.accent_soft), 10, 2, accent));
            } else {
                row.setBackground(UiUtils.roundedStrokeBg(surface, 10, 1, UiUtils.getColor(this, R.color.divider)));
            }
            LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlp.bottomMargin = dp(8);
            row.setLayoutParams(rlp);

            TextView nameTv = new TextView(this);
            nameTv.setText(dr.name == null ? "Untitled" : dr.name);
            nameTv.setTextColor(selected ? accent : fg);
            nameTv.setTextSize(15);
            nameTv.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(nameTv);

            String preview = dr.content == null ? "" : dr.content.replace("\n", " ");
            if (preview.length() > 60) preview = preview.substring(0, 60) + "…";
            TextView prevTv = new TextView(this);
            prevTv.setText(preview.isEmpty() ? "(empty)" : preview);
            prevTv.setTextColor(muted);
            prevTv.setTextSize(12);
            row.addView(prevTv);

            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    currentDraftId = dr.id;
                    currentDraftName = dr.name == null ? "Untitled" : dr.name;
                    prefs.setLastDraftId(currentDraftId);
                    editor.setText(dr.content == null ? "" : dr.content);
                    editor.setSelection(editor.getText().length());
                    if (dialog != null) dialog.dismiss();
                }
            });

            Button del = new Button(this);
            del.setText("Delete");
            del.setTextSize(11);
            del.setTextColor(UiUtils.getColor(this, R.color.danger));
            del.setBackgroundColor(Color.TRANSPARENT);
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dlp.topMargin = dp(4);
            del.setLayoutParams(dlp);
            del.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (draftDb.listDrafts().size() <= 1) {
                        Toast.makeText(MainActivity.this, "Can't delete last draft", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    draftDb.deleteDraft(dr.id);
                    if (dr.id == currentDraftId) {
                        List<DraftDb.Draft> rem = draftDb.listDrafts();
                        DraftDb.Draft nd = rem.get(0);
                        currentDraftId = nd.id;
                        currentDraftName = nd.name == null ? "Untitled" : nd.name;
                        prefs.setLastDraftId(currentDraftId);
                        editor.setText(nd.content == null ? "" : nd.content);
                        editor.setSelection(editor.getText().length());
                    }
                    if (dialog != null) dialog.dismiss();
                    showDrafts();
                }
            });
            row.addView(del);

            list.addView(row);
        }
        sv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(360)));
        container.addView(sv);

        dialog = new AlertDialog.Builder(this)
            .setTitle("Drafts")
            .setView(container)
            .setPositiveButton("Close", null)
            .create();
        dialog.getWindow().setBackgroundDrawable(UiUtils.roundedBg(surface, 16));
        dialog.show();
    }

    private void showSettings() {
        int fg = UiUtils.getColor(this, R.color.foreground);
        int muted = UiUtils.getColor(this, R.color.muted);
        int surface = UiUtils.getColor(this, R.color.surface);
        int accent = UiUtils.getColor(this, R.color.accent);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(16), dp(20), dp(16));
        root.setBackground(UiUtils.roundedBg(surface, 16));

        LinearLayout dmRow = new LinearLayout(this);
        dmRow.setOrientation(LinearLayout.HORIZONTAL);
        dmRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView dmLabel = new TextView(this);
        dmLabel.setText("Dark Mode");
        dmLabel.setTextColor(fg);
        dmLabel.setTextSize(15);
        android.widget.CheckBox cb = new android.widget.CheckBox(this);
        cb.setChecked(prefs.darkMode());
        LinearLayout.LayoutParams cblp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cblp.leftMargin = dp(12);
        cb.setLayoutParams(cblp);
        dmRow.addView(dmLabel);
        dmRow.addView(cb);
        root.addView(dmRow);

        TextView themeLabel = new TextView(this);
        themeLabel.setText("Theme color");
        themeLabel.setTextColor(muted);
        themeLabel.setTextSize(13);
        themeLabel.setPadding(0, dp(16), 0, dp(8));
        root.addView(themeLabel);

        int[] presets = {0xff6366f1, 0xff10b981, 0xffef4444, 0xfff59e0b, 0xff06b6d4, 0xffec4899};
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        for (int c : presets) {
            Button b = new Button(this);
            b.setBackgroundColor(c);
            b.setText("");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(36), dp(36));
            lp.rightMargin = dp(8);
            b.setLayoutParams(lp);
            final int color = c;
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    prefs.setAppColor(color);
                    recreate();
                }
            });
            row.addView(b);
        }
        root.addView(row);

        ColorWheelView wheel = new ColorWheelView(this);
        LinearLayout.LayoutParams wlp = new LinearLayout.LayoutParams(dp(200), dp(200));
        wlp.topMargin = dp(12);
        wheel.setLayoutParams(wlp);
        wheel.setOnColorSelectedListener(new ColorWheelView.OnColorSelectedListener() {
            public void onColorSelected(int color) {
                prefs.setAppColor(color);
            }
        });
        root.addView(wheel);

        AlertDialog d = new AlertDialog.Builder(this)
            .setTitle("Settings")
            .setView(root)
            .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface di, int w) {
                    prefs.setDarkMode(cb.isChecked());
                    recreate();
                }
            })
            .setNegativeButton("Cancel", null)
            .create();
        d.getWindow().setBackgroundDrawable(UiUtils.roundedBg(surface, 16));
        d.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (editor != null) {
            draftDb.updateDraft(currentDraftId, currentDraftName, editor.getText().toString());
        }
    }
}

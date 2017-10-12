package com.jeevan.inshorts.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.jeevan.inshorts.R;
import com.jeevan.inshorts.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterActivity extends AppCompatActivity {
    private static final String TAG = "FilterActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cb_cat_busns)
    CheckBox cbBusiness;
    @BindView(R.id.cb_cat_tech)
    CheckBox cbTech;
    @BindView(R.id.cb_cat_entr)
    CheckBox cbEntertainment;
    @BindView(R.id.cb_cat_sports)
    CheckBox cbSports;

    String existingCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_and_filter);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Filter");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        existingCategories = getIntent().getStringExtra(Constants.EX_EXISTING_FILTERS);
        if (existingCategories != null && !existingCategories.equals("")) {
            Log.d(TAG, existingCategories);
            String[] categories = existingCategories.split(",");
            for (String cat : categories) {
                Log.d(TAG, cat);
                switch (cat.charAt(1)) {
                    case 'b': cbBusiness.setChecked(true); break;
                    case 't': cbTech.setChecked(true); break;
                    case 'e': cbEntertainment.setChecked(true); break;
                    case 's': cbSports.setChecked(true); break;
                }
            }
        }
    }

    @OnClick(R.id.btn_apply_filters)
    public void applyFilters(View view) {
        Intent result = new Intent();
        result.putExtra(Constants.CAT_BUSINESS, cbBusiness.isChecked());
        result.putExtra(Constants.CAT_TECH, cbTech.isChecked());
        result.putExtra(Constants.CAT_ENTERTAINMENT, cbEntertainment.isChecked());
        result.putExtra(Constants.CAT_SPORTS, cbSports.isChecked());

        setResult(Constants.RT_FILTER, result);
        finish();
    }

    @OnClick(R.id.btn_clear_filters)
    public void clearFilters(View view) {
        // empty to denote clear filters
        setResult(Constants.RT_FILTER);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.esri.arcgisruntime.sample.featurelayershowattributes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ecomap.a2017.lahacks.sus.R;

/**
 * Displays the Damage type options in a ListView.
 */
public class DamageTypesListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.damage_type_list);
        final String[] damageTypes = getResources().getStringArray(ecomap.a2017.lahacks.sus.R.array.damage_types);

        ListView listView = (ListView) findViewById(ecomap.a2017.lahacks.sus.R.id.listview);

        listView.setAdapter(new ArrayAdapter(this, R.layout.damageattributes, damageTypes));

        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent myIntent = new Intent();
                myIntent.putExtra("typdamage", damageTypes[position]); //Optional parameters
                setResult(100, myIntent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
    }
}
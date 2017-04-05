package ecomap.a2017.lahacks.sus;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.esri.arcgisruntime.sample.featurelayershowattributes.DamageTypesListActivity;
import com.esri.arcgisruntime.sample.featurelayershowattributes.UpdateAttributes;


import java.lang.reflect.Array;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {
   private TextView info;

    Button searchmap, mapgallery, learnmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ecomap.a2017.lahacks.sus.R.layout.activity_home);
        info = (TextView) findViewById(ecomap.a2017.lahacks.sus.R.id.info);
        searchmap = (Button) this.findViewById(R.id.button3);
        mapgallery = (Button) this.findViewById(R.id.button);
        learnmap = (Button) this.findViewById(R.id.button2);


        searchmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, DisplayLocation.class);
                startActivity(intent);

            }
        });

        mapgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, SaveMap.class);
                startActivity(intent);

            }
        });

        learnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, UpdateAttributes.class);
                startActivity(intent);

            }
        });


    }
}




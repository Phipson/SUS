package com.esri.arcgisruntime.sample.editfeatureattachments;

import java.util.List;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import ecomap.a2017.lahacks.sus.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EditFeatureAttachment";
    private static final int REQUEST_CODE = 100;
    private ProgressDialog progressDialog;
    private RelativeLayout mCalloutLayout;
    private MapView mMapView;
    private ArcGISMap mMap;
    private Callout mCallout;
    private FeatureLayer mFeatureLayer;
    private ArcGISFeature mSelectedArcGISFeature;
    private android.graphics.Point mClickPoint;
    private List<Attachment> attachments;
    private String mSelectedArcGISFeatureAttributeValue;
    private String mAttributeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editfeatureattachmentsactmain);
        // inflate MapView from layout
        mMapView = (MapView) findViewById(ecomap.a2017.lahacks.sus.R.id.mapView);
        // create a map with the streets basemap
        mMap = new ArcGISMap(Basemap.Type.STREETS, 44.354388, -119.998245, 5);
        // set the map to be displayed in the mapview
        mMapView.setMap(mMap);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getApplication().getString(ecomap.a2017.lahacks.sus.R.string.fetching_no_attachments));
        progressDialog.setMessage(getApplication().getString(ecomap.a2017.lahacks.sus.R.string.wait));
        createCallout();
        // get callout, set content and show
        mCallout = mMapView.getCallout();
        // create feature layer with its service feature table
        // create the service feature table
        ServiceFeatureTable mServiceFeatureTable = new ServiceFeatureTable(getResources().getString(ecomap.a2017.lahacks.sus.R.string.sample_service_url));
        mServiceFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_CACHE);
        // create the feature layer using the service feature table
        mFeatureLayer = new FeatureLayer(mServiceFeatureTable);

        // set the color that is applied to a selected feature.
        mFeatureLayer.setSelectionColor(Color.rgb(0, 255, 255)); //cyan, fully opaque
        // set the width of selection color
        mFeatureLayer.setSelectionWidth(3);

        // add the layer to the map
        mMap.getOperationalLayers().add(mFeatureLayer);

        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                // get the point that was clicked and convert it to a point in map coordinates
                mClickPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());

                // clear any previous selection
                mFeatureLayer.clearSelection();
                mSelectedArcGISFeature = null;

                // identify the GeoElements in the given layer
                final ListenableFuture<IdentifyLayerResult> futureIdentifyLayer = mMapView.identifyLayerAsync(mFeatureLayer, mClickPoint, 5, false, 1);

                // add done loading listener to fire when the selection returns
                futureIdentifyLayer.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // call get on the future to get the result
                            IdentifyLayerResult layerResult = futureIdentifyLayer.get();

                            List<GeoElement> resultGeoElements = layerResult.getElements();
                            if (resultGeoElements.size() > 0) {
                                if (resultGeoElements.get(0) instanceof ArcGISFeature) {
                                    progressDialog.show();

                                    mSelectedArcGISFeature = (ArcGISFeature) resultGeoElements.get(0);
                                    // highlight the selected feature
                                    mFeatureLayer.selectFeature(mSelectedArcGISFeature);
                                    mAttributeID = mSelectedArcGISFeature.getAttributes().get("objectid").toString();
                                    // get the number of attachments
                                    final ListenableFuture<List<Attachment>> attachmentResults = mSelectedArcGISFeature.fetchAttachmentsAsync();

                                    attachmentResults.addDoneListener(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                attachments = attachmentResults.get();
                                                Log.d("number of attachments :", attachments.size() + "");
                                                // show callout with the value for the attribute "typdamage" of the selected feature
                                                mSelectedArcGISFeatureAttributeValue = (String) mSelectedArcGISFeature.getAttributes().get("typdamage");
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                showCallout(mSelectedArcGISFeatureAttributeValue, attachments.size());
                                                Toast.makeText(getApplicationContext(), getApplication().getString(ecomap.a2017.lahacks.sus.R.string.info_button_message), Toast.LENGTH_SHORT).show();

                                            } catch (Exception e) {
                                                Log.e(TAG, e.getMessage());
                                            }
                                        }
                                    });
                                }
                            } else {
                                // none of the features on the map were selected
                                mCallout.dismiss();
                            }
                        } catch (Exception e) {
                            Log.e(getResources().getString(ecomap.a2017.lahacks.sus.R.string.app_name), "Select feature failed: " + e.getMessage());
                        }
                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    /**
     * Display the callout
     * @param title the damage type text
     * @param noOfAttachments attachment count of the selected feature
     */
    private void showCallout(String title, int noOfAttachments){

        TextView calloutContent = (TextView) mCalloutLayout.findViewById(ecomap.a2017.lahacks.sus.R.id.calloutTextView);
        calloutContent.setText(title);

        TextView calloutAttachment = (TextView) mCalloutLayout.findViewById(ecomap.a2017.lahacks.sus.R.id.attchTV);
        String attachmentText = getApplication().getString(ecomap.a2017.lahacks.sus.R.string.attachment_info_message) + noOfAttachments;
        calloutAttachment.setText(attachmentText);

        mCallout.setLocation(mMapView.screenToLocation(mClickPoint));
        mCallout.setContent(mCalloutLayout);
        mCallout.show();
    }

    /**
     * Create a Layout for callout
     */
    private void createCallout() {

        // create content text view for the callout
        mCalloutLayout = new RelativeLayout(getApplicationContext());
        TextView calloutContent = new TextView(getApplicationContext());
        calloutContent.setId(ecomap.a2017.lahacks.sus.R.id.calloutTextView);
        calloutContent.setTextColor(Color.BLACK);
        calloutContent.setTextSize(18);

        RelativeLayout.LayoutParams relativeParamsBelow = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParamsBelow.addRule(RelativeLayout.BELOW, calloutContent.getId());

        // create attachment text view for the callout
        TextView calloutAttachment = new TextView(getApplicationContext());
        calloutAttachment.setId(ecomap.a2017.lahacks.sus.R.id.attchTV);
        calloutAttachment.setTextColor(Color.BLACK);
        calloutAttachment.setTextSize(13);
        calloutContent.setPadding(0, 20, 20, 0);
        calloutAttachment.setLayoutParams(relativeParamsBelow);

        RelativeLayout.LayoutParams relativeParamsRightOf = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParamsRightOf.addRule(RelativeLayout.RIGHT_OF, calloutAttachment.getId());

        // create image view for the callout
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), ecomap.a2017.lahacks.sus.R.drawable.ic_info));
        imageView.setLayoutParams(relativeParamsRightOf);
        imageView.setOnClickListener(new ImageViewOnclickListener());

        mCalloutLayout.addView(calloutContent);
        mCalloutLayout.addView(imageView);
        mCalloutLayout.addView(calloutAttachment);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            int noOfAttachments = data.getExtras().getInt(getApplication().getString(ecomap.a2017.lahacks.sus.R.string.noOfAttachments));
            // update the callout with attachment count
            showCallout(mSelectedArcGISFeatureAttributeValue, noOfAttachments);
        }

    }

    /**
     * Defines the listener for the ImageView clicks
     */
    private class ImageViewOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // start EditAttachmentActivity to view/edit the attachments
            Intent myIntent = new Intent(MainActivity.this, EditAttachmentActivity.class);
            myIntent.putExtra(getApplication().getString(ecomap.a2017.lahacks.sus.R.string.attribute), mAttributeID);
            myIntent.putExtra(getApplication().getString(ecomap.a2017.lahacks.sus.R.string.noOfAttachments), attachments.size());
            Bundle bundle = new Bundle();
            startActivityForResult(myIntent, REQUEST_CODE, bundle);

        }
    }


}
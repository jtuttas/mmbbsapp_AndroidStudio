package de.mmbbs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

public class UmsatzActivity extends Activity {

	private GraphicalView mChart;
	private String[] mDate = new String[8];
	private double[] mValues = new double[8];
	private ProgressDialog dialog;
	private String xmlRevenue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TabActivity.TAG, "onCreate");
        setContentView(R.layout.umsatz);
		dialog= new ProgressDialog(this);
		dialog.setTitle("Loading...");
		dialog.setMessage("Receive latest Values..");
		dialog.show();
		receiveValues();

        
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}

	 private void receiveValues() {
		// TODO Auto-generated method stub
		 new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				Log.d(TabActivity.TAG,"XML="+xmlRevenue);
				parseXML(xmlRevenue);
				openBarChart();
				dialog.cancel();
			}

			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
        		    // Create a URL for the desired page
        		    URL url = new URL(TabActivity.DB_URL+"revenue/");

        		    // Read all the text returned by the server
        		    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        		    String str;
        		    String s="";
        		    while ((str = in.readLine()) != null) {
        		        // str is one line of text; readLine() strips the newline character(s)
        		    	s=s+str+"\r\n";
        		    }
        		    xmlRevenue=s;
        		    in.close();
        		    return s;
        		} catch (MalformedURLException e) {
        			Log.d(TabActivity.TAG, "Malformed URL Exception bei Lade Revenue");
        			return null;
        		} catch (IOException e) {
        			Log.d(TabActivity.TAG, "IO-Exception bei Lade Revenue");
        			return null;
        		}
			}
	        }.execute(null, null, null);
	}

	 
	private void parseXML(String xml) {
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput( new StringReader ( xmlRevenue ) );
			int eventType = xpp.getEventType();
			int index=-1;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_DOCUMENT) {
					Log.i(TabActivity.TAG,"Start document");
				} else if(eventType == XmlPullParser.START_TAG) {
					Log.i(TabActivity.TAG,"Start tag "+xpp.getName());
					if (xpp.getName().compareTo("revenue")==0) {
						Log.i(TabActivity.TAG," Attribut="+xpp.getAttributeValue(0));
						index++;
						//Toast.makeText(this, "Attribut="+xpp.getAttributeValue(0), Toast.LENGTH_LONG).show();
						mDate[index]=xpp.getAttributeValue(0);
					}
					
				} else if(eventType == XmlPullParser.END_TAG) {
					Log.i(TabActivity.TAG,"End tag "+xpp.getName());
				} else if(eventType == XmlPullParser.TEXT) {
					Log.i(TabActivity.TAG,"Text "+xpp.getText());
					if (xpp.getText().compareTo("\n")!=0) {
						Log.i(TabActivity.TAG," Value="+xpp.getText());
						//Toast.makeText(this, "Text=("+xpp.getText()+")", Toast.LENGTH_LONG).show();
						mValues[index]=Double.parseDouble(xpp.getText());
					}
					
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}		
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (NullPointerException nux) {
			Toast.makeText(this, "Keine Netzwerkverbindung oder Server ist unten",Toast.LENGTH_SHORT).show();
			this.finish();
		}

	}
	
	/*
	private void OpenChart()
	    {
	     // Define the number of elements you want in the chart.
	     int z[]={0,1,2,3,4,5,6,7};
	     
	     
	     //int x[]={10,18,32,21,48,60,53,80};
	     

	      // Create XY Series for X Series.
	     XYSeries xSeries=new XYSeries("Umsätze");
	     

	     //  Adding data to the X Series.
	     for(int i=0;i<z.length;i++)
	     {
	      xSeries.add(z[i],mValues[i]);
	   
	     }

	        // Create a Dataset to hold the XSeries.
	     
	     XYMultipleSeriesDataset dataset=new XYMultipleSeriesDataset();
	     
	      // Add X series to the Dataset.   
	     dataset.addSeries(xSeries);
	     
	     
	      // Create XYSeriesRenderer to customize XSeries

	     XYSeriesRenderer Xrenderer=new XYSeriesRenderer();
	     Xrenderer.setColor(Color.GREEN);
	     Xrenderer.setPointStyle(PointStyle.DIAMOND);
	     Xrenderer.setDisplayChartValues(true);
	     Xrenderer.setLineWidth(2);
	     Xrenderer.setFillPoints(true);
	     
	     // Create XYMultipleSeriesRenderer to customize the whole chart

	     XYMultipleSeriesRenderer mRenderer=new XYMultipleSeriesRenderer();
	     
	     mRenderer.setChartTitle("App Umsätze");
	     mRenderer.setXTitle("Datum");
	     mRenderer.setYTitle("$");
	     mRenderer.setZoomButtonsVisible(false);
	     mRenderer.setXLabels(0);
	     mRenderer.setPanEnabled(false);

	   
	     mRenderer.setShowGrid(true);
	 
	     mRenderer.setClickEnabled(true);
	     
	     for(int i=0;i<z.length;i++)
	     {
	      mRenderer.addXTextLabel(i, mDate[i]);
	     }
	     
	       // Adding the XSeriesRenderer to the MultipleRenderer. 
	     mRenderer.addSeriesRenderer(Xrenderer);
	  
	     
	     LinearLayout chart_container=(LinearLayout)findViewById(R.id.Chart_layout);

	   // Creating an intent to plot line chart using dataset and multipleRenderer
	     
	     mChart=(GraphicalView)ChartFactory.getLineChartView(getBaseContext(), dataset, mRenderer);
	     
	     //  Adding click event to the Line Chart.
	     
	     mChart.setOnClickListener(new View.OnClickListener() {
	   
	   @Override
	   public void onClick(View arg0) {
	    // TODO Auto-generated method stub
	    
	    SeriesSelection series_selection=mChart.getCurrentSeriesAndPoint();
	    
	    if(series_selection!=null)
	    {
	     int series_index=series_selection.getSeriesIndex();
	     
	     String select_series="X Series";
	     if(series_index==0)
	     {
	      select_series="X Series";
	     }else
	     {
	      select_series="Y Series";
	     }
	     
	     String month=mDate[(int)series_selection.getXValue()];
	     
	     int amount=(int)series_selection.getValue();
	     
	     Toast.makeText(getBaseContext(), select_series+"in" + month+":"+amount, Toast.LENGTH_LONG).show();
	    }
	   }
	  });
	  
	   chart_container.addView(mChart);
	}
	*/
	private void openBarChart(){
	    int[] x = { 0,1,2,3,4,5,6,7 };
	    XYSeries incomeSeries = new XYSeries("Einnahmen");

	    // Adding data to Income and Expense Series
	    double max =0;
	    for(int i=0;i<x.length;i++){            
	        incomeSeries.add(i,mValues[i]);
	        if (mValues[i] > max) {
	        	max=mValues[i];
	        }

	    }


	    // Creating a dataset to hold each series
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    // Adding Income Series to the dataset
	    dataset.addSeries(incomeSeries);
	    // Adding Expense Series to dataset

	    // Creating XYSeriesRenderer to customize incomeSeries
	    XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
	    incomeRenderer.setColor(Color.rgb(88, 118, 168));
	    
	    incomeRenderer.setFillPoints(true);
	    incomeRenderer.setLineWidth(2);
	    incomeRenderer.setDisplayChartValues(true);
	    incomeRenderer.setChartValuesTextSize((float)20.0);
	    incomeRenderer.setChartValuesTextAlign(Align.CENTER);
	    
	    

	    // Creating a XYMultipleSeriesRenderer to customize the whole chart
	    XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
	    multiRenderer.setXLabels(0);
	    multiRenderer.setChartTitle("MMBBS App Werbeeinnahmen");
	    multiRenderer.setYTitle("Einnahmen in US Dollar");
	    multiRenderer.setZoomButtonsVisible(true);
	    multiRenderer.setAxisTitleTextSize((float)20.0);
	    multiRenderer.setChartValuesTextSize((float)12.0);
	    multiRenderer.setLabelsTextSize((float)20.0);
	    multiRenderer.setLabelsColor(Color.WHITE);
	    //multiRenderer.setXLabelsAngle(90);
	    
	    //multiRenderer.setXLabelsPadding(250);
	    multiRenderer.setYLabelsPadding(-30);
	    
	    multiRenderer.setBarSpacing(0.1);
	    
	    multiRenderer.setYAxisMax(max*1.5);

	    for(int i=0; i< x.length;i++){
	        multiRenderer.addXTextLabel(i, mDate[i]);
	        
	    }       

	    // Adding incomeRenderer and expenseRenderer to multipleRenderer
	    // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
	    // should be same
	    multiRenderer.addSeriesRenderer(incomeRenderer);
	    //multiRenderer.addSeriesRenderer(expenseRenderer);

	    
		mChart=(GraphicalView)ChartFactory.getBarChartView(getBaseContext(), dataset, multiRenderer, Type.DEFAULT);
	    LinearLayout chart_container=(LinearLayout)findViewById(R.id.Chart_layout);
	    chart_container.addView(mChart);

	}
}
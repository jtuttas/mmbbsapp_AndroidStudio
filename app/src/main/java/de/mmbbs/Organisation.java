package de.mmbbs;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Organisation extends Activity {
	ImageView iv;
	TextView tvn;
	TextView tvf;
	int[] orga = new int[]{R.drawable.ms,R.drawable.op,R.drawable.zm,R.drawable.ws,R.drawable.bo};
	String[] orga_name = new String[] {
			"OStD. Joachim Maiß",
			"StD. Dr. rer. nat. Martin Opitz",
			"StD. Uta Zimmler",
			"StD. Claudia Wessel",
			"StD. Holger Brod"
			};
	String[] orga_funktion = new String[] {
			"Schulleiter, Gesamtverantwortung, Außenvertretung",
			"Vertreter des Schulleiters, Stundenplan",
			"Koordination Medien Teilzeit, Sicherheitsbeauftragte",
			"Koordination IT-Teilzeit-Bildungsgänge, Koordination IT-Kurse und Religionskurse, Internationale Beziehungen",
			"Koordination Medien Vollzeit, Statistik, Vertretungsplanung, Qualitätsmanagement, Befragungssysteme"
	};
	Context context;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        setContentView(R.layout.organisation);


        iv =  (ImageView)this.findViewById(R.id.imageView1);
        iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ms));

        tvn =  (TextView)this.findViewById(R.id.tv_organisation_name);
        tvn.setText(orga_name[0]);

        tvf =  (TextView)this.findViewById(R.id.tv_organisation_funktion);
        tvf.setText(orga_funktion[0]);

        
        Gallery gallery = (Gallery) findViewById(R.id.gallery1);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                //Toast.makeText(Organisation.this, "gewählt" + position+" iv="+iv, Toast.LENGTH_SHORT).show();
                iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), orga[position]));
                tvn.setText(orga_name[position]);
                tvf.setText(orga_funktion[position]);
            }
        });
        
        

    }
}

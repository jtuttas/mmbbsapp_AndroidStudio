package de.mmbbs;

import android.os.Bundle;

/**
 * Die Klasse für den Vertretungsplan.
 * Um Redundanz zu verhindern, wird in dieser Klasse lediglich die Variable vertretungsplan auf true gesetzt.
 * @author Fritz, Lammers, Schwanda
 *
 */
public class Vertretungsplan extends Stundenplan {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setVertretungsplan(true);
		super.onCreate(savedInstanceState);
		
	}
}

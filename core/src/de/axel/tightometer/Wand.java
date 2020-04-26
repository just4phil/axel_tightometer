package de.axel.tightometer;

import com.badlogic.gdx.Gdx;

public class Wand extends Spielobjekt {

	
	public Wand() {
		
		x 				= 33.75f;
		y				= 4.5f;
		h				= 9.765f;
		b				= 2.25f;	
	
		Gdx.app.log("Wand, Position.x: " + x, "Position.y: " + y);
	}
	
}

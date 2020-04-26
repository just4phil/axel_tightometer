package de.axel.tightometer;

import com.badlogic.gdx.Gdx;

public class Auto extends Dynamisches_Spielobjekt{

	Tightometer spiel;
	boolean bremse;
	boolean beschaedigt;
	boolean istGestartet;
	int beschleunigung = 16;	// meter pro Sekunde
	int bremswirkung = -25;		// meter pro Sekunde
	float beschleunigungszeit;
	
	
	public Auto(Tightometer spiel) {
		this.spiel = spiel;
		
		x 				= 0;
		y				= 4.5f;
		h				= 1.125f;	// Meter
		b				= 4.5f;		// Meter
		geschwindigkeit	= 0;
		bremse 			= true;			// Bremse ist angezogen
		beschaedigt 	= false;		// Auto ist unbeschädigt
		istGestartet	= false;		// Auto ist noch nicht gestartet
		hoechstgeschwindigkeit = 0;
		beschleunigungszeit = 0;
		
		Gdx.app.log("Auto, Position.x: " + x, "Position.y: " + y);
	}
	
	
	
	public void beschleunigen(float deltaTime) {
		
		geschwindigkeit = geschwindigkeit + (beschleunigung * deltaTime);
		
		//--- hoechstgeschwindigkeit speichern
		if (geschwindigkeit > hoechstgeschwindigkeit) hoechstgeschwindigkeit = geschwindigkeit;
		
		//--- beschleunigungszeit speichern
		beschleunigungszeit = beschleunigungszeit + deltaTime;
	}
	
	
	public void bremsen(float deltaTime) {
		
		if (geschwindigkeit > 0) {
			
			geschwindigkeit = geschwindigkeit + (bremswirkung * deltaTime);
		}
		else {
			geschwindigkeit = 0;
			Tightometer.bremsen.stop();
		}
	}
	
	
	public void bremse_betaetigen(Tightometer spiel) {
		
		if (spiel.gameOver == false) {
			
			if (bremse == true) {
				bremse = false;
				istGestartet = true;
				Tightometer.playSound(Tightometer.beschleunigung);
				Tightometer.gasgeben.stop();
				Gdx.app.log("Auto", "Bremse gelöst");
			}
			else {
				bremse = true;
				Tightometer.playSound(Tightometer.bremsen);
				Tightometer.beschleunigung.stop();
				Gdx.app.log("Auto", "Bremse angezogen");
				spiel.gameOver = true;
			}
		}
	}
}

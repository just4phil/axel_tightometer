package de.axel.tightometer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.profiling.GL20Interceptor;


public class MainLoop implements Screen {

	Tightometer spiel;
	GL20 gl;
	SpriteBatch batch;
	OrthographicCamera guiCam;
	OrthographicCamera worldCam;
	Texture atlas;
	TextureRegion ferrari;
	TextureRegion ferrari_crash;
	TextureRegion saeule;
	TextureRegion saeule_kaputt;
	TextureRegion boden;
	BitmapFont font; 
	StringBuffer text = new StringBuffer();
	float textWidth;
	boolean click = false;
	boolean exit = false;
	boolean erledigt = false;
	
	Auto auto;
	Wand wand;
	
	 
	
	public MainLoop (Tightometer spiel) {

		this.spiel = spiel;
		setup();

		auto = new Auto(spiel);
		wand = new Wand();

		Tightometer.gasgeben.play();
	}


	

	public void simulieren(float deltaTime) {


		if (auto.beschaedigt == false) {
			
			if (auto.bremse == false) {
				
				auto.beschleunigen(deltaTime);
			}
			else {
				auto.bremsen(deltaTime);
			}
						
			
			auto.x = auto.x + auto.geschwindigkeit * deltaTime;
			
			
			//---- Kollisionserkennung --------------
			if (auto.x + auto.b >= wand.x) {
				
				auto.x = wand.x - auto.b;
				auto.geschwindigkeit = 0;
				auto.beschaedigt = true;	
				spiel.gameOver = true;
				Tightometer.beschleunigung.stop();
				Tightometer.bremsen.stop();
				Tightometer.playSound(Tightometer.crash);
				
				Gdx.app.log("CRASH!", "");
			}
		}
		
		
		//===== auf Erfolg prüfen ==================
		
		if (spiel.gameOver == true && auto.geschwindigkeit == 0 && erledigt == false) {
		
			if (auto.beschaedigt == false && wand.x - auto.x - auto.b < 1f) {
				
				Tightometer.playSound(Tightometer.applaus);	// bei < 1 m -> applaus abspielen
				erledigt = true;
			}
		}
		
		
		
		//===== Click / Touch abfragen und Bremse betätigen ======================
		
		check_Touchscreen();
		
		
		if (click == true) {
			
			if (spiel.gameOver == false) {
				
				auto.bremse_betaetigen(spiel);
				click = false;
			}
			else neues_Spiel();
		}
		
		 
		
		
		//===== Ausgabe ========================
		if (auto.beschaedigt == false) {
			Gdx.app.log("Auto, Position.x: " + roundScale2(auto.x), "Geschwindigkeit: " + roundScale2(auto.geschwindigkeit));
		}
		
	}
	
	
//================ Rendering ==================================
	
	
	
	public void zeichnen(float deltaTime) {
				
		batch.begin();
		//-----------------------
		
		
		if (auto.beschaedigt == false) 	{
			batch.draw(saeule, 	 		wand.x, wand.y, wand.b, wand.h);
			batch.draw(ferrari, 		auto.x, auto.y, auto.b, auto.h);
		}
		else {
			batch.draw(saeule_kaputt, 	wand.x, wand.y, wand.b, wand.h);
			batch.draw(ferrari_crash, 	auto.x, auto.y, auto.b, auto.h);
		}
					
		batch.draw(boden, 0, 0, 36.0f, 4.5f);
		
		//-----------------------
		batch.end();
		
		
		//------ Textausgabe ------
		Text_ausgeben();		
		Daten_ausgeben();
	}


	
//================ ENDE ==================================
	
	
	
	
	
	
	@Override
	public void render(float deltaTime) {
		
		simulieren(deltaTime);

		//------------------------
		
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		worldCam.position.set(auto.x, Spiel.SPIELFELDHOEHE / 2, 0);
		worldCam.update();
		//worldCam.apply(Gdx.gl20);
		gl.glEnable(GL20.GL_TEXTURE_2D);
		batch.setProjectionMatrix(worldCam.combined);
		batch.enableBlending();
		
		zeichnen(deltaTime);
	}	
	@Override
	public void resize(int width, int height) {
	}	
	@Override
	public void resume () {
	}
	@Override
	public void pause () {
	}
	@Override
	public void dispose () {
	}
	@Override
	public void show() {
	}
	@Override
	public void hide() {
	}

	void setup() {
		gl = Gdx.graphics.getGL20();
		Gdx.input.setCatchBackKey(true);
		batch = new SpriteBatch();
		guiCam = new OrthographicCamera();
		guiCam.setToOrtho(false, Tightometer.SCREENBREITE, Tightometer.SCREENHOEHE);
		
		worldCam = new OrthographicCamera();
		worldCam.setToOrtho(false, Tightometer.SPIELFELDBREITE, Tightometer.SPIELFELDHOEHE);
		
		atlas = loadTexture("data/imageatlas/800x480/pages.png");
		ferrari = new TextureRegion(atlas, 1, 1, 100, 25);
		ferrari_crash = new TextureRegion(atlas, 105, 220, 100, 25);
		saeule = new TextureRegion(atlas, 1, 28, 50, 217);
		saeule_kaputt = new TextureRegion(atlas, 53, 28, 50, 217);
		boden = new TextureRegion(atlas, 1, 76, 1, 15);
		font = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);
	}
	
	
	
	void neues_Spiel() {
		
		click = false;
		erledigt = false;
		Tightometer.applaus.stop();
		if (auto.geschwindigkeit == 0) {
			spiel.gameOver = false;
			spiel.setScreen(new MainLoop(spiel));
		}
	}
	
	void check_Touchscreen() {
		
		if (Gdx.input.justTouched()) {
			click = true;
		}
		if (Gdx.input.isKeyPressed(Keys.BACK)) exit = true;
		if (exit == true && Gdx.input.isKeyPressed(Keys.BACK) == false) Gdx.app.exit();
	}

	
	void Text_ausgeben() {
		guiCam.update();
		//guiCam.apply(Gdx.gl20);
		batch.setProjectionMatrix(guiCam.combined);
		batch.enableBlending();
		batch.begin();
		
		text.delete(0, text.length());
		
		if (spiel.gameOver == true) {
			
			if (auto.beschaedigt == true) text.append("CRASH!");
			else {
				if (roundScale2((wand.x - auto.x - auto.b)) >= 1.0f) {
					text.append("Distanz zur Mauer: ").append(roundScale2(wand.x - auto.x - auto.b)).append(" m");
				}
				else {
					text.append("Distanz zur Mauer: ").append(Math.round((wand.x - auto.x - auto.b) * 100)).append(" cm");
				}
				
			}
		}
		else {
			if (auto.istGestartet == false) text.append("Touch zum Beschleunigen");
			else  text.append("Touch zum Bremsen");
		}

		// TODO: alte font funktionen gegen glyphlayout austauschen
		//textWidth = font.getBounds(text).width;
		//font.draw(batch, text, 400 - textWidth / 2, 280);
		
		//----------------------

		batch.end();
	}
	
	
	void Daten_ausgeben() {
		
		batch.begin();
		
		text.delete(0, text.length());
		text.append("Km/h: ").append(roundScale2(3.6f * auto.geschwindigkeit));
		font.draw(batch, text, 50, 450);
		
		//-----------------------
		
		text.delete(0, text.length());
		text.append("Hoechstgeschw.: ").append(roundScale2(3.6f * auto.hoechstgeschwindigkeit));
		font.draw(batch, text, 50, 420);
		
		//-----------------------
		
		text.delete(0, text.length());
		text.append("Beschleunigungszeit: ").append(roundScale2(auto.beschleunigungszeit));
		font.draw(batch, text, 50, 390);

		//-----------------------
		
		text.delete(0, text.length());
		text.append("Autoposition: ").append(roundScale2((auto.x + auto.b)));
		font.draw(batch, text, 50, 360);

		text.delete(0, text.length());
		text.append(roundScale2((auto.x + auto.b))).append(" |");
		//textWidth = font.getBounds(text).width;

		// TODO: alte font funktionen gegen glyphlayout austauschen
//		GlyphLayout layout = new GlyphLayout(); // Obviously stick this in a field to avoid allocation each frame.
//		layout.setText(text.toString());//TODO: stringbuffer to charsequence??
//		font.draw(batch, layout, ((auto.x + auto.b) * Spiel.PXPM) - textWidth + 9, 3.5f * Spiel.PXPM);
		//-----------------------
		
		for (int i = 0; i < 33; i = i + 5) {
			text.delete(0, text.length());
			text.append("| ").append(i);
			font.draw(batch, text, i * Tightometer.PXPM, 4.5f * Tightometer.PXPM);
//			font.draw(batch, text, auto.b * Spiel.PXPM + i * Spiel.PXPM, 4.5f * Spiel.PXPM);
		}
		text.delete(0, text.length());
		text.append("| ").append(wand.x);
		font.draw(batch, text, wand.x * Tightometer.PXPM, 4.5f * Tightometer.PXPM);
		
		batch.end();
	}
	
	
	
	 public static double roundScale2(double d)  {
		 return Math.rint( d * 100 ) / 100.;
	 }
	
	 
	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}
		
}

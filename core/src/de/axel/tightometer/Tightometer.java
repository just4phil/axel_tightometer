package de.axel.tightometer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tightometer extends Game {

	public boolean gameOver = false;
	public static final float SPIELFELDBREITE 	= 36.0f; 	// Meter
	public static final float SPIELFELDHOEHE 	= 21.6f; 	// Meter
	public static final float SCREENBREITE 		= 800; 		// pixel
	public static final float SCREENHOEHE 		= 480; 		// pixel
	public static final float PXPM = 22.222222222222222222222222222222f;

	public static Music gasgeben;
	public static Sound beschleunigung;
	public static Sound bremsen;
	public static Sound crash;
	public static Sound applaus;

	@Override
	public void create () {

		gasgeben = Gdx.audio.newMusic(Gdx.files.internal("data/startup.ogg"));
		gasgeben.setLooping(true);
		gasgeben.setVolume(1f);
		beschleunigung = Gdx.audio.newSound(Gdx.files.internal("data/beschleunigung.ogg"));
		bremsen = Gdx.audio.newSound(Gdx.files.internal("data/bremsen.ogg"));
		crash = Gdx.audio.newSound(Gdx.files.internal("data/crash.ogg"));
		applaus = Gdx.audio.newSound(Gdx.files.internal("data/applaus.ogg"));

		setScreen(new MainLoop(this));
	}

//	@Override
//	public void render () {
//		Gdx.gl.glClearColor(1, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
//	}

	@Override
	public void render() {

		super.render();
	}

	@Override
	public void dispose () {
		super.dispose();

		getScreen().dispose();
	}

	public static void playSound (Sound sound) {
		sound.play(0.5f);
	}
}

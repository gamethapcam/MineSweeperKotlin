package game.minesweeper.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import game.minesweeper.MineSweeperKotlin;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("MineSweeper - Kotlin");
		config.setResizable(false);
		new Lwjgl3Application(new MineSweeperKotlin(), config);
	}
}

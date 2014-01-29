package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.me.rpg.combat.MeleeWeapon;
import com.me.rpg.combat.Projectile;
import com.me.rpg.combat.RangedWeapon;
import com.me.rpg.combat.Weapon;

public class World implements Disposable
{

	private BitmapFont debugFont;
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Map map;
	private PlayableCharacter player;
	private NonplayableCharacter npc;

	public World(SpriteBatch batch, OrthographicCamera camera)
	{
		this.batch = batch;
		this.camera = camera;

		// create characters
		Texture spritesheet = RPG.manager.get(RPG.PLAYER_TEXTURE_PATH);
		player = new PlayableCharacter("Player", spritesheet, 32, 32, 16, 16,
				0.15f);
		player.setSpeed(200f);
		spritesheet = RPG.manager.get(RPG.NPC_TEXTURE_PATH);
		Rectangle walkingBounds = new Rectangle(0, 0, camera.viewportWidth,
				camera.viewportHeight);
		npc = new NonplayableCharacter("NPC", spritesheet, 32, 32, 16, 16,
				0.15f, walkingBounds);

		// map setup
		TiledMap tiledMap = RPG.manager.get(RPG.MAP_TMX_PATH, TiledMap.class);

		// create map and place characters on it
		map = new Map(batch, camera, tiledMap);
		map.addFocusedCharacterToMap(player, map.getWidth() * 3 / 4,
				map.getHeight() / 2);
		map.addCharacterToMap(npc, map.getWidth() * 2 / 3, map.getHeight() / 2);

		debugFont = new BitmapFont();
		debugFont.setColor(0.95f, 0f, 0.23f, 1f); // "Munsell" red
		
		for (int i = 0; i < 5; ++i) {
			npc = new NonplayableCharacter("NPC" + i, spritesheet, 32, 32, 16, 16, 0.15f, walkingBounds);
			map.addCharacterToMap(npc, map.getWidth() * i / 5, map.getHeight() * i / 5);
		}
		
		// melee attack test stuff
		Texture swordSprite = RPG.manager.get(RPG.SWORD_PATH);
		Weapon sword = new MeleeWeapon("LameSword", swordSprite, 32, 32, 32, 32);
		player.equip(sword);
		
		// ranged attack test stuff
		Texture bowSprite = RPG.manager.get(RPG.SWORD_PATH);
		RangedWeapon swordbow = new RangedWeapon("swordbow", bowSprite, 32, 32, 32, 32);
		player.equip(swordbow);
		Projectile p = new Projectile("swordarrow", swordSprite, 32, 32, 32, 32, swordbow);
		swordbow.equipProjectile(p, 50);
		
		// player.equip(sword)
	}

	public void render()
	{
		map.render();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// render HUD and overlays
		float fpsX = camera.position.x - camera.viewportWidth / 2 + 15;
		float fpsY = camera.position.y + camera.viewportHeight / 2 - 15;
		debugFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
				fpsX, fpsY);

		batch.end();
	}

	public void update(float deltaTime)
	{
		map.update(deltaTime);
	}

	@Override
	public void dispose()
	{
		map.dispose();
		debugFont.dispose();
	}

}

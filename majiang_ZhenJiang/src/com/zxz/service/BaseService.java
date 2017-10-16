package com.zxz.service;

import java.util.Map;

import com.zxz.controller.GameManager;
import com.zxz.domain.Game;
import com.zxz.domain.User;

public class BaseService {

	/**µ√µΩ”Œœ∑
	 * @param user
	 * @return
	 */
	public static Game getGame(User user) {
		Map<String, Game> gameMap = GameManager.getGameMap();
		String roomId = user.getRoomId();
		Game game = gameMap.get(roomId);
		return game;
	}
	
}

package com.zxz.domain;

import java.util.List;

/**杠牌的类型
 * @author Administrator
 */
public class GangCard {

	int type;//0放杠 1暗杠 2明杠/公杠
	List<Integer> cards;//杠出的牌
	
	public GangCard(int type, List<Integer> cards) {
		super();
		this.type = type;
		this.cards = cards;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<Integer> getCards() {
		return cards;
	}
	public void setCards(List<Integer> cards) {
		this.cards = cards;
	}
	
	@Override
	public String toString() {
		return cards.toString();
	}
	
}

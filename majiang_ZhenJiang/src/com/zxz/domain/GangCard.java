package com.zxz.domain;

import java.util.List;

/**���Ƶ�����
 * @author Administrator
 */
public class GangCard {

	int type;//0�Ÿ� 1���� 2����/����
	List<Integer> cards;//�ܳ�����
	
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

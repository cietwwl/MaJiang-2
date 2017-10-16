package com.zxz.domain;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class PengCard {

	
	private String chuDir;
	private List<Integer> cards= new ArrayList<>();//碰的牌

	public void addPengCard(List<Integer> pengCards,String chuDir){
		for(int i=0;i<pengCards.size();i++){
			cards.add(pengCards.get(i));
		}
		this.chuDir = chuDir;
	}
	

	
	

	public String getChuDir() {
		return chuDir;
	}





	public List<Integer> getCards() {
		return cards;
	}

	
	
}

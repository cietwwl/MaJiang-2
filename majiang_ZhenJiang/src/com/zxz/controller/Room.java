package com.zxz.controller;

import java.util.LinkedList;
import java.util.List;

import com.zxz.algorithm.Person;




/**����
 * @author Administrator
 */
public class Room {
	
	private String roomNumber;//�����
	private List<Person> persons = new LinkedList<Person>();
	
	public Room() {
	}
	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}
	public Room(List<Person> persons) {
		super();
		this.persons = persons;
	}
	
	/**�������
	 * @param person
	 */
	public void addPerson(Person person){
		if(persons.size()<4){
			persons.add(person);
		}else{
			System.out.println("����������ѡ����������");
		}
	}
	
}

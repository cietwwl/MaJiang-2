package com.zxz.controller;

import java.util.LinkedList;
import java.util.List;

import com.zxz.algorithm.Person;




/**房间
 * @author Administrator
 */
public class Room {
	
	private String roomNumber;//房间号
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
	
	/**房间进人
	 * @param person
	 */
	public void addPerson(Person person){
		if(persons.size()<4){
			persons.add(person);
		}else{
			System.out.println("房间已满请选择其它房间");
		}
	}
	
}

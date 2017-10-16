package domain;

public class Test {

	
	public static void main(String[] args) {
		Object user = new TestUser("gushuang");
		System.out.println(user);
		TestUser u = (TestUser)user;
		System.out.println(u.getName());
	}
}

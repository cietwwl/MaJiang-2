

public class TestMap {

	
	public static void main(String[] args) {
		Student student = new Student();
		student.setName("gushuang");
		Manger.map.put("01", student);
		
		showInfo();
	}

	private static void showInfo() {
		Student student = Manger.map.get("01");
		System.out.println(student.getName());
		student.setName("dd");
		showInfo2();
	}
	
	private static void showInfo2() {
		Student student = Manger.map.get("01");
		System.out.println(student.getName());
		student.setName("dd");
	}
}

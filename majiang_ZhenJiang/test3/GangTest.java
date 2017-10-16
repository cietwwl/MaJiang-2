import java.util.ArrayList;
import java.util.List;

import com.zxz.domain.User;
import com.zxz.service.PlayGameService;

public class GangTest {

	
	
	public static void main(String[] args) {
		int array[]  ={0,1,2,5,6,7  , 8,9,10,  12};
		List<Integer> cards = new ArrayList<>();
		
		for(int i=0;i<array.length;i++){
			cards.add(array[i]);
		}
		
		User user = new User();
		user.setCards(cards);
		
		List<Integer> userCanAnGang = PlayGameService.isUserCanAnGang(user, 118, 11);
		System.out.println(userCanAnGang);
		
	}
	
	
}

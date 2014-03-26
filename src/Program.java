import java.util.ArrayList;
import java.util.HashMap;


public class Program {
	
	HashMap<String,Integer> label = new HashMap<String,Integer>();
	
	int PC = 0;
	int no_instructions = 0;
	
	ArrayList<String> lines = new ArrayList<String>();
	ArrayList<String> instructions = new ArrayList<String>();
	int no_values = 0;
	
	public void setStart(){
		if(label.containsKey("start")){
			PC = label.get("start");
		}
	}
}

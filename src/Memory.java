import java.util.HashMap;


public class Memory {
	
	int FLAGS;
	int remainder;
	int mem_space_size = 10000;
	HashMap<String,Integer> register = new HashMap<String,Integer>();
	String[] mem_space = new String[mem_space_size];
	int stack_top = mem_space_size-1;
	
	
	public void load(Program p){
		//load instructions
		for(int i = 0 ; i < p.instructions.size() ; i++){
			mem_space[i] = p.instructions.get(i);
		}
		register.put("eax", 0);
		register.put("ebx", 0);
		register.put("ecx", 0);
		register.put("edx", 0);
		register.put("esi", 0);
		register.put("edi", 0);
		register.put("esp", mem_space_size-1);
		register.put("ebp", mem_space_size-1);
		register.put("eip", 0);
		register.put("r08", 0);
		register.put("r09", 0);
		register.put("r10", 0);
		register.put("r11", 0);
		register.put("r12", 0);
		register.put("r13", 0);
		register.put("r14", 0);
		register.put("r15", 0);
	}
	
	
	public void stack_push(String value){
		mem_space[stack_top] = value.trim();
		stack_top--;
	}
	
	public int stack_pop(){
		if(stack_top == mem_space_size-1) return -1;
		stack_top++;
		int no = Integer.parseInt(mem_space[stack_top]);
		return no;
	}	
	
}

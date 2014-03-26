import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Profiling {

	int start_pc;
	ArrayList<String> tempDBB = new ArrayList<String>();
	// used to count the frequency for DBB, key-start pc; value-frequency
	HashMap<Integer, Integer> frequency = new HashMap<Integer, Integer>();
	// used to store DBB, key-start pc; value-a list of instructions
	HashMap<Integer, ArrayList<String>> DBB = new HashMap<Integer, ArrayList<String>>();

	public void startDBB(int start_pc) {
		this.start_pc = start_pc;
		this.tempDBB = new ArrayList<String>();
	}

	public void addIns(String instr) {
		tempDBB.add(instr);
	}

	public void endDBB() {
		if (!DBB.containsKey(start_pc))
			DBB.put(start_pc, tempDBB);
		if (frequency.containsKey(start_pc)) {
			int f = frequency.get(start_pc);
			f++;
			frequency.put(start_pc, f);
		} else {
			frequency.put(start_pc, 1);
		}
	}

	public void show() throws IOException {
		File file = new File("profile.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fileWritter = new FileWriter(file);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		Iterator<Integer> itr = frequency.keySet().iterator();
		while (itr.hasNext()) {
			int pc = itr.next();
			//System.out.println(frequency.get(pc));
			bufferWritter.write(frequency.get(pc) + "\r\n");
			ArrayList<String> blocks = DBB.get(pc);
			for (int i = 0; i < blocks.size(); i++) {
				//System.out.println(blocks.get(i));
				bufferWritter.write(blocks.get(i)+ "\r\n");
			}
			//System.out.println();
			bufferWritter.write("\r\n");
		}
		bufferWritter.close();
		fileWritter.close();
	}
}

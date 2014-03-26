import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

	ArrayList<String> codes = new ArrayList<String>();

	public void parseFile(String fileName, Program program) {
		try {
			BufferedReader file = new BufferedReader(new FileReader(fileName));
			String line = file.readLine();
			while (line != null) {
				if (!line.equals(""))
					codes.add(line);
				line = file.readLine();
				//System.out.println(line);
			}
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int no_instruction = 0;
		for (int i = 0; i < codes.size(); i++) {
			String code = codes.get(i).trim();
			String instr = "";
			if (code.startsWith("#"))
				continue;
			if (code.contains("#")) {
				String[] lines = code.split("#");
				program.lines.add(lines[0].trim());
				instr = lines[0].trim();
			} else {
				instr = code.trim();
				program.lines.add(code.trim());
			}
			
			//
			if (instr.contains(":")) {
				if (!instr.trim().endsWith(":")) {
					String[] tokens = instr.split(":");
					String label = tokens[0].trim();
					if (program.label.containsKey(label)) {
						System.out.println("Label " + label
								+ " is defined twice!!!");
						System.exit(1);
					}
					program.label.put(label, no_instruction);
					String instruction = tokens[1].trim();
					// System.out.println(no_instruction + ": " + instruction);
					program.instructions.add(instruction);
					no_instruction++;
				} else {
					String[] tokens = instr.split(":");
					String label = tokens[0].trim();
					if (program.label.containsKey(label)) {
						System.out.println("Label " + label
								+ " is defined twice!!!");
						System.exit(1);
					}
					program.label.put(tokens[0].trim(), no_instruction);
				}
			} else {
				// System.out.println(no_instruction + ": " + instr);
				program.instructions.add(instr);
				no_instruction++;
			}
			//
		}
		program.no_instructions = no_instruction;
		
	}

	public void parseLabel(Program program) {
		ArrayList<String> bcodes = program.lines;
		int no_instruction = 0;
		for (int i = 0; i < bcodes.size(); i++) {
			String instr = bcodes.get(i);
			if (instr.contains(":")) {
				if (!instr.trim().endsWith(":")) {
					String[] tokens = instr.split(":");
					String label = tokens[0].trim();
					if (program.label.containsKey(label)) {
						System.out.println("Label " + label
								+ " is defined twice!!!");
						System.exit(1);
					}
					program.label.put(label, no_instruction);
					String instruction = tokens[1].trim();
					// System.out.println(no_instruction + ": " + instruction);
					program.instructions.add(instruction);
					no_instruction++;
				} else {
					String[] tokens = instr.split(":");
					String label = tokens[0].trim();
					if (program.label.containsKey(label)) {
						System.out.println("Label " + label
								+ " is defined twice!!!");
						System.exit(1);
					}
					program.label.put(tokens[0].trim(), no_instruction);
				}
			} else {
				// System.out.println(no_instruction + ": " + instr);
				program.instructions.add(instr);
				no_instruction++;
			}
		}
		System.out.println();
		System.out.println("No of instruction " + no_instruction);
	}
	public void parseInstruction(String line) {
		String[] tokens = line.split(" ");
		String instr = tokens[0].trim();
		
		int no_arg = 0;
		for (int i = 1; i < tokens.length; i++) {
			String arg = "";
			if (tokens[i].contains(",")) {
				arg = tokens[i].split(",")[0].trim();
			} else {
				arg = tokens[i].trim();
			}
			System.out.print(" " + arg);
			no_arg++;
		}

		System.out.println(" no_arg - " + no_arg);
		// System.out.println(no+": "+line);

	}

}

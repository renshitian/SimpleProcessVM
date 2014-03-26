import java.io.IOException;

public class VM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("Wrong Input Argument");
			System.exit(1);
		}
		VM m = new VM(args[0]);
	}

	String[] opcode = { "mov", "push", "pop", "pushf", "popf", "call", "ret",
			"inc", "dec", "add", "sub", "mul", "div", "mod", "rem", "not",
			"xor", "or", "and", "shl", "shr", "cmp", "jmp", "je", "jne", "jg",
			"jge", "jl", "jle", "prn" };

	Parser parser = new Parser();
	Program program = null;
	Memory memory = null;
	Profiling prof = new Profiling();
	Boolean jump = true;

	public VM(String fileName) {
		program = new Program();
		memory = new Memory();

		parser.parseFile(fileName, program);
		program.setStart();
		prof.start_pc = program.PC;
		memory.load(program);
		while (exe_ins(program, memory))
			;
		System.out.println();
		try {
			prof.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean exe_ins(Program program, Memory memory) {
		int in_index = program.PC;

		if (in_index >= program.no_instructions)
			return false;
		String instruction = memory.mem_space[in_index];

		if (jump) {
			// System.out.println("Start DBB");
			prof.startDBB(in_index);
			prof.addIns(instruction);
			jump = false;
		} else {
			prof.addIns(instruction);
		}

		// System.out.println("PC"+in_index);

		String[] tokens = instruction.split(" ");
		String instr = tokens[0].trim();
		int opcode = in2opcode(instr);
		int ind = 0;
		switch (opcode) {
		case 0:
			doMov(instruction);
			in_index++;
			break;
		case 1:
			doPush(instruction);
			in_index++;
			break;
		case 2:
			doPop(instruction);
			in_index++;
			break;
		case 3:
			doPushf(instruction);
			in_index++;
			break;
		case 4:
			doPopf(instruction);
			in_index++;
			break;
		case 5:
			in_index = doCall(instruction, in_index);
			prof.endDBB();
			jump = true;
			break;
		case 6:
			in_index = doRet() + 1;
			prof.endDBB();
			jump = true;
			// System.out.println("return to caller " + in_index);
			break;
		case 7:
			doInc(instruction);
			in_index++;
			break;
		case 8:
			doDec(instruction);
			in_index++;
			break;
		case 9:
			doAdd(instruction);
			in_index++;
			break;
		case 10:
			doSub(instruction);
			in_index++;
			break;
		case 11:
			doMul(instruction);
			in_index++;
			break;
		case 12:
			doDiv(instruction);
			in_index++;
			break;
		case 13:
			doMod(instruction);
			in_index++;
			break;
		case 14:
			doRem(instruction);
			in_index++;
			break;
		case 15:
			doNot(instruction);
			in_index++;
			break;
		case 16:
			doXor(instruction);
			in_index++;
			break;
		case 17:
			doOr(instruction);
			in_index++;
			break;
		case 18:
			doAnd(instruction);
			in_index++;
			break;
		case 19:
			doShl(instruction);
			in_index++;
			break;
		case 20:
			doShr(instruction);
			in_index++;
			break;
		case 21:
			doCmp(instruction);
			in_index++;
			break;
		case 22:
			in_index = doJmp(instruction);
			prof.endDBB();
			jump = true;
			break;
		case 23:
			ind = doJe(instruction);
			if (ind == -1) {
				in_index++;
			} else {
				in_index = ind;
			}
			prof.endDBB();
			jump = true;
			break;
		case 24:
			ind = doJne(instruction);
			if (ind == -1) {
				in_index++;
			} else {
				in_index = ind;
			}
			prof.endDBB();
			jump = true;
			break;
		case 25:
			ind = doJg(instruction);
			if (ind == -1) {
				in_index++;
			} else {
				in_index = ind;
			}
			prof.endDBB();
			jump = true;
			break;
		case 26:
			ind = doJge(instruction);
			if (ind == -1) {
				in_index++;
			} else {
				in_index = ind;
			}
			prof.endDBB();
			jump = true;
			break;
		case 27:
			ind = doJl(instruction);
			if (ind == -1) {
				in_index++;
			} else {
				in_index = ind;
			}
			prof.endDBB();
			jump = true;
			break;
		case 28:
			ind = doJle(instruction);
			if (ind == -1) {
				in_index++;
			} else {
				in_index = ind;
			}
			prof.endDBB();
			jump = true;
			break;
		case 29:
			doPrn(instruction);
			in_index++;
			break;
		default:
			in_index++;
		}

		// program.current_in++;
		program.PC = in_index;

		return true;
	}

	private void doPrn(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (isRegister(arg0)) {
			int val0 = memory.register.get(arg0);
			System.out.println(val0);
		} else {
			System.out.println(arg0);
		}

	}

	private int doJle(String instruction) {
		if (memory.FLAGS == 2)
			return -1;

		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (arg0.contains("[")) {
			arg0 = arg0.substring(1, arg0.length() - 1);
			return value2no(arg0);
		} else {
			if (program.label.containsKey(arg0)) {
				return program.label.get(arg0);
			} else {
				System.out.println("No such label");
				System.exit(1);
			}
		}
		return 0;
	}

	private int doJl(String instruction) {
		if (memory.FLAGS != 0)
			return -1;

		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (arg0.contains("[")) {
			arg0 = arg0.substring(1, arg0.length() - 1);
			return value2no(arg0);
		} else {
			if (program.label.containsKey(arg0)) {
				return program.label.get(arg0);
			} else {
				System.out.println("No such label");
				System.exit(1);
			}
		}
		return 0;
	}

	private int doJge(String instruction) {
		if (memory.FLAGS == 0)
			return -1;

		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (arg0.contains("[")) {
			arg0 = arg0.substring(1, arg0.length() - 1);
			return value2no(arg0);
		} else {
			if (program.label.containsKey(arg0)) {
				return program.label.get(arg0);
			} else {
				System.out.println("No such label");
				System.exit(1);
			}
		}
		return 0;
	}

	private int doJg(String instruction) {
		if (memory.FLAGS != 2)
			return -1;

		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (arg0.contains("[")) {
			arg0 = arg0.substring(1, arg0.length() - 1);
			return value2no(arg0);
		} else {
			if (program.label.containsKey(arg0)) {
				return program.label.get(arg0);
			} else {
				System.out.println("No such label");
				System.exit(1);
			}
		}
		return 0;
	}

	private int doJne(String instruction) {
		if (memory.FLAGS == 1)
			return -1;

		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (arg0.contains("[")) {
			arg0 = arg0.substring(1, arg0.length() - 1);
			return value2no(arg0);
		} else {
			if (program.label.containsKey(arg0)) {
				return program.label.get(arg0);
			} else {
				System.out.println("No such label");
				System.exit(1);
			}
		}
		return 0;
	}

	private int doJe(String instruction) {
		if (memory.FLAGS != 1)
			return -1;

		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (arg0.contains("[")) {
			arg0 = arg0.substring(1, arg0.length() - 1);
			return value2no(arg0);
		} else {
			if (program.label.containsKey(arg0)) {
				return program.label.get(arg0);
			} else {
				System.out.println("No such label");
				System.exit(1);
			}
		}
		return 0;
	}

	private int doJmp(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (arg0.contains("[")) {
			arg0 = arg0.substring(1, arg0.length() - 1);
			return value2no(arg0);
		} else {
			if (program.label.containsKey(arg0)) {
				return program.label.get(arg0);
			} else {
				System.out.println("No such label");
				System.exit(1);
			}
		}
		return 0;
	}

	private void doCmp(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = "";
		int val0 = 0;
		if (tokens[1].contains(",")) {
			arg0 = tokens[1].split(",")[0].trim();
		} else {
			arg0 = tokens[1].trim();
		}
		if (isRegister(arg0)) {
			val0 = memory.register.get(arg0);
		} else {
			val0 = value2no(arg0);
		}

		String arg1 = tokens[2].trim();
		int val1 = 0;
		if (isRegister(arg1)) {
			val1 = memory.register.get(arg1);
		} else {
			val1 = value2no(arg1);
		}
		int flag = -1;
		if (val0 < val1)
			flag = 0;
		else if (val0 == val1)
			flag = 1;
		else
			flag = 2;

		memory.FLAGS = flag;
		// System.out.println("Compare " + arg0 + ":" + val0 + " and " + arg1
		// + ":" + val1 + " to flag " + memory.FLAGS);
	}

	private void doShr(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (tokens[1].contains(",")) {
			arg0 = tokens[1].split(",")[0].trim();
		} else {
			arg0 = tokens[1].trim();
		}
		if (!isRegister(arg0)) {
			System.out.println("Wrong argument");
			System.exit(-1);
		}
		int val0 = memory.register.get(arg0);
		String arg1 = tokens[2].trim();
		int val1 = -1;
		if (isRegister(arg1)) {
			val1 = memory.register.get(arg1);
		} else {
			val1 = value2no(arg1);
		}

		int newval = val0 >> val1;
		memory.register.put(arg0, newval);
		// System.out.println("shift left " + arg0 + "(" + val0 + ")" + " by "
		// + arg1 + "(" + val1 + ")" + " = " + memory.register.get(arg0));
	}

	private void doShl(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (tokens[1].contains(",")) {
			arg0 = tokens[1].split(",")[0].trim();
		} else {
			arg0 = tokens[1].trim();
		}
		if (!isRegister(arg0)) {
			System.out.println("Wrong argument");
			System.exit(-1);
		}
		int val0 = memory.register.get(arg0);
		String arg1 = tokens[2].trim();
		int val1 = -1;
		if (isRegister(arg1)) {
			val1 = memory.register.get(arg1);
		} else {
			val1 = value2no(arg1);
		}

		int newval = val0 << val1;
		memory.register.put(arg0, newval);
		// System.out.println("shift left " + arg0 + "(" + val0 + ")" + " by "
		// + arg1 + "(" + val1 + ")" + " = " + memory.register.get(arg0));
	}

	private void doAnd(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (tokens[1].contains(",")) {
			arg0 = tokens[1].split(",")[0].trim();
		} else {
			arg0 = tokens[1].trim();
		}
		if (!isRegister(arg0)) {
			System.out.println("Wrong argument");
			System.exit(-1);
		}
		int val0 = memory.register.get(arg0);
		String arg1 = tokens[2].trim();
		int val1 = -1;
		if (isRegister(arg1)) {
			val1 = memory.register.get(arg1);
		} else {
			val1 = value2no(arg1);
		}

		int newval = val0 & val1;
		memory.register.put(arg0, newval);
		// System.out.println("and " + arg0 + "(" + val0 + ")" + " and " + arg1
		// + "(" + val1 + ")" + " = " + memory.register.get(arg0));

	}

	private void doOr(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (tokens[1].contains(",")) {
			arg0 = tokens[1].split(",")[0].trim();
		} else {
			arg0 = tokens[1].trim();
		}
		if (!isRegister(arg0)) {
			System.out.println("Wrong argument");
			System.exit(-1);
		}
		int val0 = memory.register.get(arg0);
		String arg1 = tokens[2].trim();
		int val1 = -1;
		if (isRegister(arg1)) {
			val1 = memory.register.get(arg1);
		} else {
			val1 = value2no(arg1);
		}

		int newval = val0 | val1;
		memory.register.put(arg0, newval);
		// System.out.println("or " + arg0 + "(" + val0 + ")" + " and " + arg1
		// + "(" + val1 + ")" + " = " + memory.register.get(arg0));

	}

	private void doXor(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (tokens[1].contains(",")) {
			arg0 = tokens[1].split(",")[0].trim();
		} else {
			arg0 = tokens[1].trim();
		}
		if (!isRegister(arg0)) {
			System.out.println("Wrong argument");
			System.exit(-1);
		}
		int val0 = memory.register.get(arg0);
		String arg1 = tokens[2].trim();
		int val1 = -1;
		if (isRegister(arg1)) {
			val1 = memory.register.get(arg1);
		} else {
			val1 = value2no(arg1);
		}

		int newval = val0 ^ val1;
		memory.register.put(arg0, newval);
		// System.out.println("xor " + arg0 + "(" + val0 + ")" + " and " + arg1
		// + "(" + val1 + ")" + " = " + memory.register.get(arg0));
	}

	private void doNot(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (isRegister(arg0)) {
			int no = memory.register.get(arg0);
			int newno = ~no;
			memory.register.put(arg0, newno);
			// System.out.println("binary not " + arg0 + "(" + no + ")" + " = "
			// + newno);
		}
	}

	private void doRem(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (isRegister(arg0)) {
			memory.register.put(arg0, memory.remainder);
			// System.out.println("rem " + arg0 + " to " + memory.remainder);
		}
	}

	private void doMod(String instruction) {

		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (tokens[1].contains(",")) {
			arg0 = tokens[1].split(",")[0].trim();
		} else {
			arg0 = tokens[1].trim();
		}
		if (!isRegister(arg0)) {
			System.out.println("Wrong argument");
			System.exit(-1);
		}
		int val0 = memory.register.get(arg0);
		String arg1 = tokens[2].trim();
		int val1 = -1;
		if (isRegister(arg1)) {
			val1 = memory.register.get(arg1);
		} else {
			val1 = value2no(arg1);
		}
		memory.remainder = val0 % val1;
		// System.out.println("mod " + arg0 + " (" + val0 + ")" + " by " + arg1
		// + "(" + val1 + ")" + " = " + memory.remainder);

	}

	private void doDiv(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (tokens[1].contains(",")) {
			arg0 = tokens[1].split(",")[0].trim();
		} else {
			arg0 = tokens[1].trim();
		}
		if (!isRegister(arg0)) {
			System.out.println("Wrong argument");
			System.exit(-1);
		}
		int val0 = memory.register.get(arg0);
		String arg1 = tokens[2].trim();
		int val1 = -1;
		if (isRegister(arg1)) {
			val1 = memory.register.get(arg1);
		} else {
			val1 = value2no(arg1);
		}
		memory.register.put(arg0, val0 / val1);
		memory.remainder = val0 % val1;
		// System.out.println("div " + arg0 + " (" + val0 + ")" + " by " + arg1
		// + "(" + val1 + ")" + " = " + memory.register.get(arg0)
		// + " remain " + memory.remainder);

	}

	private void doMul(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (tokens[1].contains(",")) {
			arg0 = tokens[1].split(",")[0].trim();
		} else {
			arg0 = tokens[1].trim();
		}
		if (!isRegister(arg0)) {
			System.out.println("Wrong argument");
			System.exit(-1);
		}
		int val0 = memory.register.get(arg0);
		String arg1 = tokens[2].trim();
		int val1 = -1;
		if (isRegister(arg1)) {
			val1 = memory.register.get(arg1);
		} else {
			val1 = value2no(arg1);
		}
		memory.register.put(arg0, val0 * val1);
		// System.out.println("mul " + arg0 + " (" + val0 + ")" + " and " + arg1
		// + "(" + val1 + ")" + " = " + memory.register.get(arg0));
	}

	private void doSub(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (tokens[1].contains(",")) {
			arg0 = tokens[1].split(",")[0].trim();
		} else {
			arg0 = tokens[1].trim();
		}
		if (!isRegister(arg0)) {
			System.out.println("Wrong argument");
			System.exit(-1);
		}
		int val0 = memory.register.get(arg0);
		String arg1 = tokens[2].trim();
		int val1 = -1;
		if (isRegister(arg1)) {
			val1 = memory.register.get(arg1);
		} else {
			val1 = value2no(arg1);
		}
		memory.register.put(arg0, val0 - val1);
		// System.out.println("sub " + arg0 + " (" + val0 + ")" + " and " + arg1
		// + "(" + val1 + ")" + " = " + memory.register.get(arg0));

	}

	private void doAdd(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (tokens[1].contains(",")) {
			arg0 = tokens[1].split(",")[0].trim();
		} else {
			arg0 = tokens[1].trim();
		}
		if (!isRegister(arg0)) {
			System.out.println("Wrong argument");
			System.exit(-1);
		}
		int val0 = memory.register.get(arg0);
		String arg1 = tokens[2].trim();
		int val1 = -1;
		if (isRegister(arg1)) {
			val1 = memory.register.get(arg1);
		} else {
			val1 = value2no(arg1);
		}
		memory.register.put(arg0, val0 + val1);
		// System.out.println("add " + arg0 + " (" + val0 + ")" + " and " + arg1
		// + "(" + val1 + ")" + " = " + memory.register.get(arg0));
	}

	private void doDec(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (isRegister(arg0)) {
			int no = memory.register.get(arg0) - 1;
			memory.register.put(arg0, no);
			// System.out.println("decrease " + arg0 + " by 1");
		}
	}

	private void doInc(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		if (isRegister(arg0)) {
			int no = memory.register.get(arg0) + 1;
			memory.register.put(arg0, no);
			// System.out.println("increase " + arg0 + " by 1");
		}

	}

	private int doRet() {
		return memory.stack_pop();
	}

	private int doCall(String instruction, int in_index) {
		memory.stack_push(String.valueOf(in_index));
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		// System.out.println("call label " + arg0 + " for "
		// + program.label.get(arg0));
		return program.label.get(arg0);
	}

	private void doPushf(String instruction) {
		memory.stack_push(String.valueOf(memory.FLAGS));
		// System.out.println("Push Flag " + memory.FLAGS + " to stack");
	}

	private void doPopf(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		int value = memory.stack_pop();
		memory.register.put(arg0, value);
		// System.out.println("pop Flag: " + value + " to register " + arg0);
	}

	private void doPop(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = tokens[1].trim();
		int value = memory.stack_pop();
		memory.register.put(arg0, value);
		// System.out.println("pop " + value + " to register " + arg0);

	}

	private void doPush(String instruction) {
		String[] tokens = instruction.split(" ");
		String arg0 = "";
		if (isRegister(tokens[1])) {
			arg0 = String.valueOf(memory.register.get(tokens[1]));
		} else {
			arg0 = String.valueOf(value2no(tokens[1]));
		}
		// System.out.println("push " + arg0 + " to stack");
		memory.stack_push(arg0);
	}

	private void doMov(String instruction) {
		String[] tokens = instruction.split(" ");
		String register = "";
		if (tokens[1].contains(",")) {
			register = tokens[1].split(",")[0].trim();
		} else {
			register = tokens[1].trim();
		}
		String arg1 = tokens[2].trim();
		int val = 0;

		if (isRegister(arg1)) {
			val = memory.register.get(arg1);
		} else {
			val = value2no(arg1);
		}
		memory.register.put(register, val);
		// System.out.println("move " + memory.register.get(register)
		// + " to register " + register);
	}

	public int value2no(String value) {
		if (value.contains("x")) {
			String[] tokens = value.split("x");
			int no = Integer.parseInt(tokens[1].trim(), 16);
			return no;
		} else if (value.contains("|")) {
			String[] tokens = value.split("\\|");
			if (tokens[1].trim().equals("h")) {
				return Integer.parseInt(tokens[0].trim(), 16);
			} else if (tokens[1].trim().equals("b")) {
				return Integer.parseInt(tokens[0].trim(), 2);
			} else
				return 0;
		} else {
			return Integer.parseInt(value, 10);
		}
	}

	public boolean isRegister(String register) {
		if (memory.register.containsKey(register))
			return true;
		else
			return false;
	}

	public int in2opcode(String instr) {
		int code = -1;
		for (int i = 0; i < opcode.length; i++) {
			if (opcode[i].equals(instr)) {
				code = i;
			}
		}
		return code;
	}

}

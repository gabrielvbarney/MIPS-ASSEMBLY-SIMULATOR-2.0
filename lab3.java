//Name: Gabriel Barney and Shreya Tumma
//Section: CPE 315-03
//Description: Two Pass Assembler, MIPS emulator

import java.io.*;
import java.util.*;

class lab3 {
    
    // starting address: 0x00400000, but for this assignment start at zero
    static Map<String, String> opCodes = new HashMap<String, String>() {{
        put("and", "000000");
        put("add", "000000"); 
        put("or", "000000");
        put("sll", "000000");
        put("sub", "000000");
        put("slt", "000000");
        put("jr", "000000");
        put("addi", "001000");
        put("beq", "000100");
        put("bne", "000101");
        put("lw", "100011");
        put("sw", "101011");
        put("j", "000010");
        put("jal", "000011"); 
    }};

    static Map<String, String> functionCodes = new HashMap<String, String>() {{
        put("and", "00000 100100");
        put("add", "00000 100000");
        put("or", "00000 100101");
        put("addi", "immediate");
        put("sll", "sa000000"); 
        //put("sll", "sa");
        put("sub", "00000 100010");
        put("slt", "00000 101010");
        put("beq", "offset");
        put("bne", "offset");
        put("lw", "offset");
        put("sw", "offset");
        put("j", "target");
        put("jr", "001000"); 
        put("jal", "target");
    }};

    static Map<String, String> regCodes = new HashMap<String, String>() {{
        put("0", "00000");
        put("zero", "00000");
        put("v0", "00010");
        put("v1", "00011");
        put("a0", "00100");
        put("a1", "00101");
        put("a2", "00110");
        put("a3", "00111");
        put("t0", "01000");
        put("t1", "01001");
        put("t2", "01010");
        put("t3", "01011");
        put("t4", "01100");
        put("t5", "01101");
        put("t6", "01110");
        put("t7", "01111");
        put("s0", "10000");
        put("s1", "10001");
        put("s2", "10010");
        put("s3", "10011");
        put("s4", "10100");
        put("s5", "10101");
        put("s6", "10110");
        put("s7", "10111");
        put("t8", "11000");
        put("t9", "11001");
        put("sp", "11101");
        put("ra", "11111");
    }};

    static int PC = 0;
    public static void main(String args[]) {
    
        Map<String, String> labels = new HashMap<String, String>();
        ArrayList<String> mCodes = new ArrayList<String>();
        int [] data_mem = new int [8192];
        int [] reg_file = new int [32];
        MIPSfuncs funcs = new MIPSfuncs();

        try {
            File file = new File(args[0]);
            FileReader fread = new FileReader(file);
            BufferedReader bread = new BufferedReader(fread);
            StringBuffer buff = new StringBuffer();
            String line;
            int hexAddress = -1;
            ArrayList< ArrayList<String>> instructions = new ArrayList<ArrayList<String> >();

            while ((line = bread.readLine()) != null) {
                hexAddress = getLabelAddresses(line, hexAddress, labels, instructions);
                buff.append(line);
                buff.append('\n'); 
            }
            fread.close();

            mCodes = makeMachineCode(labels, instructions, mCodes);
        
            if (args.length > 1) {
                // script
                File script = new File(args[1]);
                FileReader sread = new FileReader(script);
                BufferedReader sbread = new BufferedReader(sread);
            
                script_output(sbread, data_mem, reg_file, funcs, mCodes);
                sread.close();

            } else {
                int status = 1;
                Scanner command = new Scanner(System.in);
                int ret = 0;
                while (ret != -1) {
                    System.out.print("mips> ");
                    String cmd = command.nextLine();
                    ret = command_output(cmd, data_mem, reg_file, funcs, mCodes); 
                    if (ret == -2) {  
                        Arrays.fill(data_mem, 0);
                        Arrays.fill(reg_file, 0);
                        PC = 0;
                    }
                }
            }

            
            } catch(IOException e) { 
                e.printStackTrace(); 
            }
    }

    public static int command_output(String cmd, int[] data_mem, int[] reg_file, MIPSfuncs funcs, ArrayList<String> mCodes) {
        
        String[] splitLine;            
        splitLine = cmd.split(" ");
        int i = 0;
            
        switch(splitLine[0]) {
            case("h"):
                h();
                break;
            case("d"):
                d(reg_file);
                break;
            case("m"):
                m(data_mem, Integer.parseInt(splitLine[1]), Integer.parseInt(splitLine[2]));
                break;
            case("c"):
                c();
                return -2;
            case("r"):
                i = PC;
                while ( i < mCodes.size()){
                    parseMCode(mCodes, reg_file, data_mem, funcs);
                    i = PC;
                }
                return i;
            case("s"):
                if (splitLine.length > 1) {
                    i = Integer.parseInt(splitLine[1]);
                    while (i > 0) {
                        parseMCode(mCodes, reg_file, data_mem, funcs);
                        i--;
                    }
                    s(Integer.parseInt(splitLine[1]));
                    break;
                } else {
                    parseMCode(mCodes, reg_file, data_mem, funcs);
                    s(1);
                    break;
                }
            default:
                return -1;
            }
            return 0;
    }

    public static int script_output(BufferedReader sbread, int[] data_mem, int[] reg_file, MIPSfuncs funcs, ArrayList<String> mCodes) {
        String line;
        String[] splitLine;  
        int i = 0; 
        try {     
        while ((line = sbread.readLine()) != null) {
            splitLine = line.split(" ");
             
            if (splitLine.length == 1) {
                System.out.println("mips> " + splitLine[0]);
            } else if (splitLine.length == 2) {
                System.out.println("mips> " + splitLine[0] + " " + 
                                              splitLine[1]);
            } else { 
                System.out.println("mips> " + splitLine[0] + " " + 
                                              splitLine[1] + " " +
                                              splitLine[2]);
            }
    
            switch(splitLine[0]) {
                case("h"):
                    h(); 
                    break;
                case("d"):
                    d(reg_file);
                    break;
                case("m"):
                    m(data_mem, Integer.parseInt(splitLine[1]), Integer.parseInt(splitLine[2]));
                    break;
                case("c"):
                    PC = 0;
                    Arrays.fill(data_mem, 0);
                    Arrays.fill(reg_file, 0);
                    c();
                    break;
                case("r"):
                    i = PC;
                    while ( i < mCodes.size()){
                        parseMCode(mCodes, reg_file, data_mem, funcs);
                        i = PC;
                    }
                    break;
                case("s"):
                    if (splitLine.length > 1) { // probably have to work with pc value returned 
                        i = Integer.parseInt(splitLine[1]);
                        while (i > 0) {
                            parseMCode(mCodes, reg_file, data_mem, funcs);
                            i--;
                        }
                        s(Integer.parseInt(splitLine[1]));
                    } else {
                        parseMCode(mCodes, reg_file, data_mem, funcs);
                        s(1);
                    }
                    break;
                default:
                    return 0;
            }
        }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
}
    // method for first pass
    public static int getLabelAddresses(String line, int hexAddress, 
        Map<String, String> labels, ArrayList<ArrayList <String>> instructions) {
        
        int commentFlag = 0; 
        String[] splitLine = line.split("\\s+|,|\\$");
        ArrayList<String> instr = new ArrayList<String>();

        // Check for empty lines and comments
        if (splitLine != null || splitLine[0].contains("#") == false) {
            for(int i = 0; i < splitLine.length; i++){
                if (commentFlag == 1) { 
                    break;
                }
                // check for comments in same line as instruction                       
                if(splitLine[i].contains("#") == true) {
                    if (splitLine[i].indexOf("#") > 0) {
                        splitLine[i] = splitLine[i].substring(0, splitLine[i].indexOf("#"));
                        commentFlag = 1;
                    } else { 
                        break;
                    }
                }
                if(splitLine[i].length() > 0 ){
                    if (opCodes.containsKey(splitLine[i]) == true) {
                        hexAddress += 1; //add hex address as a value to key (?)
                    }
                    // check for labels and store address
                    int indexCol = splitLine[i].indexOf(":");
                    if(indexCol > -1){
                        labels.put(splitLine[i].substring(0,indexCol), Integer.toString(hexAddress + 1));
                        if(indexCol != splitLine[i].length() - 1){
                            hexAddress += 1;
                            instr.add(splitLine[i].substring(indexCol +1));
                        }
                        continue;
                    } 
                    instr.add(splitLine[i]);
                }
            }
            if(instr.isEmpty() == false){
                instructions.add(instr);
            }  
        }
        return hexAddress;
    }

    // method used for second pass
    public static ArrayList<String> makeMachineCode(Map<String, String> labels, 
        ArrayList<ArrayList <String>> instructions, ArrayList<String> mCodes) {
        
        // i is the address of the current instruction
        for(int i = 0; i < instructions.size(); i++){
            String mCode = "";
            ArrayList<String> line = instructions.get(i);
            String current_inst = line.get(0);
            
            switch (line.size()) {
                case 4:
                    if(opCodes.containsKey(current_inst) == true){
                        if(functionCodes.get(current_inst) == "immediate" || functionCodes.get(current_inst) == "offset" ) {
                            mCode += iType(line, current_inst, labels, i);
                        } else if(functionCodes.get(current_inst).contains("sa") == true) {
                            mCode += shift(line, current_inst);
                        } else {
                            mCode += rType(line, current_inst);
                        }
                    }
                    else {
                        System.out.println("invalid instruction: " + current_inst);
                        System.out.println("Exiting program...");
                        System.exit(0);
                        mCode += "invalid instruction: " + current_inst;
                        mCodes.add(mCode);
                        return mCodes;
                    }
                    break;
                case 2:
                    String destination = line.get(1);
                    if (current_inst.equals("j") || current_inst.equals("jal")) {   // think about storing pc with jal
                        String binary = Integer.toString(Integer.parseInt(labels.get(destination)), 2);
                        String leadZeroes = String.format("%26s", binary).replace(' ', '0');
                        mCode += opCodes.get(current_inst) + " " + leadZeroes;
                        break;
                    } else if (current_inst.equals("jr")) {
                        mCode += (opCodes.get(current_inst) + " " + 
                                    regCodes.get(destination) + " " +
                                    "000000000000000" + " " +
                                    functionCodes.get(current_inst));
                        break;        
                    } else {
                        System.out.println("invalid instruction: " + current_inst);
                        System.out.println("Exiting program...");
                        System.exit(0);
                        mCode += "invalid instruction: " + current_inst;
                        mCodes.add(mCode);
                        return mCodes;
                    }
                default:
                    System.out.println("invalid instruction: " + current_inst);
                    System.out.println("Exiting program...");
                    System.exit(0);
                    System.out.println("Instruction Error.");
                }    
                mCodes.add(mCode);  
            }
            return mCodes;
        }

        public static String rType(ArrayList<String> line, String current_inst){
            //ex: add rd, rs, rt           
            String rd = regCodes.get(line.get(1));
            String rs = regCodes.get(line.get(2));
            String rt = regCodes.get(line.get(3));
            String opcode = opCodes.get(current_inst);
            String fCode = functionCodes.get(current_inst);

            return opcode + " " + rs + " " + rt + " " + rd + " " + fCode;
        }

        public static String shift(ArrayList<String> line, String current_inst){
            //ex: add rd, rs, rt    
            String rd = regCodes.get(line.get(1));
            String rs = "00000";
            String rt = regCodes.get(line.get(2));
            String opcode = opCodes.get(current_inst);
            String fCode = "000000";
            String binary = Integer.toBinaryString(Integer.parseInt(line.get(3)));
            String shamt = String.format("%5s",binary ).replace(' ', '0');

            return opcode + " " + rs + " " + rt + " " + rd + " " + shamt + " "+ fCode;
        }

        public static String iType(ArrayList<String> line, String current_inst, Map<String, String> labels, int i){
            String rt = regCodes.get(line.get(1));
            String rs = regCodes.get(line.get(2));
            if(functionCodes.get(current_inst).equals("offset") == true){
                
                rs = regCodes.get(line.get(1));
                rt = regCodes.get(line.get(2));
            }
            String opcode = opCodes.get(current_inst);
            String offset = line.get(3);
            int newOff = 0;
                      
            //check to see if label exists 
            if(labels.containsKey(line.get(3)) == true) {
                newOff = Integer.parseInt(labels.get(offset)) - (i+1);
            } else if(offset.contains(")") == true){
                /*rt = regCodes.get(line.get(1));
                rs = regCodes.get(line.get(3).substring(0,(line.get(3).length())-1));*/

                rt = regCodes.get(line.get(1));
                rs = regCodes.get(line.get(3).substring(0,(line.get(3).length())-1));

                offset = line.get(2).substring(0, line.get(2).length()-1);
                newOff = Integer.parseInt(offset);
            } else if (current_inst.equals("bne")|| current_inst == "bne") {
                rs = regCodes.get(line.get(1));
                rt = regCodes.get(line.get(2));
            }
            else {
                newOff = Integer.parseInt(offset);
            }
            //check if offset is negative
            if(newOff < 0){
                offset = Integer.toString(twosCompliment(newOff));
            } else{
                offset = Integer.toString(newOff);
            }
                
            String binary = Integer.toBinaryString(Integer.parseInt(offset));
            String im = String.format("%16s", binary).replace(' ', '0');

            return opcode + " " + rs + " " + rt + " " + im ;
        }

        public static int twosCompliment(int offset){
            offset = offset * -1 ;
            int newOffset = (int)(Math.pow(2, 16)) - offset;
            return newOffset;
        }

        public static void parseMCode(ArrayList<String> mCodes, int [] reg_file, int [] data_mem, MIPSfuncs funcs){
            
            int ret = 0;  
            //handle error statements
            if(mCodes.get(PC).contains(":") == false){
                String[] splitLine = mCodes.get(PC).split(" ");
                
                if(splitLine[0].equals("000000")){
                    ret = rTypeFuncs(splitLine, funcs, reg_file);
                } else if(splitLine.length == 4){
                    ret = iTypeFuncs(splitLine, funcs, reg_file, data_mem);
                } else if(splitLine.length == 2){
                    ret = jTypeFuncs(splitLine, funcs, reg_file);
                }
            }        
            PC = PC + ret;
        }

        private static int jTypeFuncs(String [] splitline, MIPSfuncs funcs, int [] reg_file) {
            String opCode = splitline[0];
            int imm = 0;
            

            if(splitline[1].charAt(0) == '1'){
                imm = convert_args(splitline[3]);
            } 
            else {
                imm = Integer.parseInt(splitline[1], 2); 
            }
            switch(opCode){
                case "000010":
                    return funcs.j(imm, PC);
                case "000011":
                    return funcs.jal(reg_file,imm, PC);
                default:
                    System.out.println("Error in jTypeFuncs"+ Arrays.toString(splitline));
                    break;
            }
            System.out.println("--------------\n");

            return 1;
        }

        private static int iTypeFuncs(String [] splitline, MIPSfuncs funcs, int [] reg_file, int [] data_mem) {
            String opCode = splitline[0];
        
            int rs = Integer.parseInt(splitline[1], 2);
            int rt = Integer.parseInt(splitline[2], 2);
            int imm = 0;
            if(splitline[3].charAt(0) == '1'){
                imm = convert_args(splitline[3]);
            } 
            else {
                imm = Integer.parseInt(splitline[3], 2); 
            }
     
            switch(opCode){
                
                case "001000":
                    funcs.addi(reg_file, rs, rt, imm);
                    break;
                case "000100":
                    return funcs.beq(reg_file, rs, rt, imm, PC);
                    
                    //break;
                case "000101":
                    return funcs.bne(reg_file, rs, rt, imm, PC);
                    
                    //break;
                case "100011":
                    funcs.lw(reg_file, data_mem, rs, rt, imm);
                    break;
                case "101011":
                    funcs.sw(reg_file, data_mem, rs, rt, imm);
                    break;
                
                default:
                    System.out.println("Error in iTypeFuncs");
                    break;
            }

            return 1;
        }

        private static int rTypeFuncs(String [] splitline, MIPSfuncs funcs, int [] reg_file) {
            
            if(splitline.length == 4 && splitline[3].contains("001000")){
                
                return funcs.jr(reg_file, Integer.parseInt(splitline[1], 2), PC);


            }
            else {
                String funcCode = splitline[5];
               
                int rs = Integer.parseInt(splitline[1], 2);
                int rt = Integer.parseInt(splitline[2], 2);
                int rd = Integer.parseInt(splitline[3], 2);  

                
                switch(funcCode){
                    case "100100":
                        funcs.and(reg_file, rs, rt, rd);
                        break;
                    case "100000":
                        funcs.add(reg_file, rs, rt, rd);
                        break;
                    case "100101":
                        funcs.or(reg_file, rs, rt, rd);
                        break;
                    case "100010":
                        funcs.sub(reg_file, rs, rt, rd);
                        break;
                    case "101010":
                        funcs.slt(reg_file, rs, rt, rd);
                        break;
                    case "000000":
                        int shamt = Integer.parseInt(splitline[4], 2);
                        funcs.sll(reg_file,rt, rd, shamt);
                        break;
                    default:
                        System.out.print("Error in rTypeFuncs" + Arrays.toString(splitline));
                        break;
                }
            }
            return 1;
        }
      
        public static int convert_args(String split) {
            // source: Ivan D. on Stack Overflow 
            // link: https://stackoverflow.com/questions/14012013/java-converting-negative-binary-back-to-integer
            
            StringBuilder builder = new StringBuilder();
            int decimal = 0;
            int power = 0;
            for (int i = 0; i < split.length(); i++) {
                builder.append((split.charAt(i) == '1' ? '0' : '1'));
            }

            while (split.length() > 0) {
                int temp = Integer
                    .parseInt(builder.charAt((split.length()) - 1)+"");
                decimal += temp * Math.pow(2, power++);
                split = split.substring(0, split.length() - 1);
            }

            return ((decimal + 1) * (-1));
        }


        public static void h() {
            System.out.println("\nh = show help");
            System.out.println("d = dump register state");
            System.out.println("s = single step through the program (i.e. execute 1 instruction and stop)");
            System.out.println("s num = step through num instructions of the program");
            System.out.println("r = run until the program ends");
            System.out.println("m num1 num2 = display data memory from location num1 to num2");
            System.out.println("c = clear all registers, memory, and the program counter to 0");
            System.out.println("q = exit the program\n");
        }

        public static void d(int[] reg_file) {   
            System.out.println("\npc = " + PC);
            System.out.println("$0 = " + reg_file[0] + "\t\t" +
                               "$v0 = " + reg_file[2] + "\t\t" +
                                "$v1 = " + reg_file[3] + "\t\t" +
                                "$a0 = " + reg_file[4] + "\t\t");
            System.out.println("$a1 = " + reg_file[5] + "\t\t" +
                               "$a2 = " + reg_file[6] + "\t\t" +
                                "$a3 = " + reg_file[7] + "\t\t" +
                                "$t0 = " + reg_file[8] + "\t\t");
            System.out.println("$t1 = " + reg_file[9] + "\t\t" +
                               "$t2 = " + reg_file[10] + "\t\t" +
                                "$t3 = " + reg_file[11] + "\t\t" +
                                "$t4 = " + reg_file[12] + "\t\t");
            System.out.println("$t5 = " + reg_file[13] + "\t\t" +
                               "$t6 = " + reg_file[14] + "\t\t" +
                                "$t7 = " + reg_file[15] + "\t\t" +
                                "$s0 = " + reg_file[16] + "\t\t");
            System.out.println("$s1 = " + reg_file[17] + "\t\t" +
                               "$s2 = " + reg_file[18] + "\t\t" +
                                "$s3 = " + reg_file[19] + "\t\t" +
                                "$s4 = " + reg_file[20] + "\t\t");
            System.out.println("$s5 = " + reg_file[21] + "\t\t" +
                               "$s6 = " + reg_file[22]+ "\t\t" +
                                "$s7 = " + reg_file[23] + "\t\t" +
                                "$t8 = " + reg_file[24] + "\t\t");
            System.out.println("$t9 = " + reg_file[25] + "\t\t" +
                               "$sp = " + reg_file[29] + "\t\t" +
                                "$ra = " + reg_file[31] + "\t\t\n");
        }        
    
        public static void m(int[] data_mem, int idx1, int idx2) {
            System.out.print("\n");
            for (int i = idx1; i <= idx2; i++) {
                System.out.println("[" + i + "] = " + data_mem[i]);
            }
            System.out.print("\n");
        }

        public static void c() {
            System.out.println("\tSimulator reset\n");
        }



        public static void s(int steps) { 
            System.out.println("\t" + steps + " instruction(s) executed");
        } 


}  

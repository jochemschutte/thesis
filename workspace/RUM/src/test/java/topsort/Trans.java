package topsort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

public class Trans{
	
	@Ignore
	@Test
	public void run() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(new File("log/tmp.txt")));
		List<String> lines = new LinkedList<>();
		for(String line = in.readLine(); line != null; line = in.readLine()) {
			if(line.contains("new ResourceFunction")) {
				String[] funcParts = line.split("new ResourceFunction\\(\"");
				String func = funcParts[1];
				func = func.substring(0, func.length()-4);
				String content = null;
				try {
					Double.parseDouble(func);
					content = func;
				}catch(NumberFormatException e) {
					String[] vars = func.split("\\+");
					String[] indexes = new String[vars.length];
					for(int i = 0; i < vars.length; i++) {
						indexes[i] = String.format("x[%d]", i);
					}
					content = String.join("+", indexes);
					for(String var : vars) {
						content += String.format(", \"%s\"", var);
					}
				}
				line = String.format("%snew ResourceFunction(x->%s));", funcParts[0], content);
			}
			lines.add(line);
		}
		in.close();
		System.out.println(String.join("\n", lines));
	}
	
	@Test
	public void run2() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(new File("log/tmp.txt")));
		String rpmName = null;
		List<String> rpms = new LinkedList<>();
		List<String> resources = new LinkedList<>();
		List<String> functions = new LinkedList<>();
		String tabs = "\t\t";
		int index = 1;
		for(String line = in.readLine(); line != null; line = in.readLine()) {
			if(line.contains("new RPM")) {
				if(rpmName != null) {
					functions = functions.stream().map(f->String.format("new ResourceFunction(%s)", f)).collect(Collectors.toList());
					rpms.add(String.format("rpm%d.add(\"%s\", %s);", index, rpmName, String.join(", ", functions)));
					functions = new LinkedList<>();
				}
				rpmName = line.split("RPM ")[1].split(" ")[0];
				resources = new LinkedList<>();
			}else if(line.contains("getResourceFunctions")){
				resources.add(line.split("put\\(")[1].split(",")[0]);
				String function = line.split("new ResourceFunction\\(")[1].split("\\)")[0];
				String[] parts = function.split("->");

				if(parts.length == 2) {
					try {
						function = Double.toString(Double.parseDouble(parts[1]));
					}catch(NumberFormatException e) {}

					if(function.contains("x->x[0],")) {
						function = String.format("\"%s\"", function.split("\"")[1]);
					}
				}
				functions.add(function);
			}else if(line.contains("//")){
				functions = functions.stream().map(f->String.format("new ResourceFunction(%s)", f)).collect(Collectors.toList());
				rpms.add(String.format("rpm%d.add(\"%s\", %s);", index, rpmName, String.join(", ", functions)));
				functions = new LinkedList<>();
				
				System.out.println(String.format("%sRpmBuilder rpm%d = b.rpmBuilder(m%d, %s);", //
						tabs, index, index, String.join(", ", resources)));
				rpms.forEach(f->System.out.println(tabs + f));
				System.out.println();
				functions = new LinkedList<>();
				resources = new LinkedList<>();
				rpms = new LinkedList<>();
				rpmName = null;
				index++;
			}
		}
		in.close();
	}
}
package tangent65536.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import tangent65536.obfuscator.matlab.CodeBlock;
import tangent65536.obfuscator.matlab.CodeParser;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		final File workspace = new File("C:\\Users\\User\\Desktop\\test");
		final String[] files = {"IiIIiiiII", "clearfunc", "importfunc"};
		
		for(String file : files)
		{
			BufferedReader reader = new BufferedReader(new FileReader(new File(workspace, file + ".m")));
			CodeBlock codes = CodeParser.parseFile(reader, true);
			FileWriter writer = new FileWriter(new File(workspace, "obf_" + file + ".m"));
			writer.write(codes.transformPlainScript().toString());
			writer.close();
			reader.close();
		}
	}
}

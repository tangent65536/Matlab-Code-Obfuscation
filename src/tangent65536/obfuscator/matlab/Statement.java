package tangent65536.obfuscator.matlab;

import java.util.HashMap;

public class Statement
{
	public final String statement;
	public final Type type;
	
	private Statement(String line, Type typ)
	{
		this.statement = line;
		this.type = typ;
	}
	
	public static Statement parseStatement(String line)
	{
		line = line.trim();
		return new Statement(line, Type.checkType(line));
	}
	
	@Override
	public String toString()
	{
		return this.statement;
	}
	
	public static enum Type
	{
		IF("if"),
		ELSEIF("elseif"),
		ELSE("else"),
		WHILE("while"),
		FOR("for"),
		TRY("try"),
		CATCH("catch"),
		FUNCTION("function"),
		CLASSDEF("classdef"),
		CLASS_PROPERTIES("properties"),
		CLASS_METHODS("methods"),
		END("end"),
		CONTINUE("continue;"),
		BREAK("break;"),
		SWITCH("switch"),
		CASE("case"),
		DEFAULT(null);
		
		private final static HashMap<String, Type> MAPPING = new HashMap<>();
		static
		{
			for(Type cs : Type.values())
			{
				MAPPING.put(cs.key, cs);
			}
		}
		
		private final String key;
		
		private Type(String _key)
		{
			this.key = _key;
		}
		
		private static Type checkType(String line)
		{
			String cache = line.split("[\\s\\(\\[]")[0];
			Type ret = MAPPING.get(cache);
			if(ret == null)
			{
				ret = DEFAULT;
			}
			return ret;
		}
		
		public String toString()
		{
			return this.key;
		}
	}
}
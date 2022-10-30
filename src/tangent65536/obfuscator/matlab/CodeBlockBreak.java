package tangent65536.obfuscator.matlab;

public class CodeBlockBreak extends CodeBlockTransformedBlank
{
	protected final Statement.Type type;
	
	public CodeBlockBreak(Statement.Type type)
	{
		this.type = type;
	}
	
	@Override
	protected String toString(int level)
	{
		// return getIndent(level) + this.type + "\r\n";
		return "";
	}
}

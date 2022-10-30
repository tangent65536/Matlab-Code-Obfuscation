package tangent65536.obfuscator.matlab;

public class AbstractCodeBlockClassMembers extends AbstractCodeBlockTransformedControl
{
	public AbstractCodeBlockClassMembers(Statement clause, Statement.Type check)
	{
		super(clause, check);
	}
	
	protected String toString(int level)
	{
		String indent = getIndent(level);
		
		String ret = indent + this.control + "\r\n";
		ret += super.toString(level + 1);
		ret += indent + "end\r\n";
		
		return ret;
	}
}

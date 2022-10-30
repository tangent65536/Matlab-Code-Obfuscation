package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockWhile extends AbstractCodeBlockControl implements ILoopBlock
{
	private final CodeBlockPseudoLoopback pseudoBlock = new CodeBlockPseudoLoopback(this);
	
	public CodeBlockWhile(Statement clause)
	{
		super(clause, Statement.Type.WHILE);
	}
	
	protected String toString(int level)
	{
		String indent = getIndent(level);
		
		String ret = indent + this.control + "\r\n";
		ret += super.toString(level + 1);
		ret += indent + "end\r\n";
		
		return ret;
	}

	@Override
	public CodeBlockPseudoLoopback getLoopbackBlock()
	{
		return this.pseudoBlock;
	}
	
	protected IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		CodeBlockTransformedIf ret = new CodeBlockTransformedIf(Statement.parseStatement("if" + this.control.statement.substring(5)), super.forceTransform(outputs, traversed), this.next == null ? null : this.next.transform(outputs, traversed));
		this.pseudoBlock.setTransformedLoopbackPtr(ret);
		
		if(!this.leading)
		{
			ret.markNotLeading();
		}
		
		return ret;
	}
	
	public int getLinesCount()
	{
		return super.getLinesCount() + 3;
	}
}

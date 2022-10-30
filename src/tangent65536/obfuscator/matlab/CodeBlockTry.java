package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockTry extends AbstractCodeBlockControl implements IBranchingBlock
{
	private CodeBlockCatch catchBlock = null;
	
	public CodeBlockTry(Statement clause)
	{
		super(clause, Statement.Type.TRY);
	}

	public void setCatch(CodeBlockCatch ptr)
	{
		this.catchBlock = ptr;
	}
	
	@Override
	public AbstractCodeBlock getBranch()
	{
		return this.catchBlock;
	}

	protected String toString(int level)
	{
		String indent = getIndent(level);
		
		String ret = indent + this.control + "\r\n";
		ret += super.toString(level + 1);
		
		if(this.catchBlock != null)
		{
			ret += indent + this.catchBlock.control + "\r\n";
			ret += this.catchBlock.toString(level + 1);
		}
		
		ret += indent + "end\r\n";
		
		return ret;
	}
	
	protected IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		IIndexableBlock _catchPtr = this.catchBlock == null ? null : this.catchBlock.transform(outputs, traversed);
		CodeBlockTransformedTry ret = new CodeBlockTransformedTry(_catchPtr, this.catchBlock.control);
		ret.addChildren(this.children);
		ret.setNextBlock(this.next);
		return ret;
	}
	
	public int getLinesCount()
	{
		return super.getLinesCount() + 3;
	}
}

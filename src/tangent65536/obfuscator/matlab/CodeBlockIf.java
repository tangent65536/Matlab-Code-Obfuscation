package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockIf extends AbstractCodeBlockControl implements IBranchingBlock
{
	private static final Statement.Type[] VALID_TYPES = {Statement.Type.IF, Statement.Type.ELSEIF};
	
	private AbstractCodeBlock elseBlock = null;
	
	/**
	 * @param clause The corresponding "if" or "elseif" clause
	 */
	public CodeBlockIf(Statement clause)
	{
		super(clause, VALID_TYPES);
	}
	
	public void setElse(AbstractCodeBlock ptr)
	{
		this.elseBlock = ptr;
	}

	@Override
	public AbstractCodeBlock getBranch()
	{
		return this.elseBlock;
	}
	
	protected String toString(int level)
	{
		String indent = getIndent(level);
		
		String ret = indent + this.control.statement.substring(this.control.type == Statement.Type.ELSEIF ? 4 : 0) + "\r\n";
		ret += super.toString(level + 1);
		
		if(this.elseBlock != null)
		{
			ret += indent + "else\r\n";
			ret += this.elseBlock.toString(level + 1);
		}
		
		ret += indent + "end\r\n";
		
		return ret;
	}
	
	protected IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		IIndexableBlock _ifPtr = super.forceTransform(outputs, traversed);
		IIndexableBlock _elsePtr = this.elseBlock == null ? (this.next == null ? null : this.next.transform(outputs, traversed)) : this.elseBlock.transform(outputs, traversed);
		Statement newIf = Statement.parseStatement(this.control.statement.substring(this.control.type == Statement.Type.ELSEIF ? 4 : 0));
		return new CodeBlockTransformedIf(newIf, _ifPtr, _elsePtr);
	}
	
	public int getLinesCount()
	{
		return super.getLinesCount() + 3;
	}
}

package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Tangent65536
 *
 * A transformed/obfuscated "if" statement handler that manages if/for/while statements.
 *  The "next" pointer in this type of blocks should never be used!
 */
public class CodeBlockTransformedIf extends AbstractCodeBlock implements IIndexableBlock
{
	private int id = INVALID_ID;
	
	private final Statement control;
	private final IIndexableBlock ifJump;
	private final IIndexableBlock elseJump;
	
	public CodeBlockTransformedIf(Statement ctrl, IIndexableBlock _if, IIndexableBlock _else)
	{
		this.control = ctrl;
		this.ifJump = _if;
		this.elseJump = _else;
		
		((AbstractCodeBlock)_if).markNotLeading();
		if(_else != null)
		{
			((AbstractCodeBlock)_else).markNotLeading();
		}
	}
	
	@Override
	protected String toString(int level)
	{
		return "";
	}

	@Override
	protected IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		return this;
	}
	
	@Override
	public void setId(HashSet<Integer> used)
	{
		if(this.id == INVALID_ID)
		{
			this.id = getUniqueU16(used);
		}
	}

	@Override
	public int getId()
	{
		return this.id;
	}

	@Override
	public void updateChain(HashSet<IIndexableBlock> blocks, HashSet<Integer> used)
	{
		if(!blocks.contains(this.ifJump))
		{
			this.ifJump.setId(used);
			blocks.add(this.ifJump);
		}
		if(this.elseJump != null && !blocks.contains(this.elseJump))
		{
			this.elseJump.setId(used);
			blocks.add(this.elseJump);
		}
	}

	@Override
	public String transformedNextString(int level, String loopVarName)
	{
		String indent = getIndent(level);
		String indent_1 = getIndent(level + 1);
		
		String ret = "";
		ret += indent + this.control + "\r\n";
		ret += indent_1 + loopVarName + " = " + this.ifJump.getId() + ";\r\n";
		ret += indent_1 + "continue;\r\n";
		ret += indent + "end\r\n";
		ret += indent + loopVarName + " = " + (this.elseJump == null ? INVALID_ID : this.elseJump.getId()) + ";\r\n";
		
		return ret;
	}
	
	public int getLinesCount()
	{
		return 5;
	}
}

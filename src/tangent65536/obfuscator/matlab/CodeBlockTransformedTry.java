package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockTransformedTry extends CodeBlock implements IIndexableBlock
{
	private int id = INVALID_ID;
	
	private final IIndexableBlock catchJump;
	private final Statement catchStatement;
	
	public CodeBlockTransformedTry(IIndexableBlock _catch, Statement caught)
	{
		this.catchJump = _catch;

		if(_catch != null)
		{
			((AbstractCodeBlock)_catch).markNotLeading();
		}
		
		this.catchStatement = caught;
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
		if(this.catchJump != null && !blocks.contains(this.catchJump))
		{
			this.catchJump.setId(used);
			blocks.add(this.catchJump);
		}
		if(this.next != null)
		{
			IIndexableBlock _next = this.next.getLastTransformed();
			if(!blocks.contains(_next))
			{
				_next.setId(used);
				blocks.add(_next);
			}
		}
	}

	@Override
	public String transformedNextString(int level, String loopVarName)
	{
		String indent = getIndent(level);
		String indent_1 = getIndent(level + 1);
		
		String ret = indent + "try\r\n";
		ret += super.toString(level + 1);
		
		if(this.catchJump != null)
		{
			ret += indent_1 + loopVarName + " = " + (this.next == null ? INVALID_ID : this.next.getLastTransformed().getId()) + ";\r\n";
			ret += indent + this.catchStatement + "\r\n";
			ret += indent_1 + loopVarName + " = " + this.catchJump.getId() + ";\r\n";
			ret += indent + "end\r\n";
		}
		else
		{
			ret += indent + "end\r\n";
			ret += indent + loopVarName + " = " + (this.next == null ? INVALID_ID : this.next.getLastTransformed().getId()) + ";\r\n";
		}
		
		return ret;
	}
}

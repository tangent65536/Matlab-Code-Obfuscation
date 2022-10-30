package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockTransformedBlank extends AbstractCodeBlock implements IIndexableBlock
{
	private int id = INVALID_ID;
	
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
	protected String toString(int level)
	{
		return "";
	}
	
	@Override
	public String transformedNextString(int level, String loopVarName)
	{
		return getIndent(level) + loopVarName + " = " + (this.next == null ? INVALID_ID : this.next.getLastTransformed().getId()) + ";\r\n";
	}

	@Override
	protected IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		return this;
	}

	@Override
	public void updateChain(HashSet<IIndexableBlock> blocks, HashSet<Integer> used)
	{
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
	
	public boolean finalized(HashSet<AbstractCodeBlock> traversed)
	{
		return traversed.contains(this);
	}
	
	public int getLinesCount()
	{
		return 2;
	}
}

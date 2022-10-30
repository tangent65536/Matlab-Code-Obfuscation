package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class AbstractCodeBlockTransformed extends CodeBlock implements IIndexableBlock
{
	@Override
	public void setId(HashSet<Integer> used){}

	@Override
	/**
	 * This is a dispatcher block => return error!
	 */
	public int getId()
	{
		System.err.println("Calling get ID on a dispatcher/definition block!");
		return INVALID_ID;
	}

	@Override
	protected IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		return this;
	}

	@Override
	public void updateChain(HashSet<IIndexableBlock> blocks, HashSet<Integer> used)
	{
		System.err.println("Calling update chain on a dispatcher/definition block!");
	}

	@Override
	public String transformedNextString(int level, String loopVarName)
	{
		return "";
	}
	
	public void addTransformedChildren(Collection<IIndexableBlock> list)
	{
		HashSet<AbstractCodeBlock> check = new HashSet<>();
		for(IIndexableBlock block : list)
		{
			AbstractCodeBlock ptr = (AbstractCodeBlock)block;
			if(!check.contains(ptr))
			{
				this.children.add(ptr);
				check.add(ptr);
			}
		}
	}
}

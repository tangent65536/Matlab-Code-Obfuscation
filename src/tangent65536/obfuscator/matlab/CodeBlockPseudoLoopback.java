package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockPseudoLoopback extends AbstractCodeBlock implements IIndexableBlock, IDelegateBlock
{
	private IIndexableBlock loopbackPtr = null;
	
	protected final AbstractCodeBlock loopParent;
	
	protected CodeBlockPseudoLoopback(ILoopBlock loop)
	{
		this.loopParent = (AbstractCodeBlock)loop;
	}
	
	protected void setTransformedLoopbackPtr(IIndexableBlock block)
	{
		this.loopbackPtr = block;
	}
	
	@Override
	protected String toString(int level)
	{
		return "";
	}

	@Override
	protected IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		return this.getLastTransformed();
	}
	
	public IIndexableBlock getLastTransformed()
	{
		return this.loopbackPtr == null ? this : this.loopbackPtr;
	}

	@Override
	public void setId(HashSet<Integer> used)
	{
		this.loopbackPtr.setId(used);
	}

	@Override
	public int getId()
	{
		return this.loopbackPtr.getId();
	}

	@Override
	public void updateChain(HashSet<IIndexableBlock> blocks, HashSet<Integer> used)
	{
		this.loopbackPtr.updateChain(blocks, used);
	}

	@Override
	public String transformedNextString(int level, String loopVarName)
	{
		return this.loopbackPtr.transformedNextString(level, loopVarName);
	}

	@Override
	public IIndexableBlock getEntity()
	{
		return this.loopbackPtr;
	}
	
	public int getLinesCount()
	{
		return this.loopParent.getLinesCount();
	}
}
